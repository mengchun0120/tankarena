package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.controllers.DriveWheel;
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

        final int oldStartRow = map.crampRow(map.getRow(bottomBound()));
        final int oldEndRow = map.crampRow(map.getRow(topBound()));
        final int oldStartCol = map.crampCol(map.getCol(leftBound()));
        final int oldEndCol = map.crampCol(map.getCol(rightBound()));

        final float newX = crampX(map,
                position[0] + moveDistance * template.rotateDirection[direction][0]);
        final float newY = crampY(map,
                position[1] + moveDistance * template.rotateDirection[direction][1]);

        final int newStartRow = map.crampRow(map.getRow(newY - template.halfBreath));
        final int newEndRow = map.crampRow(map.getRow(newY + template.halfBreath));
        final int newStartCol = map.crampCol(map.getCol(newX - template.halfBreath));
        final int newEndCol = map.crampCol(map.getCol(newX + template.halfBreath));

        for(int row = oldStartRow; row <= oldEndRow; ++row) {
            for(int col = oldStartCol; col <= oldEndCol; ++col) {
                if(row < newStartRow || row > newEndRow || col < newStartCol
                        || col > newEndCol) {
                    map.removeObject(this, row, col);
                }
            }
        }

        for(int row = newStartRow; row <= newEndRow; ++row) {
            for(int col = newStartCol; col <= newEndCol; ++col) {
                if(row < oldStartRow || row > oldEndRow || col < oldStartCol
                        || col > oldEndCol) {
                    map.addObject(this, row, col);
                }
            }
        }

        position[0] = newX;
        position[1] = newY;
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
}
