package com.crazygame.tankarena.geometry;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Circle extends Polygon {
    public final float radius;

    public Circle(float radius, float centerX, float centerY, int numPoints) {
        super(genSideVertices(radius, centerX, centerY, numPoints), centerX, centerY);
        this.radius = radius;
    }

    private static float[] genSideVertices(float radius,float centerX, float centerY,
                                           int numPoints) {
        final int numFloats = (numPoints + 1) * SimpleShaderProgram.POSITION_COMPONENT_COUNT;
        final float[] vertexData = new float[numFloats];
        int offset = 0;

        final float angleDelta = 2f * (float)Math.PI / (float)numPoints;
        float angle = 0f;

        for(int i = 0; i <= numPoints; ++i) {
            vertexData[offset++] = centerX + radius * (float)Math.cos(angle);
            vertexData[offset++] = centerY + radius * (float)Math.sin(angle);
            angle += angleDelta;
        }

        return vertexData;
    }
}
