package com.nukethemoon.libgdxjam.screens.planet.helper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

public class SphereTextureProvider extends TextureProvider.FileTextureProvider {

	private String planetId;

	public SphereTextureProvider(String planetId) {
		this.planetId = planetId;
	}


	@Override
	public Texture load(String fileName) {
		return super.load("textures/spheres/" + planetId + ".png");
	}
}
