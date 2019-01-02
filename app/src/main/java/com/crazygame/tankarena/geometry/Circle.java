package com.crazygame.tankarena.geometry;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Circle extends Polygon {
    public final float radius;

    public Circle(float radius, int numPoints) {
        super(genSideVertices(radius, numPoints));
        this.radius = radius;
    }

    private static float[] genSideVertices(float radius, int numPoints) {
        final int numFloats = (numPoints + 1) * SimpleShaderProgram.POSITION_COMPONENT_COUNT;
        final float[] vertexData = new float[numFloats];
        int offset = 0;

        final float angleDelta = 2f * (float)Math.PI / (float)numPoints;
        float angle = 0f;

        for(int i = 0; i <= numPoints; ++i) {
            vertexData[offset++] = radius * (float)Math.cos(angle);
            vertexData[offset++] = radius * (float)Math.sin(angle);
            angle += angleDelta;
        }

        return vertexData;
    }
}
