package com.nukethemoon.libgdxjam.game.artifacts;

import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

public class ValueArtifact extends Artifact {

	private final float value;

	public ValueArtifact(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}
}
