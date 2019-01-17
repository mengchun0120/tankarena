package com.crazygame.tankarena.gameobj;

import android.opengl.GLES20;
import android.util.Log;

import com.crazygame.tankarena.opengl.SimpleShaderProgram;

public class Explosion extends GameObject {
    private static int count = 0;
    public final int template_id;
    public float curTime;

    public Explosion(int template_id, float x, float y) {
        super("e" + (count++));
        this.template_id = template_id;
        position[0] = x;
        position[1] = y;
        curTime = 0f;
    }

    @Override
    public void draw(SimpleShaderProgram simpleShaderProgram) {
        ExplosionTemplate template = ExplosionTemplate.templates[template_id];
        simpleShaderProgram.setUseTime(true);
        simpleShaderProgram.setTimeRelated(template.directionBuffer, template.speedsBuffer,
                position, template.color, curTime, template.duration);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, template.numPoints);
        flag |= FLAG_DRAWN;
    }

    public void update(Map map, float timeDelta) {
        ExplosionTemplate template = ExplosionTemplate.templates[template_id];
        curTime += timeDelta;
        if(curTime >= ExplosionTemplate.templates[template_id].duration) {
            map.removeObject(this);
        } else {
            flag |= FLAG_UPDATED;
        }
    }

    @Override
    public float leftBound() {
        return position[0] - ExplosionTemplate.templates[template_id].breath;
    }

    @Override
    public float rightBound() {
        return position[0] + ExplosionTemplate.templates[template_id].breath;
    }

    @Override
    public float topBound() {
        return position[1] + ExplosionTemplate.templates[template_id].breath;
    }

    @Override
    public float bottomBound() {
        return position[1] + ExplosionTemplate.templates[template_id].breath;
    }

    @Override
    public float leftCollisionBound() {
        return 0;
    }

    @Override
    public float rightCollisionBound() {
        return 0;
    }

    @Override
    public float topCollisionBound() {
        return 0;
    }

    @Override
    public float bottomCollisionBound() {
        return 0;
    }
}
