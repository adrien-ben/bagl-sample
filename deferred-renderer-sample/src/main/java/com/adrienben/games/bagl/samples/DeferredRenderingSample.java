package com.adrienben.games.bagl.samples;

import com.adrienben.games.bagl.core.Color;
import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.core.math.MathUtils;
import com.adrienben.games.bagl.deferred.PBRDeferredSceneRenderer;
import com.adrienben.games.bagl.engine.*;
import com.adrienben.games.bagl.engine.game.DefaultGame;
import com.adrienben.games.bagl.engine.rendering.Material;
import com.adrienben.games.bagl.engine.rendering.model.Mesh;
import com.adrienben.games.bagl.engine.rendering.model.MeshFactory;
import com.adrienben.games.bagl.engine.rendering.model.Model;
import com.adrienben.games.bagl.engine.rendering.sprite.Sprite;
import com.adrienben.games.bagl.engine.rendering.sprite.Spritebatch;
import com.adrienben.games.bagl.engine.rendering.text.Font;
import com.adrienben.games.bagl.engine.rendering.text.Text;
import com.adrienben.games.bagl.engine.rendering.text.TextRenderer;
import com.adrienben.games.bagl.engine.scene.GameObject;
import com.adrienben.games.bagl.engine.scene.Scene;
import com.adrienben.games.bagl.engine.scene.components.CameraComponent;
import com.adrienben.games.bagl.engine.scene.components.ModelComponent;
import com.adrienben.games.bagl.engine.scene.components.PointLightComponent;
import com.adrienben.games.bagl.engine.scene.components.SpotLightComponent;
import com.adrienben.games.bagl.opengl.shader.Shader;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class DeferredRenderingSample {

    public static void main(String[] args) {
        new Engine(new TestGame(), TestGame.TITLE).start();
    }

    private enum DisplayMode {
        SCENE, ALBEDO, NORMALS, DEPTH, EMISSIVE, SHADOW, UNPROCESSED
    }

    private static final class TestGame extends DefaultGame {

        private static final String TITLE = "Deferred Rendering";

        private static final String INSTRUCTIONS = "Display scene : F2\n"
                + "Display Albedo : F3\n"
                + "Display Normals : F4\n"
                + "Display Depth : F5\n"
                + "Display Emissive : F6\n"
                + "Display Shadow Maps : F7\n"
                + "Display Scene before post process : F8\n"
                + "Move camera : Z, Q, S, D, LCTRL, SPACE\n"
                + "Advance time: 1, 2\n"
                + "Toggle debug info: A";

        private int width;
        private int height;

        private TextRenderer textRenderer;
        private PBRDeferredSceneRenderer renderer;

        private Font font;

        private Scene scene;
        private Mesh pointBulb;
        private Mesh spotBulb;

        private Text toggleInstructionsText;
        private Text instructionsText;

        private Spritebatch spritebatch;
        private Shader depthBufferViewerShader;

        private DisplayMode displayMode = DisplayMode.SCENE;
        private Sprite albedoSprite;
        private Sprite normalSprite;
        private Sprite depthSprite;
        private Sprite emissiveSprite;
        private List<Sprite> shadowMaps;
        private Sprite preProcessSprite;
        private int shadowMapIndex;

        private boolean displayInstructions = false;
        private boolean fpsCamera = false;

        @Override
        public void init() {
            super.init();

            width = Configuration.getInstance().getXResolution();
            height = Configuration.getInstance().getYResolution();

            textRenderer = new TextRenderer();
            renderer = new PBRDeferredSceneRenderer();

            font = getAssetStore().getAsset("segoe", Font.class);

            scene = getAssetStore().getAsset("main_scene", Scene.class);
            loadMeshes();
            addBuldModelToLights();

            toggleInstructionsText = Text.create("Toggle instructions : F1", font, 0.01f, 0.97f, 0.03f, Color.BLACK);
            instructionsText = Text.create(INSTRUCTIONS, font, 0.01f, 0.94f, 0.03f, Color.BLACK);

            spritebatch = new Spritebatch(1024, width, height);
            depthBufferViewerShader = Shader.builder().vertexPath(ResourcePath.get("classpath:/shaders/sprite/sprite.vert"))
                    .fragmentPath(ResourcePath.get("classpath:/depth_buffer_viewer.frag")).build();

            albedoSprite = Sprite.builder().texture(renderer.getGBuffer().getColorTexture(0)).build();
            normalSprite = Sprite.builder().texture(renderer.getGBuffer().getColorTexture(1)).build();
            depthSprite = Sprite.builder().texture(renderer.getGBuffer().getDepthTexture()).build();
            emissiveSprite = Sprite.builder().texture(renderer.getGBuffer().getColorTexture(2)).build();
            shadowMaps = List.of(
                    Sprite.builder().texture(renderer.getCSMBuffer().get(0).getDepthTexture()).width(width).height(height).build(),
                    Sprite.builder().texture(renderer.getCSMBuffer().get(1).getDepthTexture()).width(width).height(height).build(),
                    Sprite.builder().texture(renderer.getCSMBuffer().get(2).getDepthTexture()).width(width).height(height).build(),
                    Sprite.builder().texture(renderer.getCSMBuffer().get(3).getDepthTexture()).width(width).height(height).build()
            );
            preProcessSprite = Sprite.builder().texture(renderer.getFinalBuffer().getColorTexture(0)).build();
        }

        @Override
        public void destroy() {
            super.destroy();
            textRenderer.destroy();
            renderer.destroy();
            pointBulb.destroy();
            spotBulb.destroy();
            spritebatch.destroy();
            depthBufferViewerShader.destroy();
        }

        private void loadMeshes() {
            pointBulb = MeshFactory.createSphere(0.1f, 8, 8);
            spotBulb = MeshFactory.createCylinder(0.1f, 0.065f, 0.2f, 12);
        }

        private void addBuldModelToLights() {
            scene.getObjectsByTag("point_lights").forEach(parent ->
                    parent.getComponentOfType(PointLightComponent.class).ifPresent(point ->
                            createBulb(parent, point.getLight().getColor(), point.getLight().getIntensity(), pointBulb)));

            scene.getObjectsByTag("spot_lights").forEach(parent ->
                    parent.getComponentOfType(SpotLightComponent.class).ifPresent(spot ->
                            createBulb(parent, spot.getLight().getColor(), spot.getLight().getIntensity(), spotBulb)));
        }

        private void createBulb(final GameObject parent, final Color color, final float intensity, final Mesh bulbModel) {
            final var modelObject = parent.createChild("bulb_" + parent.getId(), "debug");
            modelObject.getLocalTransform().setRotation(new Quaternionf().rotationX(MathUtils.toRadians(-90f)));

            final var material = Material.builder().emissive(color).emissiveIntensity(intensity).build();
            final var model = new Model();
            model.addNode().addMesh(bulbModel, material);
            final var modelComponent = new ModelComponent(model, false);
            modelObject.addComponent(modelComponent);
        }

        @Override
        public void update(final Time time) {
            scene.update(time);
            rotateSun(time);
            toggleInstructions();
            toggleDebug();
            selectDisplayMode();
            selectCameraMode();
        }

        private void rotateSun(final Time time) {
            if (Input.isKeyPressed(GLFW.GLFW_KEY_1) || Input.isKeyPressed(GLFW.GLFW_KEY_2)) {
                final var speed = Input.isKeyPressed(GLFW.GLFW_KEY_1) ? 20 : -20;
                scene.getObjectById("sun").ifPresent(sunObj -> {
                    final var transform = new Transform()
                            .setRotation(new Quaternionf().setAngleAxis(MathUtils.toRadians(speed * time.getElapsedTime()), 1f, 1f, 0f));
                    sunObj.getLocalTransform().transform(transform);
                });
            }
        }

        private void toggleInstructions() {
            if (Input.wasKeyPressed(GLFW.GLFW_KEY_F1)) {
                displayInstructions = !displayInstructions;
            }
        }

        private void toggleDebug() {
            if (Input.wasKeyPressed(GLFW.GLFW_KEY_Q)) {
                scene.getObjectsByTag("debug").forEach(obj -> obj.setEnabled(!obj.isEnabled()));
            }
        }

        private void selectDisplayMode() {
            if (Input.wasKeyPressed(GLFW.GLFW_KEY_F2)) {
                displayMode = DisplayMode.SCENE;
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F3)) {
                displayMode = DisplayMode.ALBEDO;
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F4)) {
                displayMode = DisplayMode.NORMALS;
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F5)) {
                displayMode = DisplayMode.DEPTH;
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F6)) {
                displayMode = DisplayMode.EMISSIVE;
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F7)) {
                if (displayMode == DisplayMode.SHADOW) {
                    shadowMapIndex = (shadowMapIndex + 1) % 4;
                } else {
                    displayMode = DisplayMode.SHADOW;
                    shadowMapIndex = 0;
                }
            } else if (Input.wasKeyPressed(GLFW.GLFW_KEY_F8)) {
                displayMode = DisplayMode.UNPROCESSED;
            }
        }

        private void selectCameraMode() {
            if (Input.wasKeyPressed(GLFW.GLFW_KEY_TAB)) {
                fpsCamera = !fpsCamera;
                if (fpsCamera) {
                    Input.setMouseMode(MouseMode.DISABLED);
                } else {
                    Input.setMouseMode(MouseMode.NORMAL);
                }
            }
        }

        @Override
        public void render() {
            renderer.render(scene);
            renderDisplayMode();
            renderInstructions();
        }

        private void renderDisplayMode() {
            spritebatch.start();
            if (displayMode == DisplayMode.ALBEDO) {
                spritebatch.render(albedoSprite);
            } else if (displayMode == DisplayMode.NORMALS) {
                spritebatch.render(normalSprite);
            } else if (displayMode == DisplayMode.EMISSIVE) {
                spritebatch.render(emissiveSprite);
            } else if (displayMode == DisplayMode.UNPROCESSED) {
                spritebatch.render(preProcessSprite);
            } else if (displayMode == DisplayMode.SHADOW) {
                spritebatch.render(shadowMaps.get(shadowMapIndex));
            }
            spritebatch.end();

            spritebatch.start(depthBufferViewerShader);
            if (displayMode == DisplayMode.DEPTH) {
                final var camera = scene.getObjectById("camera").orElseThrow().getComponentOfType(CameraComponent.class).orElseThrow().getCamera();
                setUniformsForDepthBufferViewer(camera.getzNear(), camera.getzFar());
                spritebatch.render(depthSprite);
            }
            spritebatch.end();
        }

        private void setUniformsForDepthBufferViewer(final float minDepth, final float maxDepth) {
            depthBufferViewerShader.bind();
            depthBufferViewerShader.setUniform("minDepth", minDepth);
            depthBufferViewerShader.setUniform("maxDepth", maxDepth);
            Shader.unbind();
        }

        private void renderInstructions() {
            textRenderer.render(toggleInstructionsText);
            if (displayInstructions) {
                textRenderer.render(instructionsText);
            }
        }
    }
}
