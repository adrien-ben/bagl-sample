package com.adrienben.games.bagl.samples;

import com.adrienben.games.bagl.core.Color;
import com.adrienben.games.bagl.engine.Engine;
import com.adrienben.games.bagl.engine.Time;
import com.adrienben.games.bagl.engine.game.Game;
import com.adrienben.games.bagl.engine.rendering.shape.UIRenderer;
import com.adrienben.games.bagl.opengl.BlendMode;
import com.adrienben.games.bagl.opengl.OpenGL;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * {@link UIRenderer} sample class.
 * <p>
 * Renders a bunch of randomly generated boxes.
 */
public class UIRenderingSample implements Game {

    private static final String TITLE = "Shape Rendering Sample";
    private static final int SHAPE_COUNT = 5000;

    private UIRenderer renderer;
    private List<Vector2f> positions;
    private List<Vector2f> sizes;
    private List<Color> colors;

    public static void main(String[] args) {
        new Engine(new UIRenderingSample(), TITLE).start();
    }

    @Override
    public void init() {
        OpenGL.setClearColor(Color.CORNFLOWER_BLUE);
        OpenGL.setBlendMode(BlendMode.TRANSPARENCY);
        this.renderer = new UIRenderer();
        this.positions = new ArrayList<>();
        this.sizes = new ArrayList<>();
        this.colors = new ArrayList<>();
        final var random = new Random();
        for (var i = 0; i < SHAPE_COUNT; i++) {
            this.positions.add(new Vector2f(random.nextFloat(), random.nextFloat()));
            this.sizes.add(new Vector2f(random.nextFloat() * 0.3f, random.nextFloat() * 0.3f));
            this.colors.add(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat() * 0.4f + 0.6f));
        }
    }

    @Override
    public void destroy() {
        this.renderer.destroy();
    }

    @Override
    public void update(Time time) {
    }

    @Override
    public void render() {
        this.renderer.start();
        for (var i = 0; i < SHAPE_COUNT; i++) {
            final Vector2f position = this.positions.get(i);
            final Vector2f size = this.sizes.get(i);
            final Color color = this.colors.get(i);
            this.renderer.renderBox(position.x(), position.y(), size.x(), size.y(), color);
        }
        this.renderer.end();
    }

}
