package com.crazygame.tankarena.utils;

public class TimeDeltaCalculator {
    private final float[] timeDeltaHistory;
    private long prevTime;

    public TimeDeltaCalculator(int timeDeltaHistorySize) {
        timeDeltaHistory = new float[timeDeltaHistorySize];
    }

    public void start() {
        for(int i = 0; i < timeDeltaHistory.length; ++i) {
            timeDeltaHistory[i] = 0f;
        }

        prevTime = System.nanoTime();
    }

    public float curTimeDelta() {
        long curTime = System.nanoTime();
        float timeDelta = (float)(curTime - prevTime) / 1e9f;

        float timeDeltaSum = timeDelta;
        for(int i  = 1; i < timeDeltaHistory.length; ++i) {
            timeDeltaSum += timeDeltaHistory[i];
            timeDeltaHistory[i-1] = timeDeltaHistory[i];
        }

        timeDelta = timeDeltaSum / (float)timeDeltaHistory.length;
        timeDeltaHistory[timeDeltaHistory.length-1] = timeDelta;

        prevTime = curTime;

        return timeDelta;
    }
}
