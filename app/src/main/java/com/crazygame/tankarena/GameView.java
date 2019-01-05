package com.crazygame.tankarena;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.crazygame.tankarena.controllers.DriveWheel;
import com.crazygame.tankarena.controllers.FireButton;
import com.crazygame.tankarena.gameobj.LeftPanel;
import com.crazygame.tankarena.gameobj.Map;
import com.crazygame.tankarena.gameobj.RightPanel;
import com.crazygame.tankarena.opengl.SimpleShaderProgram;
import com.crazygame.tankarena.utils.TimeDeltaCalculator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer,
        View.OnTouchListener {
    public final static int RUNNING = 0;
    public final static int PAUSED = 1;
    public final static int END = 2;

    public final static int FINGER_DOWN = 0;
    public final static int FINGER_MOVE = 1;
    public final static int FINGER_UP = 2;

    private final Context context;

    public final float[] viewportSize = new float[SimpleShaderProgram.POSITION_COMPONENT_COUNT];
    private SimpleShaderProgram simpleShaderProgram;

    private boolean running;

    private final TouchEventHandlerPool touchEventHandlerPool =
            new TouchEventHandlerPool(100);

    private DriveWheel driveWheel;
    private FireButton fireButton;
    public Map map;
    private LeftPanel leftPanel;
    private RightPanel rightPanel;

    TimeDeltaCalculator timeDeltaCalculator = new TimeDeltaCalculator(3);

    public GameView(Context context, float width, float height) {
        super(context);

        this.context = context;

        viewportSize[0] = width;
        viewportSize[1] = height;

        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        simpleShaderProgram = new SimpleShaderProgram(context);
        simpleShaderProgram.useProgram();

        simpleShaderProgram.setViewportSize(viewportSize);

        driveWheel = new DriveWheel(-viewportSize[0]/2f + 180f,
                -viewportSize[1]/2f + 180f);
        fireButton = new FireButton(viewportSize[0]/2f - 180f,
                -viewportSize[1]/2f + 180f);
        map = new Map(this, R.raw.map1);

        float panelWidth = (viewportSize[0] - map.width) / 2f;
        leftPanel = new LeftPanel(-(map.width + panelWidth) / 2f, 0f, panelWidth,
                viewportSize[1]);
        rightPanel = new RightPanel((map.width + panelWidth) / 2f, 0f, panelWidth,
                viewportSize[1]);

        timeDeltaCalculator.start();

        setOnTouchListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float timeDelta = timeDeltaCalculator.curTimeDelta();

        map.update(timeDelta);

        map.draw(simpleShaderProgram);

        simpleShaderProgram.setViewportOrigin(null, null);
        leftPanel.draw(simpleShaderProgram);
        rightPanel.draw(simpleShaderProgram);
        driveWheel.draw(simpleShaderProgram);
        fireButton.draw(simpleShaderProgram);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                int pointerIdx = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(pointerIdx);
                float x = translateX(motionEvent.getX(pointerIdx));
                float y = translateY(motionEvent.getY(pointerIdx));
                queueEvent(touchEventHandlerPool.alloc(FINGER_DOWN, pointerId, x, y));
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                int count = motionEvent.getPointerCount();
                for (int i = 0; i < count; ++i) {
                    int pointerId = motionEvent.getPointerId(i);
                    float x = translateX(motionEvent.getX(i));
                    float y = translateY(motionEvent.getY(i));
                    queueEvent(touchEventHandlerPool.alloc(FINGER_MOVE, pointerId, x, y));
                }
                return true;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                int pointerIdx = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(pointerIdx);
                float x = translateX(motionEvent.getX(pointerIdx));
                float y = translateY(motionEvent.getY(pointerIdx));
                queueEvent(touchEventHandlerPool.alloc(FINGER_UP, pointerId, x, y));
                return true;
            }
        }

        return false;
    }

    private float translateX(float x) {
        return -viewportSize[0]/2f + x;
    }

    private float translateY(float y) {
        return viewportSize[1]/2f - y;
    }

    @Override
    public void onPause() {
    }

    public class TouchEventHandler implements Runnable {
        public int pointerId;
        public int action;
        public float x, y;
        public TouchEventHandler next = null;

        public TouchEventHandler(int action, int pointerId, float x, float y) {
            this.action = action;
            this.pointerId = pointerId;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            driveWheel.onTouch(action, pointerId, x, y);
            fireButton.onTouch(action, pointerId, x, y);
            map.updatePlayer(driveWheel.direction, fireButton.firing);
            touchEventHandlerPool.free(this);
        }
    }

    public class TouchEventHandlerPool {
        private TouchEventHandler firstAvailHandler = null;
        private int count = 0;
        private int maxSize;

        public TouchEventHandlerPool(int maxSize) {
            this.maxSize = maxSize;
        }

        public synchronized TouchEventHandler alloc(int action, int pointerId, float x, float y) {
            TouchEventHandler handler;
            if (firstAvailHandler != null) {
                handler = firstAvailHandler;
                handler.action = action;
                handler.pointerId = pointerId;
                handler.x = x;
                handler.y = y;
                firstAvailHandler = firstAvailHandler.next;
                --count;
            } else {
                handler = new TouchEventHandler(action, pointerId, x, y);
            }
            return handler;
        }

        public synchronized void free(TouchEventHandler handler) {
            if (count == maxSize) {
                return;
            }

            handler.next = firstAvailHandler;
            firstAvailHandler = handler;
            ++count;
        }
    }
}