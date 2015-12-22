package com.nukethemoon.libgdxjam.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.libgdxjam.Log;

public class MenuScreen implements Screen {

	private Stage stage;

	private Table contentTable;

	public MenuScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);

		contentTable = new Table(uiSkin).debug();
		contentTable.setFillParent(true);

		TextButton button01 = new TextButton("Button01", uiSkin);
		button01.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Log.l(MenuScreen.this.getClass(), "Button01 clicked");
			}
		});
		contentTable.add(button01);
		contentTable.row();

		TextButton button02 = new TextButton("Button02", uiSkin);
		button02.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Log.l(MenuScreen.this.getClass(), "Button02 clicked");
			}
		});
		contentTable.add(button02);


		stage.addActor(contentTable);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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

	}
}
