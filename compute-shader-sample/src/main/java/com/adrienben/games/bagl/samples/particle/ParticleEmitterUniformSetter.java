package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.opengl.shader.Shader;

public class ParticleEmitterUniformSetter {

    private final Shader shader;

    public ParticleEmitterUniformSetter(final Shader shader) {
        this.shader = shader;
    }

    public void setParticleEmitterUniform(final ParticleEmitter particleEmitter) {
        shader.setUniform("uEmitter.position", particleEmitter.getPosition());
        shader.setUniform("uEmitter.batchSize", particleEmitter.getBatchSize());
        shader.setUniform("uEmitter.isEmitting", particleEmitter.isEmitting());
    }
}
