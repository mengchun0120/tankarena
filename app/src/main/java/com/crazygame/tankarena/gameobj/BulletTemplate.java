package com.crazygame.tankarena.gameobj;

import android.graphics.Color;

import com.crazygame.tankarena.geometry.Circle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class BulletTemplate {
    public final float radius = 7.0f;
    public final float collisionRadius = 4.94f;
    public final float[] speed = {
            300f, 100f
    };

    public final float[][] fillColor = {
            OpenGLHelper.getColor(Color.argb(255, 153, 217, 234)),
            OpenGLHelper.getColor(Color.argb(255, 200, 100, 100))
    };

    public final float[][] borderColor = {
            OpenGLHelper.getColor(Color.argb(255, 80, 80, 255)),
            OpenGLHelper.getColor(Color.argb(255, 255, 80, 80))
    };

    public final int[] power = {
            3, 1
    };

    public final Circle circle;

    public BulletTemplate() {
        circle = new Circle(radius, 0f, 0f, 10);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, int side, float[] position) {
        simpleShaderProgram.setObjRef(position, 0);
        simpleShaderProgram.setRotate(null, null);
        circle.draw(simpleShaderProgram, fillColor[side], borderColor[side], 1f);
    }
}
