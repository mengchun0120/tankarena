package com.crazygame.tankarena.opengl;

import android.content.Context;
import android.opengl.GLES20;

import com.crazygame.tankarena.utils.FileUtil;

public class ShaderProgram {
    // Uniform constants
    public final int program;

    public ShaderProgram(Context context, int vertexShaderResourceId,
                         int fragmentShaderResourceId) {
        program = OpenGLHelper.buildProgram(
                FileUtil.readTextFromResource(context, vertexShaderResourceId),
                FileUtil.readTextFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
