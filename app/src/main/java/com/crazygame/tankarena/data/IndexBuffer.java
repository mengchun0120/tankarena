package com.crazygame.tankarena.data;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class IndexBuffer {
    private final int bufferId;

    public IndexBuffer(short[] indices) {
        final int[] buffers = new int[1];
        GLES20.glGenBuffers(buffers.length, buffers, 0);
        if(buffers[0] == 0) {
            throw new RuntimeException("Couldn't create new index buffer objects");
        }
        bufferId = buffers[0];

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, bufferId);

        ShortBuffer indexArray = ByteBuffer
                .allocateDirect(indices.length * Constants.BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indices);
        indexArray.position(0);

        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                indexArray.capacity() * Constants.BYTES_PER_SHORT,
                indexArray, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getBufferId() {
        return bufferId;
    }
}
