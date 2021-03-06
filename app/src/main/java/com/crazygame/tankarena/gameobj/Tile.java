package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Tile extends GameObject {
    private static TileTemplate template = new TileTemplate();
    private static int count = 0;

    public Tile(float x, float y) {
        super("i" + (count++));
        position[0] = x;
        position[1] = y;
    }

    @Override
    public String toString() {
        return idStr;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setUseTime(false);
        template.draw(simpleShaderProgram, position);
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
}
