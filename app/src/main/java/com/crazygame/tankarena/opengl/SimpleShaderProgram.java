package com.crazygame.tankarena.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.crazygame.tankarena.R;
import com.crazygame.tankarena.data.VertexBuffer;

public class SimpleShaderProgram extends ShaderProgram {
    public final static int POSITION_COMPONENT_COUNT = 2;
    public final static int COLOR_COMPONENT_COUNT = 4;

    public final int uRelativeToViewportLocation;
    public final int uViewportOriginLocation;
    public final int uUseObjRefLocation;
    public final int uObjRefLocation;
    public final int uProjectionMatrixLocation;
    public final int uColorLocation;

    public final int aPositionLocation;

    public SimpleShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        uRelativeToViewportLocation = GLES20.glGetUniformLocation(program,
                "u_RelativeToViewport");
        uViewportOriginLocation = GLES20.glGetUniformLocation(program, "u_ViewportOrigin");
        uUseObjRefLocation = GLES20.glGetUniformLocation(program, "u_UseObjRef");
        uObjRefLocation = GLES20.glGetUniformLocation(program, "u_ObjRef");
        uProjectionMatrixLocation = GLES20.glGetUniformLocation(program, "u_ProjectionMatrix");

        uColorLocation = GLES20.glGetUniformLocation(program, "u_Color");

        aPositionLocation = GLES20.glGetAttribLocation(program, "a_Position");
    }

    public void setRelativeToViewport(boolean value) {
        GLES20.glUniform1i(uRelativeToViewportLocation, value ? 1 : 0);
    }

    public void setUseObjRef(boolean value) {
        GLES20.glUniform1i(uUseObjRefLocation, value ? 1 : 0);
    }

    public void setProjectionMatrix(float[] projectionMatrix, int offset) {
        GLES20.glUniformMatrix4fv(uProjectionMatrixLocation, 1, false, projectionMatrix,
                offset);
    }

    public void setViewportOrigin(float[] viewportOrigin, int offset) {
        GLES20.glUniform2fv(uViewportOriginLocation, 1, viewportOrigin, offset);
    }

    public void setObjRef(float[] objRef, int offset) {
        GLES20.glUniform2fv(uObjRefLocation, 1, objRef, offset);
    }

    public void setColor(float[] color, int offset) {
        GLES20.glUniform4fv(uColorLocation, 1, color, offset);
    }

    public void setPosition(VertexBuffer vertexBuffer, int offset, int stride) {
        vertexBuffer.setVertexAttributePointer(offset, aPositionLocation, 
                POSITION_COMPONENT_COUNT, stride);
    }
}
