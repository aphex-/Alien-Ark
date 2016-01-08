package com.nukethemoon.libgdxjam.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;

public abstract class Artifact {
	private static int nextID;
	public final int ID = ++nextID;

	@Override
	public int hashCode() {
		return ID;
	}

	public TextureRegion getTexture() {
		return App.TEXTURES.findRegion("slot01");
	}
}
