package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class CenteredTable extends Table {

	@Override
	public void pack() {
		super.pack();
		setPosition(
				(Gdx.graphics.getWidth() / 2) - (getWidth() / 2),
				(Gdx.graphics.getHeight() / 2) - (getHeight() / 2));

	}
}
