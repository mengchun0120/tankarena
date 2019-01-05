package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Tank extends GameObject {
    public final static TankTemplate template = new TankTemplate();
    private int direction;
    private boolean moving = false;
    private int side;
    private boolean firing = false;
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
}
