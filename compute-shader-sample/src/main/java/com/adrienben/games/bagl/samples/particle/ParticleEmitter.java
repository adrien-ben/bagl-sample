package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.engine.Time;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ParticleEmitter {

    private final Vector3f position;
    private final int batchSize;
    private final float rate;

    private float timeBeforeEmission;
    private boolean isEmitting;

    public ParticleEmitter(final Vector3fc position, final int batchSize, final float rate) {
        this.position = new Vector3f(position);
        this.batchSize = batchSize;
        this.rate = rate;
        this.timeBeforeEmission = rate;
        this.isEmitting = false;
    }

    public void update(final Time time) {
        isEmitting = false;
        timeBeforeEmission -= time.getElapsedTime();
        if (timeBeforeEmission <= 0) {
            isEmitting = true;
            timeBeforeEmission = rate;
        }
    }

    public Vector3fc getPosition() {
        return position;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isEmitting() {
        return isEmitting;
    }
}
