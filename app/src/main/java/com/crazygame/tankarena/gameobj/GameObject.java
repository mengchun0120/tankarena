package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public abstract class GameObject {
    public final float[] position = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    public boolean drawn = false;

    public abstract void draw(SimpleShaderProgram simpleShaderProgram);

    public abstract float leftBound();

    public abstract float rightBound();

    public abstract float topBound();

    public abstract float bottomBound();
}
