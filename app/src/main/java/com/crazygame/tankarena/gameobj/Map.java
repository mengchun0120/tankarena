package com.crazygame.tankarena.gameobj;

import android.util.Log;

import com.crazygame.tankarena.GameView;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    public final float blockBreath = 100f;
    private final int numBlocksX = 12;
    private final int numBlocksY;
    public final float width = numBlocksX * blockBreath;
    public final float height;
    public final MapItem[][] items;
    private final MapItemPool mapItemPool = new MapItemPool(1000);
    private final GameView gameView;
    private final float[] viewportOrigin = {0f, 0f};
    private final float[] screenCenter = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final float maxViewportOriginY;
    private Tank player;

    public Map(GameView gameView, int resourceId) {
        this.gameView = gameView;

        try {
            InputStream inputStream = gameView.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            numBlocksY = Integer.parseInt(tokenizer.nextToken());
            height = numBlocksY * blockBreath;
            maxViewportOriginY = height - gameView.viewportSize[1];

            screenCenter[0] = width / 2f;
            screenCenter[1] = gameView.viewportSize[1] / 2f;

            items = new MapItem[numBlocksY][numBlocksX];

            while((line = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);

                String type = tokenizer.nextToken();
                if(type.equals("tile")) {
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());

                    Tile tile = new Tile(x, y);
                    addObject(tile);

                } else if(type.equals("tank")) {
                    int side = Integer.parseInt(tokenizer.nextToken());
                    int direction = Integer.parseInt(tokenizer.nextToken());
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());

                    Tank tank = new Tank(side, direction, x, y);

                    if(side == 0) {
                        player = tank;
                    }

                    addObject(tank);
                }
            }

        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setViewportOrigin(viewportOrigin, screenCenter);

        int startRow = getRow(viewportOrigin[1]);
        int endRow = crampRow(getRow(viewportOrigin[1] + gameView.viewportSize[1]));

        clearFlags(startRow, endRow, 0, numBlocksX-1, 0);
        for(int row = startRow; row <= endRow; ++row) {
            for(int col = 0; col < numBlocksX; ++col) {
                for(MapItem item = items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;
                    if((obj.flag & GameObject.FLAG_DRAWN) == 0) {
                        obj.draw(simpleShaderProgram);
                    }
                }
            }
        }
    }

    public void move(GameObject obj, int oldBottomRow, int oldTopRow, int oldLeftCol,
                     int oldRightCol, int newBottomRow, int newTopRow, int newLeftCol,
                     int newRightCol) {

        removeObjectFromOldRegion(obj, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol,
                newBottomRow, newTopRow, newLeftCol, newRightCol);

        addObjectToNewRegion(obj, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol,
                newBottomRow, newTopRow, newLeftCol, newRightCol);
    }

    public void updatePlayer(int direction, boolean firing) {
        player.setDirection(direction);
        player.firing = firing;
    }

    public void update(float timeDelta) {
        player.update(this, timeDelta);
        updateViewportOrigin();

        int bottomRow = getRow(viewportOrigin[1]);
        int topRow = crampRow(getRow(viewportOrigin[1] + gameView.viewportSize[1]));

        clearFlags(bottomRow, topRow, 0, numBlocksX-1, 0);
        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = 0; col < numBlocksX; ++col) {
                MapItem item = items[row][col];
                while(item != null) {
                    GameObject obj = item.gameObject;

                    if ((obj.flag & GameObject.FLAG_UPDATED) == 0 && obj != player) {
                        if (obj instanceof Tank) {
                            Tank tank = (Tank) obj;
                            tank.update(this, timeDelta);
                        } else if(obj instanceof Bullet) {
                            Bullet bullet = (Bullet) obj;
                            bullet.update(this, timeDelta);
                        }
                    }

                    if((obj.flag & GameObject.FLAG_DELETED) != 0) {
                        MapItem next = item.next;
                        removeObject(obj);
                        item = next;
                    } else {
                        item = item.next;
                    }
                }
            }
        }
    }

    public void addObject(GameObject obj) {
        int startRow = crampRow(getRow(obj.bottomBound()));
        int endRow = crampRow(getRow(obj.topBound()));
        int startCol = crampCol(getCol(obj.leftBound()));
        int endCol = crampCol(getCol(obj.rightBound()));

        for(int row = startRow; row <= endRow; ++row) {
            for(int col = startCol; col <= endCol; ++col) {
                addObject(obj, row, col);
            }
        }
    }

    public void addObject(GameObject obj, int row, int col) {
        MapItem item = mapItemPool.alloc(obj);
        item.next = items[row][col];
        items[row][col] = item;
    }

    public void removeObject(GameObject obj, int row, int col) {
        MapItem prev = null, item;

        for(item = items[row][col]; item != null; item = item.next) {
            if(item.gameObject == obj) {
                break;
            }
            prev = item;
        }

        if(item != null) {
            if(prev != null) {
                prev.next = item.next;
            } else {
                items[row][col] = item.next;
            }
            mapItemPool.free(item);
        }
    }

    public void removeObject(GameObject obj) {
        int bottomRow = crampRow(getRow(obj.bottomBound()));
        int topRow = crampRow(getRow(obj.topBound()));
        int leftCol = crampCol(getCol(obj.leftBound()));
        int rightCol = crampCol(getCol(obj.rightBound()));

        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = leftCol; col <= rightCol; ++col) {
                removeObject(obj, row, col);
            }
        }
    }

    public int getRow(float y) {
        return (int)Math.floor(y / blockBreath);
    }

    public int getCol(float x) {
        return (int)Math.floor(x / blockBreath);
    }

    public int crampRow(int row) {
        if(row < 0) {
            return 0;
        }

        if(row >= numBlocksY) {
            return numBlocksY-1;
        }

        return row;
    }

    public int crampCol(int col) {
        if(col < 0) {
            return 0;
        }

        if(col >= numBlocksX) {
            return numBlocksX-1;
        }

        return col;
    }

    public void clearFlags(int bottomRow, int topRow, int leftCol, int rightCol, int flagMask) {
        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = leftCol; col <= rightCol; ++col) {
                for(MapItem item = items[row][col]; item != null; item = item.next) {
                    item.gameObject.flag &= flagMask;
                }
            }
        }
    }

    private void updateViewportOrigin() {
        float newViewportY = player.position[1] - gameView.viewportSize[1]/2f;
        if(newViewportY < 0f) {
            newViewportY = 0f;
        } else if(newViewportY > maxViewportOriginY) {
            newViewportY = maxViewportOriginY;
        }
        viewportOrigin[1] = newViewportY;
    }

    private void removeObjectFromOldRegion(GameObject obj, int oldBottomRow, int oldTopRow,
                                     int oldLeftCol, int oldRightCol, int newBottomRow,
                                     int newTopRow, int newLeftCol, int newRightCol) {

        for(int row = oldBottomRow; row <= oldTopRow; ++row) {
            for(int col = oldLeftCol; col <= oldRightCol; ++col) {
                if(!coveredByRegion(row, col, newBottomRow, newTopRow, newLeftCol, newRightCol)) {
                    removeObject(obj, row, col);
                }
            }
        }
    }

    private void addObjectToNewRegion(GameObject obj, int oldBottomRow, int oldTopRow,
                                     int oldLeftCol, int oldRightCol, int newBottomRow,
                                     int newTopRow, int newLeftCol, int newRightCol) {

        for(int row = newBottomRow; row <= newTopRow; ++row) {
            for(int col = newLeftCol; col <= newRightCol; ++col) {
                if(!coveredByRegion(row, col, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol)) {
                    addObject(obj, row, col);
                }
            }
        }
    }

    private boolean coveredByRegion(int row, int col, int bottomRow, int topRow,
                                         int leftCol, int rightCol) {
        return row >= bottomRow && row <= topRow && col >= leftCol && col <= rightCol;
    }
}
