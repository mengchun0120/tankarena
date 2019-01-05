package com.crazygame.tankarena.controllers;

import android.graphics.Color;

import com.crazygame.tankarena.GameView;
import com.crazygame.tankarena.geometry.Pie;
import com.crazygame.tankarena.geometry.Polygon;
import com.crazygame.tankarena.geometry.Triangle;
import com.crazygame.tankarena.opengl.OpenGLHelper;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class DriveWheel {
    public final static int UP = 0;
    public final static int LEFT = 1;
    public final static int DOWN = 2;
    public final static int RIGHT = 3;
    public final static int NOT_MOVE = -1;

    private final float[] borderColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 0, 9));
    private final float[] arrowFillColor =
            OpenGLHelper.getColor(Color.argb(255, 255, 200, 30));
    private final float[] normalPieColor =
            OpenGLHelper.getColor(Color.argb(255, 227, 255, 164));
    private final float[] pressedPieColor =
            OpenGLHelper.getColor(Color.argb(255, 0, 255, 0));

    private final float[] center = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final float radius = 150f;
    private final Triangle[] arrows = new Triangle[4];
    private final Pie[] pies = new Pie[4];
    public int direction = NOT_MOVE;
    private int curPointerId = -1;

    public DriveWheel(float centerX, float centerY) {
        final float distArrowToCenter = radius * 0.75f;

        center[0] = centerX;
        center[1] = centerY;

        // Create wheel
        float angle = 45;
        for(int i = 0; i < 4; ++i) {
            pies[i] = new Pie(radius, angle, angle+90f, 0, 0,
                    20);
            angle += 90f;
        }

        final float arrowHeight = radius * 0.25f;
        final float arrowWidth = radius * 0.8f;

        // up arrow
        arrows[0] = new Triangle(new float[]{
                0f, distArrowToCenter + arrowHeight/2f,
                -arrowWidth/2f, distArrowToCenter - arrowHeight/2f,
                arrowWidth/2f, distArrowToCenter - arrowHeight/2f
        });

        // right arrow
        arrows[1] = new Triangle(new float[]{
                -distArrowToCenter + arrowHeight/2f, arrowWidth/2f,
                -distArrowToCenter - arrowHeight/2f, 0f,
                -distArrowToCenter + arrowHeight/2f, -arrowWidth/2f
        });

        // down arrow
        arrows[2] = new Triangle(new float[]{
                arrowWidth/2f, -distArrowToCenter + arrowHeight/2f,
                -arrowWidth/2f, -distArrowToCenter + arrowHeight/2f,
                0f, -distArrowToCenter - arrowHeight/2f
        });

        // left arrow
        arrows[3] = new Triangle(new float[]{
                distArrowToCenter + arrowHeight/2f, 0f,
                distArrowToCenter - arrowHeight/2f, arrowWidth/2f,
                distArrowToCenter - arrowHeight/2f, -arrowWidth/2f
        });
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setObjRef(center, 0);
        simpleShaderProgram.setRotate(null, null);
        for(int i = 0; i < 4; ++i) {
            pies[i].draw(simpleShaderProgram, direction == i ? pressedPieColor : normalPieColor,
                    borderColor, 1.0f);
        }

        for(int i = 0; i < 4; ++i) {
            arrows[i].draw(simpleShaderProgram, arrowFillColor, borderColor, 1.0f);
        }
    }

    public void onTouch(int action, int pointerId, float x, float y) {
        if(curPointerId != -1) {
            if(curPointerId != pointerId) {
                return;
            } else if(action == GameView.FINGER_UP) {
                curPointerId = -1;
                direction = NOT_MOVE;
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
                direction = NOT_MOVE;
            }
            return;
        }

        curPointerId = pointerId;

        float absoluteDistanceX = Math.abs(distanceX);
        float absoluteDistanceY = Math.abs(distanceY);

        if(absoluteDistanceX >= absoluteDistanceY) {
            direction =  distanceX >= 0f ? RIGHT : LEFT;
        } else {
            direction = distanceY >= 0f ? UP : DOWN;
        }
    }
}

