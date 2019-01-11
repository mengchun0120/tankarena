package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Bullet extends GameObject {
    public final static BulletTemplate template = new BulletTemplate();

    public final int side;
    public final float[] direction = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public Bullet(int side, float x, float y, float directionX, float directionY) {
        this.side = side;
        position[0] = x;
        position[1] = y;
        direction[0] = directionX;
        direction[1] = directionY;
    }

    public void update(Map map, float timeDelta) {
        final float moveDistance = template.speed[side] * timeDelta;

        position[0] += moveDistance * direction[0];
        position[1] += moveDistance * direction[1];

        if(position[0] <= -template.radius || position[0] >= map.width + template.radius ||
           position[1] <= -template.radius || position[1] >= map.height + template.radius) {

            flag |= FLAG_DELETED;
        } else {

        }

        flag |= FLAG_UPDATED;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
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
}
