package com.crazygame.tankarena.gameobj;

public class ObjectPool<T> {
    public final Class type;
    private final int[] nextIndices;
    private final Object[] pool;
    private int firstEmptySlot;
    private int firstAvailSlot;

    public ObjectPool(Class type, int maxObjects) {
        this.type = type;

        pool = new Object[maxObjects];
        for(int i = 0; i < maxObjects; ++i) {
            pool[i] = null;
        }

        nextIndices = new int[maxObjects];
        for(int i = 0; i < maxObjects - 1; ++i) {
            nextIndices[i] = i+1;
        }
        nextIndices[maxObjects-1] = -1;

        firstEmptySlot = 0;
        firstAvailSlot = -1;
    }

    public T alloc() {
        T t = null;
        try {
            if (firstAvailSlot == -1) {
                t = (T)type.newInstance();
            } else {
                t = (T)pool[firstAvailSlot];
                pool[firstAvailSlot] = null;
                nextIndices[firstAvailSlot] = firstEmptySlot;
                firstEmptySlot = firstAvailSlot;
                firstAvailSlot = nextIndices[firstAvailSlot];
            }
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return t;
    }

    public void free(T t) {
        if(firstEmptySlot == -1) {
            return;
        }

        pool[firstEmptySlot] = t;
        nextIndices[firstEmptySlot] = firstAvailSlot;
        firstAvailSlot = firstEmptySlot;
        firstEmptySlot = nextIndices[firstEmptySlot];
    }
}
