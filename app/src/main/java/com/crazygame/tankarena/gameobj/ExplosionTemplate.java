package com.crazygame.tankarena.gameobj;

import android.content.Context;

import com.crazygame.tankarena.R;
import com.crazygame.tankarena.data.VertexBuffer;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class ExplosionTemplate {
    public static ExplosionTemplate[] templates = null;

    public final VertexBuffer directionBuffer;
    public final VertexBuffer speedsBuffer;
    public final float[] color = new float[3];
    public final float breath;
    public final float duration;
    public final int numPoints;

    public static void initTemplates(Context context) {
        if(templates != null) {
            return;
        }
        templates = new ExplosionTemplate[2];
        templates[0] = new ExplosionTemplate(context, R.raw.explosion1);
        templates[1] = new ExplosionTemplate(context, R.raw.explosion2);
    }

    public ExplosionTemplate(Context context, int resourceId) {
        BufferedReader reader = null;
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line);

            color[0] = Float.parseFloat(tokenizer.nextToken());
            color[1] = Float.parseFloat(tokenizer.nextToken());
            color[2] = Float.parseFloat(tokenizer.nextToken());

            numPoints = Integer.parseInt(tokenizer.nextToken());
            breath = Float.parseFloat(tokenizer.nextToken());
            duration = Float.parseFloat(tokenizer.nextToken());

            float[] points = new float[numPoints * SimpleShaderProgram.POSITION_COMPONENT_COUNT];
            float[] speeds = new float[numPoints];

            int offset = 0;
            for(int i = 0; i < numPoints; ++i) {
                tokenizer = new StringTokenizer(reader.readLine());

                points[offset++] = Float.parseFloat(tokenizer.nextToken());
                points[offset++] = Float.parseFloat(tokenizer.nextToken());
                speeds[i] = Float.parseFloat(tokenizer.nextToken());
            }

            directionBuffer = new VertexBuffer(points.length);
            directionBuffer.floatBuffer.position(0);
            directionBuffer.floatBuffer.put(points);
            directionBuffer.bindData();

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
