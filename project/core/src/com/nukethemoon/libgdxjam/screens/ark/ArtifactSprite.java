package com.nukethemoon.libgdxjam.screens.ark;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;

/**
 * Adds an Artifact to a sprite. Makes some things easier, but is a bit hacky.
 * Don't use in AAA code!
 */
class ArtifactSprite extends Sprite {
	private Artifact artifact;

	public ArtifactSprite(Artifact artifact) {
		this.artifact = artifact;

		TextureRegion region;
		if(artifact instanceof AttributeArtifact){
			region = App.TEXTURES.findRegion("placeholder_square");
		} else if(artifact instanceof ValueArtifact){
			region = App.TEXTURES.findRegion("placeholder_kreis");
		} else {
			region = App.TEXTURES.findRegion("placeholder_tri");
		}

		setRegion(region);
		setColor(1, 1, 1, 1);
		setSize(region.getRegionWidth(), region.getRegionHeight());
		setOrigin(getWidth() / 2, getHeight() / 2);
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public Artifact getArtifact() {
		return artifact;
	}
}
