package com.adrienben.games.bagl.samples;

import com.adrien.games.bagl.core.*;
import com.adrien.games.bagl.rendering.BlendMode;
import com.adrien.games.bagl.rendering.sprite.Sprite;
import com.adrien.games.bagl.rendering.sprite.Spritebatch;
import com.adrien.games.bagl.rendering.texture.Texture;
import com.adrien.games.bagl.rendering.texture.TextureParameters;
import com.adrien.games.bagl.utils.ResourcePath;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpritebatchSample {

    public static void main(String[] args) {
        new Engine(new TestGame(), TestGame.TITLE).start();
    }

    private static final class TestGame implements Game {

        private static final String TITLE = "Spritebatch";
        private static final int SPRITE_COUNT = 10000;
        private final List<Sprite> sprites = new ArrayList<>();
        private int width;
        private int height;
        private Texture texture;
        private Spritebatch spritebatch;

        @Override
        public void init() {
            this.width = Configuration.getInstance().getXResolution();
            this.height = Configuration.getInstance().getYResolution();

            this.spritebatch = new Spritebatch(1024, this.width, this.height);

            this.texture = Texture.fromFile(ResourcePath.get("classpath:/default.png"), true, TextureParameters.builder());

            final var r = new Random();
            for (var i = 0; i < SPRITE_COUNT; i++) {
                final var size = r.nextInt(32) + 32;
                final var sprite = Sprite.builder()
                        .texture(texture)
                        .position(new Vector2f(r.nextFloat() * this.width, r.nextFloat() * this.height))
                        .width(size)
                        .height(size)
                        .rotation(r.nextFloat() * 360)
                        .build();
                sprites.add(sprite);
            }

            Engine.setBlendMode(BlendMode.TRANSPARENCY);
            Engine.setClearColor(Color.CORNFLOWER_BLUE);
        }

        @Override
        public void update(Time time) {
            sprites.forEach(sprite -> sprite.setRotation(sprite.getRotation() + time.getElapsedTime() * 50));
        }

        @Override
        public void render() {
            this.spritebatch.start();
            this.sprites.forEach(spritebatch::render);
            this.spritebatch.end();
        }

        @Override
        public void destroy() {
            this.texture.destroy();
        }

    }
}
