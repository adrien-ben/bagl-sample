package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.core.utils.DoubleBuffer;
import com.adrienben.games.bagl.opengl.buffer.AtomicCounter;
import com.adrienben.games.bagl.opengl.buffer.BufferTarget;
import com.adrienben.games.bagl.opengl.vertex.VertexArray;

import static com.adrienben.games.bagl.samples.particle.ParticleData.PARTICLE_COUNT;

public class ParticleBuffer {

    private final DoubleBuffer<ParticleData> particleData;
    private final AtomicCounter aliveParticleCounter;
    private final AtomicCounter deadParticleCounter;
    private final AtomicCounter emittedParticlesCounter;

    public ParticleBuffer() {
        particleData = new DoubleBuffer<>(ParticleData::new);
        aliveParticleCounter = new AtomicCounter();
        deadParticleCounter = new AtomicCounter(PARTICLE_COUNT);
        emittedParticlesCounter = new AtomicCounter();
    }

    public void destroy() {
        particleData.apply(ParticleData::destroy);
        aliveParticleCounter.destroy();
        deadParticleCounter.destroy();
        emittedParticlesCounter.destroy();
    }

    public void bind() {
        particleData.getReadBuffer().getVertexBuffer().getBuffer().bind(BufferTarget.SHADER_STORAGE, 0);
        particleData.getReadBuffer().getParticleBuffer().bind(BufferTarget.SHADER_STORAGE, 1);
        particleData.getWriteBuffer().getVertexBuffer().getBuffer().bind(BufferTarget.SHADER_STORAGE, 2);
        particleData.getWriteBuffer().getParticleBuffer().bind(BufferTarget.SHADER_STORAGE, 3);
        aliveParticleCounter.bind(0);
        deadParticleCounter.bind(1);
        emittedParticlesCounter.bind(2);
    }

    public void unbind() {
        emittedParticlesCounter.unbind(2);
        deadParticleCounter.unbind(1);
        aliveParticleCounter.unbind(0);
        particleData.getReadBuffer().getParticleBuffer().unbind(BufferTarget.SHADER_STORAGE, 1);
        particleData.getReadBuffer().getVertexBuffer().getBuffer().unbind(BufferTarget.SHADER_STORAGE, 0);
        particleData.getWriteBuffer().getParticleBuffer().unbind(BufferTarget.SHADER_STORAGE, 2);
        particleData.getWriteBuffer().getVertexBuffer().getBuffer().unbind(BufferTarget.SHADER_STORAGE, 3);
    }

    public void swap() {
        particleData.swap();
    }

    public void resetCounters() {
        aliveParticleCounter.reset();
        deadParticleCounter.reset(PARTICLE_COUNT);
        emittedParticlesCounter.reset();
    }

    public int getAliveParticleCount() {
        return aliveParticleCounter.getValue();
    }

    public VertexArray getVertexArray() {
        return particleData.getReadBuffer().getVertexArray();
    }
}
