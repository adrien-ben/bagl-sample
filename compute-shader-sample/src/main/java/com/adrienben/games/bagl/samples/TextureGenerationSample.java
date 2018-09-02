package com.adrienben.games.bagl.samples;

import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.Configuration;
import com.adrienben.games.bagl.engine.Engine;
import com.adrienben.games.bagl.engine.Time;
import com.adrienben.games.bagl.engine.game.DefaultGame;
import com.adrienben.games.bagl.engine.rendering.sprite.Sprite;
import com.adrienben.games.bagl.engine.rendering.sprite.Spritebatch;
import com.adrienben.games.bagl.opengl.AccessMode;
import com.adrienben.games.bagl.opengl.OpenGL;
import com.adrienben.games.bagl.opengl.shader.Shader;
import com.adrienben.games.bagl.opengl.shader.compute.Barrier;
import com.adrienben.games.bagl.opengl.texture.Format;
import com.adrienben.games.bagl.opengl.texture.Texture2D;
import com.adrienben.games.bagl.opengl.texture.TextureParameters;

import java.util.EnumSet;

public class TextureGenerationSample extends DefaultGame {

    private Shader textureGenerateShader;
    private Texture2D generatedTexture;
    private Spritebatch spritebatch;
    private Sprite sprite;

    @Override
    public void init() {
        super.init();
        generatedTexture = new Texture2D(512, 512, TextureParameters.builder().format(Format.RGBA8).build());
        textureGenerateShader = Shader.computeBuilder().computePath(ResourcePath.get("classpath:/texture_generate.comp")).build();
        spritebatch = new Spritebatch(1024, Configuration.getInstance().getXResolution(), Configuration.getInstance().getYResolution());
        sprite = Sprite.builder().texture(generatedTexture).build();
    }

    @Override
    public void destroy() {
        super.destroy();
        textureGenerateShader.destroy();
        generatedTexture.destroy();
        spritebatch.destroy();
    }

    @Override
    public void update(Time time) {
        // generate texture
        generatedTexture.bindAsImageTexture(0, 0, AccessMode.WRITE_ONLY);
        textureGenerateShader.bind();
        textureGenerateShader.setUniform("uTime", time.getTotalTime());

        OpenGL.dispatchCompute(32, 32);
        OpenGL.setMemoryBarriers(EnumSet.of(Barrier.SHADER_IMAGE_ACCESS));

        Shader.unbind();
        generatedTexture.unbindAsImageTexture(0);
    }

    @Override
    public void render() {
        // render texture
        spritebatch.start();
        spritebatch.render(sprite);
        spritebatch.end();
    }

    public static void main(final String[] args) {
        new Engine(new TextureGenerationSample(), "Texture Generation Sample").start();
    }
}
