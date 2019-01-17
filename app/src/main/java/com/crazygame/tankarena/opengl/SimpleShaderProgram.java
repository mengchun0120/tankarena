package com.crazygame.tankarena.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.crazygame.tankarena.R;
import com.crazygame.tankarena.data.VertexBuffer;

public class SimpleShaderProgram extends ShaderProgram {
    public final static int POSITION_COMPONENT_COUNT = 2;
    public final static int COLOR_COMPONENT_COUNT = 4;

    public final int relativeToViewportOriginLocation;
    public final int viewportOriginLocation;
    public final int screenCenterLocation;
    public final int useObjRefLocation;
    public final int objRefLocation;
    public final int rotateLocation;
    public final int rotateRefLocation;
    public final int rotateDirectionLocation;
    public final int viewportSizeLocation;
    public final int colorLocation;
    public final int positionLocation;
    public final int useTimeLocation;
    public final int directionLocation;
    public final int speedLocation;
    public final int curTimeLocation;
    public final int durationLocation;
    public final int timeColorLocation;

    public SimpleShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        relativeToViewportOriginLocation = GLES20.glGetUniformLocation(program,
                "relativeToViewportOrigin");
        viewportOriginLocation = GLES20.glGetUniformLocation(program, "viewportOrigin");
        screenCenterLocation = GLES20.glGetUniformLocation(program, "screenCenter");
        useObjRefLocation = GLES20.glGetUniformLocation(program, "useObjRef");
        objRefLocation = GLES20.glGetUniformLocation(program, "objRef");
        rotateLocation = GLES20.glGetUniformLocation(program, "rotate");
        rotateRefLocation = GLES20.glGetUniformLocation(program, "rotateRef");
        rotateDirectionLocation = GLES20.glGetUniformLocation(program, "rotateDirection");
        viewportSizeLocation = GLES20.glGetUniformLocation(program, "viewportSize");
        colorLocation = GLES20.glGetUniformLocation(program, "color");
        positionLocation = GLES20.glGetAttribLocation(program, "position");
        useTimeLocation = GLES20.glGetUniformLocation(program, "useTime");
        directionLocation = GLES20.glGetAttribLocation(program, "direction");
        speedLocation = GLES20.glGetAttribLocation(program, "speed");
        curTimeLocation = GLES20.glGetUniformLocation(program, "curTime");
        durationLocation = GLES20.glGetUniformLocation(program, "duration");
        timeColorLocation = GLES20.glGetUniformLocation(program, "timeColor");
    }

    public void setViewportOrigin(float[] viewportOrigin, float[] screenCenter) {
        GLES20.glUniform1i(relativeToViewportOriginLocation, viewportOrigin != null ? 1 : 0);
        if(viewportOrigin != null) {
            GLES20.glUniform2fv(viewportOriginLocation, 1, viewportOrigin, 0);
            GLES20.glUniform2fv(screenCenterLocation, 1, screenCenter, 0);
        }
    }

    public void setObjRef(float[] objRef, int offset) {
        GLES20.glUniform1i(useObjRefLocation, objRef != null ? 1 : 0);
        if(objRef != null) {
            GLES20.glUniform2fv(objRefLocation,  1, objRef, offset);
        }
    }

    public void setRotate(float[] rotateRef, float[] rotateDirection) {
        GLES20.glUniform1i(rotateLocation, rotateRef != null ? 1 : 0);
        if(rotateRef != null) {
            GLES20.glUniform2fv(rotateRefLocation, 1, rotateRef, 0);
            GLES20.glUniform2fv(rotateDirectionLocation, 1, rotateDirection, 0);
        }
    }

    public void setViewportSize(float[] viewportSize) {
        GLES20.glUniform2fv(viewportSizeLocation, 1, viewportSize, 0);
    }

    public void setColor(float[] color, int offset) {
        GLES20.glUniform4fv(colorLocation, 1, color, offset);
    }

    public void setPosition(VertexBuffer vertexBuffer, int offset, int stride) {
        vertexBuffer.setVertexAttributePointer(offset, positionLocation,
                POSITION_COMPONENT_COUNT, stride);
    }

    public void setUseTime(boolean useTime) {
        GLES20.glUniform1i(useTimeLocation, useTime ? 1 : 0);
    }

    public void setTimeRelated(VertexBuffer directionBuffer, VertexBuffer speedBuffer,
                               float[] objRef, float[] timeColor, float curTime, float duration) {
        GLES20.glUniform1f(curTimeLocation, curTime);
        GLES20.glUniform1f(durationLocation, duration);
        GLES20.glUniform2fv(objRefLocation, 1, objRef, 0);
        GLES20.glUniform3fv(timeColorLocation, 1, timeColor, 0);
        directionBuffer.setVertexAttributePointer(0, directionLocation,
                POSITION_COMPONENT_COUNT, 0);
        speedBuffer.setVertexAttributePointer(0, speedLocation,
                1, 0);
    }
}
