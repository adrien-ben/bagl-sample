package com.adrienben.games.bagl.samples;

import com.adrienben.games.bagl.core.math.MathUtils;
import com.adrienben.games.bagl.core.math.Vectors;
import com.adrienben.games.bagl.engine.*;
import com.adrienben.games.bagl.engine.camera.Camera;
import com.adrienben.games.bagl.engine.camera.CameraController;
import com.adrienben.games.bagl.engine.camera.FPSCameraController;
import com.adrienben.games.bagl.engine.game.DefaultGame;
import com.adrienben.games.bagl.engine.rendering.postprocess.PostProcessor;
import com.adrienben.games.bagl.engine.rendering.postprocess.steps.BloomStep;
import com.adrienben.games.bagl.opengl.BlendMode;
import com.adrienben.games.bagl.opengl.FrameBuffer;
import com.adrienben.games.bagl.opengl.FrameBufferParameters;
import com.adrienben.games.bagl.opengl.OpenGL;
import com.adrienben.games.bagl.opengl.texture.Format;
import com.adrienben.games.bagl.samples.particle.ParticleBuffer;
import com.adrienben.games.bagl.samples.particle.ParticleEmitter;
import com.adrienben.games.bagl.samples.particle.ParticleRenderer;
import com.adrienben.games.bagl.samples.particle.ParticleUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGetIntegeri_v;
import static org.lwjgl.opengl.GL43.GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS;

public class GPUParticlesSample extends DefaultGame {

    private static final Logger LOG = LogManager.getLogger(GPUParticlesSample.class);

    private ParticleEmitter particleEmitter = new ParticleEmitter(Vectors.VEC3_ZERO, 1000, 0.02f);
    private ParticleBuffer particleBuffer;
    private ParticleUpdater particleUpdater;
    private ParticleRenderer particleRenderer;

    private Camera camera;
    private CameraController cameraController;
    private Vector3f massCenter = new Vector3f();
    private boolean updateParticles = true;
    private FrameBuffer frameBuffer;
    private PostProcessor postProcessor;

    @Override
    public void init() {
        super.init();
        displayComputeCapabilities();
        OpenGL.setBlendMode(BlendMode.ADDITIVE);
        OpenGL.disableDepthWrite();
        glDisable(GL_DEPTH_TEST);
        Input.setMouseMode(MouseMode.DISABLED);

        particleBuffer = new ParticleBuffer();
        particleUpdater = new ParticleUpdater();
        particleRenderer = new ParticleRenderer();

        initCamera();
        frameBuffer = new FrameBuffer(Configuration.getInstance().getXResolution(), Configuration.getInstance().getYResolution(),
                FrameBufferParameters.builder().colorOutputFormat(Format.RGB16F).depthStencilTextureParameters(null).build());
        postProcessor = new PostProcessor(
                new BloomStep(Configuration.getInstance().getXResolution(), Configuration.getInstance().getYResolution()));
    }

    private void displayComputeCapabilities() {
        try (final MemoryStack memoryStack = MemoryStack.stackPush()) {
            final var intBuffer = memoryStack.mallocInt(1);
            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 0, intBuffer);
            LOG.info("Max compute work group count (x): {}", intBuffer.get(0));
            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, intBuffer);
            LOG.info("Max compute work group size (x): {}", intBuffer.get(0));

            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 1, intBuffer);
            LOG.info("Max compute work group count (y): {}", intBuffer.get(0));
            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, intBuffer);
            LOG.info("Max compute work group size (y): {}", intBuffer.get(0));

            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_COUNT, 2, intBuffer);
            LOG.info("Max compute work group count (z): {}", intBuffer.get(0));
            glGetIntegeri_v(ARBComputeShader.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, intBuffer);
            LOG.info("Max compute work group size (z): {}", intBuffer.get(0));
        }
        LOG.info("Max atomic counters for compute shader: {}", glGetInteger(GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS));
    }

    private void initCamera() {
        camera = new Camera(new Vector3f(0, 0, 30), new Vector3f(0, 0, -1), Vectors.VEC3_UP, MathUtils.toRadians(60),
                (float) Configuration.getInstance().getXResolution()/(float) Configuration.getInstance().getYResolution(),
                0.1f, 1000f);
        final var controller = new FPSCameraController(camera);
        controller.setMovementSpeed(30);
        cameraController = controller;
    }

    @Override
    public void destroy() {
        super.destroy();
        particleBuffer.destroy();
        particleUpdater.destroy();
        particleRenderer.destroy();
        frameBuffer.destroy();
    }

    @Override
    public void update(final Time time) {
        cameraController.update(time);
        toggleParticleUpdate();
        if (Input.wasMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            randomizeCenterOfMass();
        }
        if (updateParticles) {
            updateParticles(time);
        }
    }

    private void toggleParticleUpdate() {
        if (Input.wasKeyPressed(GLFW.GLFW_KEY_E)) {
            updateParticles = !updateParticles;
        }
    }

    private void randomizeCenterOfMass() {
        massCenter.setComponent(0, MathUtils.random(-30, 30));
        massCenter.setComponent(1, MathUtils.random(-30, 30));
        massCenter.setComponent(2, MathUtils.random(-30, 30));
    }

    private void updateParticles(final Time time) {
        particleUpdater.setEmitter(particleEmitter);
        particleUpdater.setCenterOfMass(massCenter);
        particleUpdater.setParticles(particleBuffer);
        particleUpdater.update(time);
    }

    @Override
    public void render() {
        frameBuffer.bind();
        frameBuffer.clear();
        particleRenderer.setCamera(camera);
        particleRenderer.setParticles(particleBuffer);
        particleRenderer.render();
        frameBuffer.unbind();
        postProcessor.process(frameBuffer.getColorTexture(0));
    }

    public static void main(final String[] args) {
        new Engine(new GPUParticlesSample(), "Vertex Update Sample").start();
    }
}
