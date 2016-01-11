package com.nukethemoon.libgdxjam.game.artifacts.operators;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;

public class Increase extends OperatorArtifact {

	@Override
	protected float applyOperator(float base, float value) {
		return base + value;
	}

	@Override
	public TextureRegion getBackgroundTexture() {
		return App.TEXTURES.findRegion("slotOperatorPlus");
	}

	@Override
	public String getDescription() {
		return "Takes a given VALUE and ADDS it to a provided ATTRIBUTE's original value";
	}

	@Override
	public String getAlienVerb() {
		return "INCREASES";
	}
}
