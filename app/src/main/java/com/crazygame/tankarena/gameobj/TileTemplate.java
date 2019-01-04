package com.crazygame.tankarena.gameobj;

import android.graphics.Color;

import com.crazygame.tankarena.geometry.Rectangle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class TileTemplate {
    private final float[] borderColor = OpenGLHelper.getColor(
            Color.argb(255, 15, 15, 100));
    private final float[] fillColor = OpenGLHelper.getColor(
            Color.argb(255, 200, 200, 255));
    public final float breath = 25f;
    public final float halfBreath = breath/2f;
    private final Rectangle tile;

    public TileTemplate() {
        tile = new Rectangle(breath, breath, 0f, 0f);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, float[] position) {
        simpleShaderProgram.setObjRef(position, 0);
        simpleShaderProgram.setRotate(null, 0, null,
                0);
        tile.draw(simpleShaderProgram, fillColor, borderColor, 1f);
    }
}
