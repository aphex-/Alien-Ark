package com.nukethemoon.libgdxjam.game.artifacts.operators;

import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;


public class Decrease extends OperatorArtifact {
	@Override
	protected float applyOperator(float base, float value) {
		return base - value;
	}
}
