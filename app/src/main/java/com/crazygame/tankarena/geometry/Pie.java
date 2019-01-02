package com.crazygame.tankarena.geometry;

import android.opengl.GLES20;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Pie extends Shape {
    public final float radius;
    public final float startAngleInDegree;
    public final float endAngleInDegree;

    public Pie(float radius, float startAngleInDegree, float endAngleInDegree, int numPoints) {
        super(numPoints + 1);

        this.radius = radius;
        this.startAngleInDegree = startAngleInDegree;
        this.endAngleInDegree = endAngleInDegree;

        float[] vertexData = new float[numVertices * SimpleShaderProgram.POSITION_COMPONENT_COUNT];

        vertexData[0] = 0f;
        vertexData[1] = 0f;

        float angle = startAngleInDegree * (float)Math.PI / 180f;
        float angleDelta = (endAngleInDegree - startAngleInDegree) *
                (float)Math.PI / 180f / (float)(numPoints-1);
        int offset = 2;

        for(int i = 0; i < numPoints; ++i) {
            vertexData[offset++] = radius * (float)Math.cos(angle);
            vertexData[offset++] = radius * (float)Math.sin(angle);
            angle += angleDelta;
        }

        vertices.floatBuffer.position(0);
        vertices.floatBuffer.put(vertexData);
        vertices.bindData();
    }

    @Override
    public void draw(SimpleShaderProgram program,  float[] fillColor,
                     float[] borderColor, float lineWidth) {
        program.setPosition(vertices, 0, 0);

        if(fillColor != null) {
            program.setColor(fillColor, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numVertices);
        }

        if(borderColor != null) {
            program.setColor(borderColor, 0);
            GLES20.glLineWidth(lineWidth);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 1, numVertices);
        }
    }
}
