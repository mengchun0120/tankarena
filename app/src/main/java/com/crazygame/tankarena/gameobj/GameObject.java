package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public abstract class GameObject {
    public final static int FLAG_UPDATED = 0x00000001;
    public final static int FLAG_DELETED = 0x00000002;
    public final static int FLAG_DRAWN =   0x00000004;

    public final float[] position = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    public int flag = 0;

    public abstract void draw(SimpleShaderProgram simpleShaderProgram);

    public abstract float leftBound();

    public abstract float rightBound();

    public abstract float topBound();

    public abstract float bottomBound();

    public abstract float leftCollisionBound();

    public abstract float rightCollisionBound();

    public abstract float topCollisionBound();

    public abstract float bottomCollisionBound();
}
