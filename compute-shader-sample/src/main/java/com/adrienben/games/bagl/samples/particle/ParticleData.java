package com.adrienben.games.bagl.samples.particle;

import com.adrienben.games.bagl.core.Color;
import com.adrienben.games.bagl.opengl.buffer.Buffer;
import com.adrienben.games.bagl.opengl.buffer.BufferUsage;
import com.adrienben.games.bagl.opengl.vertex.VertexArray;
import com.adrienben.games.bagl.opengl.vertex.VertexBuffer;
import com.adrienben.games.bagl.opengl.vertex.VertexBufferParams;
import com.adrienben.games.bagl.opengl.vertex.VertexElement;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class ParticleData {

    public static final Color PARTICLE_COLOR = new Color(0.8f, 0.3f, 0.0001f, 1.0f);
    public static final int PARTICLE_COUNT = 2097152;

    private VertexArray vertexArray;
    private VertexBuffer vertexBuffer;
    private Buffer particleBuffer;

    public ParticleData() {
        vertexBuffer = new VertexBuffer(initVertices(), VertexBufferParams.builder().element(new VertexElement(0, 4)).build());
        vertexArray = new VertexArray();
        vertexArray.bind();
        vertexArray.attachVertexBuffer(vertexBuffer);
        vertexArray.unbind();
        particleBuffer = initParticles();
    }

    private FloatBuffer initVertices() {
        final var positions = MemoryUtil.memAllocFloat(4*PARTICLE_COUNT);
        MemoryUtil.memSet(positions, 0);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            positions.put(4*i + 3, 1.0f);
        }
        return positions;
    }

    private Buffer initParticles() {
        final var velocities = MemoryUtil.memAllocFloat(4*PARTICLE_COUNT);
        MemoryUtil.memSet(velocities, 0);
        return new Buffer(velocities, BufferUsage.STATIC_READ);
    }

    public void destroy() {
        particleBuffer.destroy();
        vertexBuffer.destroy();
        vertexArray.destroy();
    }

    public VertexArray getVertexArray() {
        return vertexArray;
    }

    public VertexBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public Buffer getParticleBuffer() {
        return particleBuffer;
    }
}
