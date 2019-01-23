package com.crazygame.tankarena.gameobj;

import android.util.Log;

import com.crazygame.tankarena.controllers.DriveWheel;
import com.crazygame.tankarena.data.Constants;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

import java.util.Random;

public class Tank extends GameObject {
    public final static TankTemplate template = new TankTemplate();
    private int direction;
    private boolean moving = false;
    public final int side;
    public boolean firing = false;
    private float moveSpeed = 200f;
    private float timeSinceLastFire = 0f;
    private float timeLastUI = 0f;
    public static int count = 0;
    public int health = 10;
    private Random random = new Random();

    public Tank(int side, int direction, float x, float y) {
        this.side = side;
        this.direction = direction;
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseTime(false);
        template.draw(simpleShaderProgram, side, position, direction);
        flag |= FLAG_DRAWN;
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
        if(side == 1) {
            timeLastUI += timeDelta;
            if(timeLastUI > 0.5f) {
                runAI(map);
                timeLastUI = 0f;
            }
        }

        if(firing) {
            timeSinceLastFire += timeDelta;
            if(timeSinceLastFire > template.fireSpeed[side]) {
                fire(map);
                timeSinceLastFire = 0f;
            }
        }

        if(moving) {
            move(map, timeDelta);
        }

        flag |= FLAG_UPDATED;
    }

    private void move(Map map, float timeDelta) {
        float moveDistance = moveSpeed * timeDelta;
        if(moveDistance >= Constants.MAX_MOVE_DISTANCE) {
            moveDistance = Constants.MAX_MOVE_DISTANCE;
        }

        final int oldBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int oldTopRow = map.crampRow(map.getRow(topBound()));
        final int oldLeftCol = map.crampCol(map.getCol(leftBound()));
        final int oldRightCol = map.crampCol(map.getCol(rightBound()));
        
        position[0] = crampX(map,
                position[0] + moveDistance * template.rotateDirection[direction][0]);
        position[1] = crampY(map,
                position[1] + moveDistance * template.rotateDirection[direction][1]);

        checkCollisionAndAdjustPosition(map);

        final int newBottomRow = map.crampRow(map.getRow(bottomBound()));
        final int newTopRow = map.crampRow(map.getRow(topBound()));
        final int newLeftCol = map.crampCol(map.getCol(leftBound()));
        final int newRightCol = map.crampCol(map.getCol(rightBound()));

        map.move(this, oldBottomRow, oldTopRow, oldLeftCol, oldRightCol, newBottomRow,
                newTopRow, newLeftCol, newRightCol);

        checkBullet(map);
    }

    public void fire(Map map) {
        float directionX = template.rotateDirection[direction][0];
        float directionY = template.rotateDirection[direction][1];
        float bulletX = position[0] + template.firingPoint[0] * directionX -
                template.firingPoint[1] * directionY;
        float bulletY = position[1] + template.firingPoint[0] * directionY +
                template.firingPoint[1] * directionX;

        Bullet bullet = Pool.bulletPool.alloc();
        bullet.direction[0] = directionX;
        bullet.direction[1] = directionY;
        bullet.position[0] = bulletX;
        bullet.position[1] = bulletY;
        bullet.side = side;
        bullet.flag |= FLAG_UPDATED;
        map.addObject(bullet);
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

    private void checkCollisionAndAdjustPosition(Map map) {
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
                        adjustPosition(obj);
                    }

                    obj.flag |= GameObject.FLAG_CHECKED;
                }
            }
        }
    }

    private void adjustPosition(GameObject obj) {
        float leftAdjustX = obj.rightCollisionBound() - leftCollisionBound();
        float rightAdjustX = rightCollisionBound() - obj.leftCollisionBound();
        float downAdjustY = obj.topCollisionBound() - bottomCollisionBound();
        float upAdjustY =  topCollisionBound() - obj.bottomCollisionBound();

        if(leftAdjustX <= Constants.CLOSE_TO_ZERO || rightAdjustX <= Constants.CLOSE_TO_ZERO ||
           downAdjustY <= Constants.CLOSE_TO_ZERO || upAdjustY <= Constants.CLOSE_TO_ZERO) {
            return;
        }

        float directionX = template.rotateDirection[direction][0];
        float directionY = template.rotateDirection[direction][1];

        float adjustX;
        if (directionX > 0f) {
            adjustX = rightAdjustX;
        } else if (directionX < 0f) {
            adjustX = leftAdjustX;
        } else {
            adjustX = 0f;
        }

        float adjustY;
        if (directionY > 0f) {
            adjustY = upAdjustY;
        } else if (directionY < 0f) {
            adjustY = downAdjustY;
        } else {
            adjustY = 0f;
        }

        position[0] -= directionX * adjustX;
        position[1] -= directionY * adjustY;
    }

    private void checkBullet(Map map) {
        int bottomRow = map.crampRow(map.getRow(bottomCollisionBound()));
        int topRow = map.crampRow(map.getRow(topCollisionBound()));
        int leftCol = map.crampCol(map.getCol(leftCollisionBound()));
        int rightCol = map.crampCol(map.getCol(rightCollisionBound()));

        map.clearFlags(bottomRow, topRow, leftCol, rightCol, ~GameObject.FLAG_CHECKED);
        for(int row = bottomRow; row <= topRow; ++row) {
            for(int col = leftCol; col <= rightCol; ++col) {
                for(MapItem item = map.items[row][col]; item != null; item = item.next) {
                    GameObject obj = item.gameObject;
                    if(obj instanceof Bullet && collideWith(obj)) {
                        Bullet bullet = (Bullet)obj;
                        if(bullet.side != side) {
                            health -= bullet.power();
                            map.removeObject(bullet);
                            Pool.bulletPool.free(bullet);
                        }
                    }
                }
            }
        }

        if(health <= 0) {
            map.removeObject(this);
            Explosion explosion = Pool.explosionPool.alloc();
            explosion.template_id = 1;
            explosion.curTime = 0f;
            explosion.position[0] = position[0];
            explosion.position[1] = position[1];
            map.addObject(explosion);
        }
    }

    private boolean collideWith(GameObject obj) {
        return (obj.rightCollisionBound() - leftCollisionBound()) > 0 &&
                (rightCollisionBound() - obj.leftCollisionBound()) > 0 &&
                (obj.topCollisionBound() - bottomCollisionBound()) > 0 &&
                (topCollisionBound() - obj.bottomCollisionBound()) > 0;
    }

    private void runAI(Map map) {
        float deltaX = map.player.position[0] - position[0];
        float deltaY = map.player.position[1] - position[1];
        float absDeltaX = Math.abs(deltaX);
        float absDeltaY = Math.abs(deltaY);

        boolean horizontalInRange = absDeltaX < template.halfBreath;
        boolean verticalInRange = absDeltaY < template.halfBreath;
        boolean chooseHorizontal;

        if(horizontalInRange || verticalInRange) {
            moving = false;
            firing = true;

            if(horizontalInRange && verticalInRange) {
                chooseHorizontal = random.nextBoolean();
            } else {
                chooseHorizontal = verticalInRange;
            }

        } else {
            moving = true;
            firing = false;
            chooseHorizontal = random.nextBoolean();
        }

        if(chooseHorizontal) {
            direction = deltaX >= 0 ? DriveWheel.RIGHT : DriveWheel.LEFT;
        } else {
            direction = deltaY >= 0 ? DriveWheel.UP : DriveWheel.DOWN;
        }
    }
}
