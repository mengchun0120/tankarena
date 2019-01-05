package com.crazygame.tankarena.gameobj;

import android.content.Context;
import android.util.Log;

import com.crazygame.tankarena.GameView;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Map {
    private final float blockBreath = 100f;
    private final int numBlocksX = 12;
    private final int numBlocksY;
    public final float width = numBlocksX * blockBreath;
    public final float height;
    private final MapItem[][] items;
    private final MapItemPool mapItemPool = new MapItemPool(1000);
    private final GameView gameView;
    private final float[] viewportOrigin = {0f, 0f};
    private final float[] screenCenter = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

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

            screenCenter[0] = width / 2f;
            screenCenter[1] = gameView.viewportSize[1] / 2f;
            Log.d("map", "s0=" + screenCenter[0] + " s1=" + screenCenter[1]);

            items = new MapItem[numBlocksY][numBlocksX];

            while((line = reader.readLine()) != null) {
                tokenizer = new StringTokenizer(line);

                String type = tokenizer.nextToken();
                if(type.equals("tile")) {
                    float x = Float.parseFloat(tokenizer.nextToken());
                    float y = Float.parseFloat(tokenizer.nextToken());
                    Tile tile = new Tile(x, y);
                    Log.d("map", "t=" + (x-viewportOrigin[0]-screenCenter[0]));
                    addObject(tile);
                }
            }

        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setViewportOrigin(viewportOrigin, screenCenter);

        int startRow = (int)Math.floor(viewportOrigin[1] / blockBreath);
        int endRow = (int)Math.floor((viewportOrigin[1] + gameView.viewportSize[1]) / blockBreath);

        clearForDraw(startRow, endRow);
        for(int row = startRow; row <= endRow; ++row) {
            for(int col = 0; col < numBlocksX; ++col) {
                for(MapItem item = items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;
                    if(!obj.drawn) {
                        obj.draw(simpleShaderProgram);
                        obj.drawn = true;
                    }
                }
            }
        }
    }

    public void addObject(GameObject obj) {
        int startRow = crampRow((int)Math.floor(obj.bottomBound() / blockBreath));
        int endRow = crampRow((int)Math.floor(obj.topBound() / blockBreath));
        int startCol = crampCol((int)Math.floor(obj.leftBound() / blockBreath));
        int endCol = crampCol((int)Math.floor(obj.rightBound() / blockBreath));

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

    private void clearForDraw(int startRow, int endRow) {
        for(int row = startRow; row <= endRow; ++row) {
            for(int col = 0; col < numBlocksX; ++col) {
                for(MapItem item = items[row][col]; item != null; item = item.next) {
                    item.gameObject.drawn = false;
                }
            }
        }
    }
}
