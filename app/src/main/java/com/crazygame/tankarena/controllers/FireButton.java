package com.crazygame.tankarena.controllers;

import android.graphics.Color;

import com.crazygame.tankarena.GameView;
import com.crazygame.tankarena.geometry.Circle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class FireButton {
    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 0, 9));
    private final float[] normalColor =
            OpenGLHelper.getColor(Color.argb(255, 200, 200, 255));
    private final float[] pressedColor =
            OpenGLHelper.getColor(Color.argb(255, 255, 12, 12));

    private final float radius = 150f;
    private final float[] center = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    public boolean firing = false;
    private Circle button;
    private int curPointerId = -1;

    public FireButton(float centerX, float centerY) {
        center[0] = centerX;
        center[1] = centerY;
        button = new Circle(radius, 0f, 0f, 40);
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setObjRef(center, 0);
        simpleShaderProgram.setRotate(null, null);
        button.draw(simpleShaderProgram, firing ? pressedColor : normalColor, borderColor,
                1f);
    }

    public void onTouch(int action, int pointerId, float x, float y) {
        if(curPointerId != -1) {
            if(curPointerId != pointerId) {
                return;
            } else if(action == GameView.FINGER_UP) {
                curPointerId = -1;
                firing = false;
                return;
            }
        } else if(action == GameView.FINGER_UP) {
            return;
        }

        float distanceX = x - center[0];
        float distanceY = y - center[1];

        if(distanceX * distanceX + distanceY * distanceY > radius * radius) {
            if(curPointerId != -1) {
                curPointerId = -1;
                firing = false;
            }
            return;
        }

        firing = true;
        curPointerId = pointerId;
    }
}
