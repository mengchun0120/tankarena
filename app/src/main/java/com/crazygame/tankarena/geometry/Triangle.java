package com.crazygame.tankarena.geometry;

import android.opengl.GLES20;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Triangle extends Shape {
    public Triangle(float[] vertices) {
        super(vertices.length / SimpleShaderProgram.POSITION_COMPONENT_COUNT);
        this.vertices.floatBuffer.position(0);
        this.vertices.floatBuffer.put(vertices);
        this.vertices.bindData();
    }

    @Override
    public void draw(SimpleShaderProgram program, float[] fillColor, float[] borderColor,
                     float lineWidth) {
        program.setPosition(vertices, 0, 0);
        if(fillColor != null) {
            program.setColor(fillColor, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numVertices);
        }

        if(borderColor != null) {
            program.setColor(borderColor, 0);
            GLES20.glLineWidth(lineWidth);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, numVertices);
        }
    }
}
