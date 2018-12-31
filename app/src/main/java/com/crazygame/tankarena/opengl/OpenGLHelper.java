package com.crazygame.tankarena.opengl;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import com.crazygame.tankarena.config.Config;

public class OpenGLHelper {
    private final static String TAG = "OpenGLHelper";
    
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    public static int compileShader(int type, String shaderCode) {
        final int shaderObjectId = GLES20.glCreateShader(type);

        if(shaderObjectId == 0) {
            if(Config.LOG_ON) {
                Log.w(TAG, "Couldn't create new shader");
            }

            return 0;
        }

        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if(Config.LOG_ON) {
            Log.v(TAG, "Results of compiling source:\n" + shaderCode + "\n:" +
                    GLES20.glGetShaderInfoLog(shaderObjectId));
        }

        if(compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);

            if(Config.LOG_ON) {
                Log.w(TAG, "Compilation of shader failed");
            }

            return 0;
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        int programObjectId = GLES20.glCreateProgram();

        if(programObjectId == 0) {
            if(Config.LOG_ON) {
                Log.w(TAG, "Couldn't create new program");
            }

            return 0;
        }

        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);

        GLES20.glLinkProgram(programObjectId);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);

        if(Config.LOG_ON) {
            Log.v(TAG, "Results of linking program:\n" +
                    GLES20.glGetProgramInfoLog(programObjectId));
        }

        if(linkStatus[0] == 0) {
            if(Config.LOG_ON) {
                Log.w(TAG, "Linking program failed");
            }

            return 0;
        }

        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        Log.v(TAG, "Results of validating program:" + validateStatus[0] +
                "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShader = compileVertexShader(vertexShaderCode);
        int fragmentShader = compileFragmentShader(fragmentShaderCode);
        int program = linkProgram(vertexShader, fragmentShader);

        if(Config.LOG_ON) {
            validateProgram(program);
        }

        return program;
    }

    public static float[] getColor(int rgba) {
        float[] color = new float[SimpleShaderProgram.COLOR_COMPONENT_COUNT];
        color[0] = (float)Color.red(rgba) / 255f;
        color[1] = (float)Color.green(rgba) / 255f;
        color[2] = (float)Color.blue(rgba) / 255f;
        color[3] = (float)Color.alpha(rgba) / 255f;
        return color;
    }
}
