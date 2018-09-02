package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.Input;
import com.adrienben.games.bagl.engine.Time;
import com.adrienben.games.bagl.opengl.OpenGL;
import com.adrienben.games.bagl.opengl.shader.Shader;
import com.adrienben.games.bagl.opengl.shader.compute.Barrier;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import java.util.EnumSet;

public class ParticleUpdater {

    private final Shader shader;
    private final ParticleEmitterUniformSetter particleEmitterUniformSetter;
    private final Vector3f centerOfMass = new Vector3f();
    private ParticleEmitter emitter;
    private ParticleBuffer particles;

    public ParticleUpdater() {
        shader = Shader.computeBuilder()
                .computePath(ResourcePath.get("classpath:/particles.comp"))
                .build();
        particleEmitterUniformSetter = new ParticleEmitterUniformSetter(shader);
    }

    public void destroy() {
        shader.destroy();
    }

    public void update(final Time time) {
        preDispatchSetUp(time);
        OpenGL.dispatchCompute(2048);
        OpenGL.setMemoryBarriers(EnumSet.of(Barrier.SHADER_STORAGE, Barrier.VERTEX_ATTRIB_ARRAY, Barrier.ATOMIC_COUNTER));
        postDispatchCleanUp();
        particles.swap();
    }

    private void preDispatchSetUp(final Time time) {
        emitter.update(time);
        shader.bind();
        particleEmitterUniformSetter.setParticleEmitterUniform(emitter);
        shader.setUniform("uDelta", time.getElapsedTime());
        shader.setUniform("uCenterOfMass", centerOfMass);
        shader.setUniform("uHasGravity", Input.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2));
        particles.resetCounters();
        particles.bind();
    }

    private void postDispatchCleanUp() {
        particles.unbind();
        Shader.unbind();
    }

    public void setEmitter(final ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    public void setParticles(final ParticleBuffer particles) {
        this.particles = particles;
    }

    public void setCenterOfMass(final Vector3fc centerOfMass) {
        this.centerOfMass.set(centerOfMass);
    }
}
