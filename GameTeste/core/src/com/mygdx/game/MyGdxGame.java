package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class MyGdxGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private TextureAtlas copoAtlas;
    private Animation<TextureRegion> animation;
    private float stateTime;

    @Override
    public void create () {
        batch = new SpriteBatch();
        copoAtlas = new TextureAtlas(Gdx.files.internal("C:/Users/Nivaldo/Desktop/LibGDX/GameTeste/assets/copo.atlas"));
        Array<TextureAtlas.AtlasRegion> regions = copoAtlas.findRegions("copo");
        Array<TextureRegion> frames = new Array<>();
        frames.addAll(regions);
        float frameDuration = 0.1f;
        animation = new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        stateTime = 0;
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);
        float x = stateTime * 100; // Ajuste a velocidade de movimento multiplicando o tempo por uma constante (100 neste caso)
        batch.begin();
        batch.draw(currentFrame, x, 0);
        batch.end();
    }

    @Override
    public void dispose () {
        batch.dispose();
        copoAtlas.dispose();
    }
}

