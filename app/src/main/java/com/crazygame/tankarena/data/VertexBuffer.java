package com.crazygame.tankarena.data;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexBuffer {
    public final int bufferId;
    public final FloatBuffer floatBuffer;

    public VertexBuffer(int numFloats) {
        final int[] buffers = new int[1];

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        if(buffers[0] == 0) {
            throw new RuntimeException("Couldn't create new vertex buffer objects");
        }
        bufferId = buffers[0];

        floatBuffer = ByteBuffer
                .allocateDirect(numFloats * Constants.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    public VertexBuffer(float[] vertexData) {
        this(vertexData.length);
        floatBuffer.position(0);
        floatBuffer.put(vertexData);
        bindData();
    }

    public void bindData() {
        floatBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * Constants.BYTES_PER_FLOAT,
                floatBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttributePointer(int dataOffset, int attributeLocation,
                                          int componentCount, int stride) {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferId);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT, false,
                stride, dataOffset);
        GLES20.glEnableVertexAttribArray(attributeLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
