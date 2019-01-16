package com.crazygame.tankarena.gameobj;

import com.crazygame.tankarena.data.VertexBuffer;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class ExplosionTemplate {
    public final VertexBuffer pointsBuffer;
    public final VertexBuffer speedsBuffer;
    public final float[] color = new float[3];
    public final float breath;

    public ExplosionTemplate(String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            color[0] = Float.parseFloat(tokenizer.nextToken());
            color[1] = Float.parseFloat(tokenizer.nextToken());
            color[2] = Float.parseFloat(tokenizer.nextToken());

            int numPoints = Integer.parseInt(tokenizer.nextToken());
            breath = Integer.parseInt(tokenizer.nextToken());

            float[] points = new float[numPoints * SimpleShaderProgram.POSITION_COMPONENT_COUNT];
            float[] speeds = new float[numPoints];

            int offset = 0;
            for(int i = 0; i < numPoints; ++i) {
                tokenizer = new StringTokenizer(reader.readLine());

                points[offset++] = Float.parseFloat(tokenizer.nextToken());
                points[offset++] = Float.parseFloat(tokenizer.nextToken());
                speeds[i] = Float.parseFloat(tokenizer.nextToken());
            }

            pointsBuffer = new VertexBuffer(points.length);
            pointsBuffer.floatBuffer.position(0);
            pointsBuffer.floatBuffer.put(points);
            pointsBuffer.bindData();

            speedsBuffer = new VertexBuffer(speeds.length);
            speedsBuffer.floatBuffer.position(0);
            speedsBuffer.floatBuffer.put(speeds);
            speedsBuffer.bindData();

        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
