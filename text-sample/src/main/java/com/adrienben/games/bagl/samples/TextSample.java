package com.adrienben.games.bagl.samples;

import com.adrienben.games.bagl.core.Color;
import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.Engine;
import com.adrienben.games.bagl.engine.Time;
import com.adrienben.games.bagl.engine.game.Game;
import com.adrienben.games.bagl.engine.rendering.text.Font;
import com.adrienben.games.bagl.engine.rendering.text.Text;
import com.adrienben.games.bagl.engine.rendering.text.TextRenderer;
import com.adrienben.games.bagl.opengl.OpenGL;

/**
 * Text sample. Implementation of the signed distance field algorithm from Valve.
 */
public class TextSample implements Game {

    private static final String TITLE = "Text Sample";

    private Font arial;
    private Font segoe;
    private TextRenderer renderer;
    private Text text;

    public static void main(String[] args) {
        new Engine(new TextSample(), TITLE).start();
    }

    @Override
    public void init() {
        OpenGL.setClearColor(Color.CORNFLOWER_BLUE);
        arial = new Font(ResourcePath.get("classpath:/fonts/arial/arial.fnt"));
        segoe = new Font(ResourcePath.get("classpath:/fonts/segoe/segoe.fnt"));
        renderer = new TextRenderer();
        text = Text.create("|Hello Potatoe World", segoe, Color.BLACK);
    }

    @Override
    public void destroy() {
        arial.destroy();
        segoe.destroy();
        renderer.destroy();
    }

    @Override
    public void update(Time time) {
    }

    @Override
    public void render() {
        renderer.render(text.setY(1 - 0.25f).setScale(0.25f));
        renderer.render(text.setY(1 - 0.45f).setScale(0.20f));
        renderer.render(text.setY(1 - 0.63f).setScale(0.18f));
        renderer.render(text.setY(1 - 0.78f).setScale(0.15f));
        renderer.render(text.setY(1 - 0.9f).setScale(0.12f));
        renderer.render(text.setY(1 - 0.96f).setScale(0.06f));
        renderer.render(text.setY(0f).setScale(0.04f));
    }

}
