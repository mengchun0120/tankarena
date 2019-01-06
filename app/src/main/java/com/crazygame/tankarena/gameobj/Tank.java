package com.crazygame.tankarena.gameobj;

import android.util.Log;

import com.crazygame.tankarena.controllers.DriveWheel;
import com.crazygame.tankarena.data.Constants;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Tank extends GameObject {
    public final static TankTemplate template = new TankTemplate();
    private int direction;
    private boolean moving = false;
    private int side;
    public boolean firing = false;
    private float moveSpeed = 200f;

    public Tank(int side, int direction, float x, float y) {
        this.side = side;
        this.direction = direction;
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, side, position, direction);
    }

    @Override
    public float leftBound() {
        return position[0] - template.halfBreath;
    }

    @Override
    public float rightBound() {
        return position[0] + template.halfBreath;
    }

    @Override
    public float topBound() {
        return position[1] + template.halfBreath;
    }

    @Override
    public float bottomBound() {
        return position[1] - template.halfBreath;
    }

    @Override
    public float leftCollisionBound() {
        return position[0] - template.halfBreath;
    }

    @Override
    public float rightCollisionBound() {
        return position[0] + template.halfBreath;
    }

    @Override
    public float topCollisionBound() {
        return position[1] + template.halfBreath;
    }

    @Override
    public float bottomCollisionBound() {
        return position[1] - template.halfBreath;
    }

    public void setDirection(int direction) {
        if(direction == DriveWheel.NOT_MOVE) {
            moving = false;
        } else {
            moving = true;
            this.direction = direction;
        }
    }

    public void update(Map map, float timeDelta) {
        if(moving) {
            move(map, timeDelta);
        }
    }

    private void move(Map map, float timeDelta) {
        final float moveDistance = moveSpeed * timeDelta;

        MapRegion oldRegion = MapRegion.regionPool[0];
        getMapRegion(map, oldRegion);

        position[0] = crampX(map,
                position[0] + moveDistance * template.rotateDirection[direction][0]);
        position[1] = crampY(map,
                position[1] + moveDistance * template.rotateDirection[direction][1]);

        MapRegion newRegion = MapRegion.regionPool[1];
        getMapRegion(map, newRegion);

        checkCollision(map, newRegion);

        map.move(this, oldRegion, newRegion);
    }

    private float crampX(Map map, float x) {
        if(x < template.halfBreath) {
            return template.halfBreath;
        }

        float maxRightBound = map.width - template.halfBreath;
        if(x > maxRightBound) {
            return maxRightBound;
        }

        return x;
    }

    private float crampY(Map map, float y) {
        if(y < template.halfBreath) {
            return template.halfBreath;
        }

        float maxTopBound = map.height - template.halfBreath;
        if(y > maxTopBound) {
            return maxTopBound;
        }

        return y;
    }

    private void getMapRegion(Map map, MapRegion region) {
        region.update(map, leftBound(), rightBound(), topBound(), bottomBound());
    }

    private void checkCollision(Map map, MapRegion region) {
        boolean collide = false;
        float maxAdjustY = -1e9f, maxAdjustX = -1e9f;
        
        map.clearFlags(region.startRow, region.endRow, region.startCol, region.endCol);
        for(int row = region.startRow; row <= region.endRow; ++row) {
            for(int col = region.startCol; col <= region.endCol; ++col) {
                for(MapItem item = map.items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;
                    if(obj.flag || obj == this) {
                        continue;
                    }

                    if((obj instanceof Tank) || (obj instanceof Tile)) {
                        float leftAdjustX = obj.rightCollisionBound() - leftCollisionBound();
                        float rightAdjustX = rightCollisionBound() - obj.leftCollisionBound();
                        float downAdjustY = obj.topCollisionBound() - bottomCollisionBound();
                        float upAdjustY =  topCollisionBound() - obj.bottomCollisionBound();

                        if(leftAdjustX > Constants.CLOSE_TO_ZERO &&
                           rightAdjustX > Constants.CLOSE_TO_ZERO &&
                           downAdjustY > Constants.CLOSE_TO_ZERO &&
                           upAdjustY > Constants.CLOSE_TO_ZERO) {
                            collide = true;

                            float directionX = template.rotateDirection[direction][0];
                            float adjustX;
                            if(directionX > 0f) {
                                adjustX = rightAdjustX;
                            } else if(directionX < 0f) {
                                adjustX = leftAdjustX;
                            } else {
                                adjustX = 0f;
                            }

                            if(adjustX > maxAdjustX) {
                                maxAdjustX = adjustX;
                            }

                            float directionY = template.rotateDirection[direction][1];
                            float adjustY;
                            if(directionY > 0f) {
                                adjustY = upAdjustY;
                            } else if(directionY < 0f) {
                                adjustY = downAdjustY;
                            } else {
                                adjustY = 0f;
                            }

                            if(adjustY > maxAdjustY) {
                                maxAdjustY = adjustY;
                            }
                        }
                    }
                }
            }
        }

        if(collide) {
            position[0] -= template.rotateDirection[direction][0] * maxAdjustX;
            position[1] -= template.rotateDirection[direction][1] * maxAdjustY;
            getMapRegion(map, region);
        }
    }
}
