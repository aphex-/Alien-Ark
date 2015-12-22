package com.nukethemoon.libgdxjam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SplashScreen implements Screen {

	private SpriteBatch batch;
	private TextureAtlas textureAtlas;
	private Sprite ntmLogo;

	public SplashScreen() {
		textureAtlas = new TextureAtlas("textures/game.atlas");
		batch = new SpriteBatch();
		ntmLogo = new Sprite(textureAtlas.findRegion("ntm_logo"));
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(ntmLogo, 0, 0);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		textureAtlas.dispose();
		batch.dispose();
	}
}
