package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Tile extends GameObject {
    private static TileTemplate template = new TileTemplate();

    public Tile(float x, float y) {
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        template.draw(simpleShaderProgram, position);
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