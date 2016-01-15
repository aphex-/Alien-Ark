package com.nukethemoon.libgdxjam.screens.splash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class SplashScreen implements Screen, InputProcessor {

	private final Ani ani;
	private SpriteBatch batch;

	private Sprite splashScreen00;
	private Sprite splashScreen01;

	private Sprite currentSprite;
	private int nextCount = 0;


	public SplashScreen(InputMultiplexer multiplexer) {
		multiplexer.addProcessor(this);
		batch = new SpriteBatch();
		splashScreen00 = createCenteredSprite("splashScreen00.png");
		splashScreen01 = createCenteredSprite("splashScreen01.png");
		ani = new Ani();

		splashScreen00.setAlpha(0);
		splashScreen01.setAlpha(0);

		startAnimation(splashScreen00);
	}

	private void startAnimation(Sprite sprite) {
		currentSprite = sprite;
		BaseAnimation currentAnimation = new SpriteFadeAnimation(currentSprite, new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation baseAnimation) {
				next();
			}
		});
		ani.add(currentAnimation);
	}

	private void exit() {
		App.openSolarScreen();
	}

	private void next() {
		nextCount++;
		if (nextCount == 1) {
			startAnimation(splashScreen01);
		}
		if (nextCount == 2) {
			exit();
		}
	}

	private Sprite createCenteredSprite(String textureName) {
		Sprite sprite = new Sprite(new Texture(Gdx.files.internal("textures/" + textureName)));
		float halfScreenWidth = Gdx.graphics.getWidth() / 2;
		float halfScreenHeight = Gdx.graphics.getHeight() / 2;
		sprite.setPosition(halfScreenWidth - (sprite.getWidth() / 2f),
				halfScreenHeight - (sprite.getHeight() / 2));
		return sprite;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		batch.begin();
		if (currentSprite != null) {
			currentSprite.draw(batch);
		}
		batch.end();
		ani.update();
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
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		next();
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		next();
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
