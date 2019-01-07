package com.crazygame.tankarena.gameobj;

import android.graphics.Color;

import com.crazygame.tankarena.geometry.Circle;
import com.crazygame.tankarena.geometry.Rectangle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class TankTemplate {
    private final float[][] baseColor = {
            OpenGLHelper.getColor(Color.argb(255, 0, 180, 0)),
            OpenGLHelper.getColor(Color.argb(255, 200, 150, 150))
    };

    private final float[][] turretColor = {
            OpenGLHelper.getColor(Color.argb(255, 122, 255, 122)),
            OpenGLHelper.getColor(Color.argb(255, 255, 200, 200))
    };

    private final float[][] barrelColor = {
            OpenGLHelper.getColor(Color.argb(255, 60, 122,122)),
            OpenGLHelper.getColor(Color.argb(255, 200, 122, 60))
    };

    private final float[][] borderColor = {
            OpenGLHelper.getColor(Color.argb(255, 0, 89, 0)),
            OpenGLHelper.getColor(Color.argb(255, 89, 0, 0))
    };

    public final float[][] rotateDirection = {
            {0f, 1f},
            {-1f, 0f},
            {0f, -1f},
            {1f, 0f}
    };

    public final float[] fireSpeed = {
            0.2f, 1.0f
    };

    private final float[] rotateRef = {0f, 0f};

    public final Rectangle base;
    public final Circle turret;
    public final Rectangle barrel;

    public final float breath = 100f;
    public final float halfBreath = breath / 2f;
    public final float[] firingPoint = {halfBreath, 0f};

    public TankTemplate() {
        base = new Rectangle(breath, breath, 0f, 0f);

        final float turretToCenter = 10f;
        final float turretRadius = 30f;
        turret = new Circle(turretRadius, -turretToCenter, 0f, 20);

        final float barrelToCenter = 35f;
        final float barrelWidth = 30f;
        final float barrelHeight = 15f;
        barrel = new Rectangle(barrelWidth, barrelHeight, barrelToCenter, 0f);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram, int side, float[] position,
                     int direction) {
        simpleShaderProgram.setObjRef(position, 0);
        simpleShaderProgram.setRotate(rotateRef, rotateDirection[direction]);
        base.draw(simpleShaderProgram, baseColor[side], borderColor[side], 1f);
        barrel.draw(simpleShaderProgram, barrelColor[side], borderColor[side], 1f);
        turret.draw(simpleShaderProgram, turretColor[side], borderColor[side], 1f);
    }
}
