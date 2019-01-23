package com.crazygame.tankarena.gameobj;

import android.util.Log;

import com.crazygame.tankarena.data.Constants;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;
import com.crazygame.tankarena.utils.FileLog;

public class Bullet extends GameObject {
    public final static BulletTemplate template = new BulletTemplate();

    public int side;
    public final float[] direction = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public void update(Map map, float timeDelta) {
        float moveDistance = template.speed[side] * timeDelta;
        if(moveDistance >= Constants.MAX_MOVE_DISTANCE) {
            moveDistance = Constants.MAX_MOVE_DISTANCE;
        }

        final int oldBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int oldTopRow = map.crampRow(map.getRow(topBound()));
        final int oldLeftCol = map.crampCol(map.getCol(leftBound()));
        final int oldRightCol = map.crampCol(map.getCol(rightBound()));

        position[0] += moveDistance * direction[0];
        position[1] += moveDistance * direction[1];

        final int newBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int newTopRow = map.crampRow(map.getRow(topBound()));
        final int newLeftCol = map.crampCol(map.getCol(leftBound()));
        final int newRightCol = map.crampCol(map.getCol(rightBound()));

        map.move(this, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol, newBottomRow,
                newTopRow, newLeftCol, newRightCol);

        if(outOfBound(map) || checkCollision(map)) {
            map.removeObject(this, newBottomRow, newTopRow, newLeftCol, newRightCol);
            Pool.bulletPool.free(this);
        } else {
            flag |= FLAG_UPDATED;
        }
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseTime(false);
        template.draw(simpleShaderProgram, side, position);
        flag |= FLAG_DRAWN;
    }

    @Override
    public float leftBound() {
        return position[0] - template.radius;
    }

    @Override
    public float rightBound() {
        return position[0] + template.radius;
    }

    @Override
    public float topBound() {
        return position[1] + template.radius;
    }

    @Override
    public float bottomBound() {
        return position[1] - template.radius;
    }

    @Override
    public float leftCollisionBound() {
        return position[0] - template.collisionRadius;
    }

    @Override
    public float rightCollisionBound() {
        return position[0] + template.collisionRadius;
    }

    @Override
    public float topCollisionBound() {
        return position[1] + template.collisionRadius;
    }

    @Override
    public float bottomCollisionBound() {
        return position[1] - template.collisionRadius;
    }

    public int power() {
        return template.power[side];
    }

    private boolean outOfBound(Map map) {
        return position[0] <= map.viewportOrigin[1] - template.radius ||
               position[0] >= map.width + template.radius ||
               position[1] <= -template.radius ||
               position[1] >= map.viewportOrigin[1] + map.gameView.viewportSize[1] +
                              template.radius;
    }

    private boolean checkCollision(Map map) {
        boolean collide = false;
        int bottomRow = map.crampRow(map.getRow(bottomCollisionBound()));
        int topRow = map.crampRow(map.getRow(topCollisionBound()));
        int leftCol = map.crampCol(map.getCol(leftCollisionBound()));
        int rightCol = map.crampCol(map.getCol(rightCollisionBound()));

        map.clearFlags(bottomRow, topRow, leftCol, rightCol, ~GameObject.FLAG_CHECKED);
        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = leftCol; col <= rightCol; ++col) {
                for(MapItem item = map.items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;

                    if(obj == null || (obj.flag & FLAG_CHECKED) != 0 || obj == this) {
                        continue;
                    }

                    if((obj instanceof Tank) || (obj instanceof Tile)) {
                        if(collideWith(obj)) {
                            collide = true;
                            if(obj instanceof Tank) {
                                Tank tank = (Tank)obj;
                                if(tank.side != side) {
                                    tank.health -= template.power[side];
                                    if(tank.health <= 0) {
                                        map.removeObject(tank);
                                        Explosion explosion = Pool.explosionPool.alloc();
                                        explosion.template_id = 1;
                                        explosion.curTime = 0f;
                                        explosion.position[0] = position[0];
                                        explosion.position[1] = position[1];
                                        map.addObject(explosion);
                                    }
                                }
                            }
                        }
                    }

                    obj.flag |= GameObject.FLAG_CHECKED;
                }
            }
        }

        if(collide) {
            Explosion explosion = Pool.explosionPool.alloc();
            explosion.template_id = 0;
            explosion.curTime = 0f;
            explosion.position[0] = position[0];
            explosion.position[1] = position[1];
            map.addObject(explosion);
        }

        return collide;
    }

    private boolean collideWith(GameObject obj) {
        if(obj instanceof Tank) {
            Tank tank = (Tank)obj;
            if(tank.side == side) {
                return false;
            }
        }

        return (obj.rightCollisionBound() - leftCollisionBound()) > 0 &&
               (rightCollisionBound() - obj.leftCollisionBound()) > 0 &&
               (obj.topCollisionBound() - bottomCollisionBound()) > 0 &&
               (topCollisionBound() - obj.bottomCollisionBound()) > 0;
    }
}
