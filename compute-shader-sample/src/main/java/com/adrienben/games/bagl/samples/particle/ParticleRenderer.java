package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.camera.Camera;
import com.adrienben.games.bagl.engine.rendering.shaders.CameraUniformSetter;
import com.adrienben.games.bagl.opengl.shader.Shader;

import static com.adrienben.games.bagl.samples.particle.ParticleData.PARTICLE_COLOR;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;

public class ParticleRenderer {

    private final Shader shader;
    private final CameraUniformSetter cameraUniformSetter;
    private ParticleBuffer particles;
    private Camera camera;

    public ParticleRenderer() {
        shader = Shader.pipelineBuilder()
                .vertexPath(ResourcePath.get("classpath:/particles.vert"))
                .fragmentPath(ResourcePath.get("classpath:/particles.frag"))
                .build();
        cameraUniformSetter = new CameraUniformSetter(shader);
    }

    public void destroy() {
        shader.destroy();
    }

    public void render() {
        particles.getVertexArray().bind();
        shader.bind();
        shader.setUniform("uColor", PARTICLE_COLOR);
        cameraUniformSetter.setViewProjectionUniform(camera);
        glDrawArrays(GL_POINTS, 0, particles.getAliveParticleCount());
        Shader.unbind();
        particles.getVertexArray().unbind();
    }

    public void setParticles(final ParticleBuffer particles) {
        this.particles = particles;
    }

    public void setCamera(final Camera camera) {
        this.camera = camera;
    }
}
