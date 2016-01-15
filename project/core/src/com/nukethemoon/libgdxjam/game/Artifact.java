package com.nukethemoon.libgdxjam.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public abstract class Artifact {
	private static int nextID;
	public final int ID = ++nextID;

	@Override
	public int hashCode() {
		return ID;
	}

	protected abstract TextureRegion getBackgroundTexture();

	public abstract String getDescription();

	public Actor createActor(float w, float h) {
		final Image bg = new Image(getBackgroundTexture());
		bg.setWidth(w);
		bg.setHeight(h);

		return bg;
	}
}
