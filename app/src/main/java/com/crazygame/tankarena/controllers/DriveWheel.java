package com.crazygame.tankarena.controllers;

import android.graphics.Color;

import com.crazygame.tankarena.GameView;
import com.crazygame.tankarena.geometry.Pie;
import com.crazygame.tankarena.geometry.Polygon;
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
    private final float[] arrowLocations =
            new float[4 * SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private final Polygon[] arrows = new Polygon[4];
    private final Pie[] pies = new Pie[4];
    private int direction = NOT_MOVE;
    private int curPointerId = -1;

    public DriveWheel(float centerX, float centerY) {
        final float distArrowToCenter = radius * 0.75f;

        center[0] = centerX;
        center[1] = centerY;

        // Create wheel
        float angle = 45;
        for(int i = 0; i < 4; ++i) {
            pies[i] = new Pie(radius, angle, angle+90f, 20);
            angle += 90f;
        }

        // location of up arrow
        arrowLocations[0] = centerX;
        arrowLocations[1] = centerY + distArrowToCenter;
        // location of right arrow
        arrowLocations[2] = centerX - distArrowToCenter;
        arrowLocations[3] = centerY;
        // location of down arrow
        arrowLocations[4] = centerX;
        arrowLocations[5] = centerY - distArrowToCenter;
        // location of left arrow
        arrowLocations[6] = centerX + distArrowToCenter;
        arrowLocations[7] = centerY;

        final float arrowHeight = radius * 0.25f;
        final float arrowWidth = radius * 0.8f;

        // up arrow
        arrows[0] = new Polygon(new float[]{
                0f, arrowHeight/2f,
                -arrowWidth/2f, -arrowHeight/2f,
                arrowWidth/2f, -arrowHeight/2f
        });

        // right arrow
        arrows[1] = new Polygon(new float[]{
                arrowHeight/2f, arrowWidth/2f,
                -arrowHeight/2f, 0f,
                arrowHeight/2f, -arrowWidth/2f
        });

        // down arrow
        arrows[2] = new Polygon(new float[]{
                arrowWidth/2f, arrowHeight/2f,
                -arrowWidth/2f, arrowHeight/2f,
                0f, -arrowHeight/2f
        });

        // left arrow
        arrows[3] = new Polygon(new float[]{
                arrowHeight/2f, 0f,
                -arrowHeight/2f, arrowWidth/2f,
                -arrowHeight/2f, -arrowWidth/2f
        });
    }

    public void draw(SimpleShaderProgram simpleShaderProgram) {
        simpleShaderProgram.setObjRef(center, 0);
        for(int i = 0; i < 4; ++i) {
            pies[i].draw(simpleShaderProgram, direction == i ? pressedPieColor : normalPieColor,
                    borderColor, 1.0f);
        }

        for(int i = 0; i < 4; ++i) {
            simpleShaderProgram.setObjRef(arrowLocations, i*2);
            arrows[i].draw(simpleShaderProgram, arrowFillColor, borderColor, 1.0f);
        }
    }

    public int getDirection() {
        return direction;
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

        float xdist = x - center[0];
        float ydist = y - center[1];

        if(xdist * xdist + ydist * ydist > radius * radius) {
            if(curPointerId != -1) {
                curPointerId = -1;
                direction = NOT_MOVE;
            }
            return;
        }

        curPointerId = pointerId;

        float xabs = Math.abs(xdist);
        float yabs = Math.abs(ydist);

        if(xabs >= yabs) {
            direction =  xdist >= 0f ? RIGHT : LEFT;
        } else {
            direction = ydist >= 0f ? UP : DOWN;
        }
    }
}

