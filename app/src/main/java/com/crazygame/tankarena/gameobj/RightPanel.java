package com.crazygame.tankarena.gameobj;

import android.graphics.Color;

import com.crazygame.tankarena.geometry.Rectangle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class RightPanel {
    private final Rectangle panel;
    private final float[] fillColor = OpenGLHelper.getColor(
            Color.argb(255, 120, 120, 120));
    private final float[] borderColor = OpenGLHelper.getColor(
            Color.argb(255, 50, 50, 50));
    private final float[] center = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];

    public RightPanel(float centerX, float centerY, float width, float height) {
        center[0] = centerX;
        center[1] = centerY;
        panel = new Rectangle(width, height, 0f, 0f);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setObjRef(center, 0);
        simpleShaderProgram.setRotate(null, null);
        panel.draw(simpleShaderProgram, fillColor, borderColor, 1f);
    }
}
