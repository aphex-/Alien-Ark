package com.nukethemoon.libgdxjam.game.artifacts.operators;

import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;

/**
 * Created by lovis on 23.12.15.
 */
public class Increase extends OperatorArtifact {

	@Override
	protected float applyOperator(float base, float value) {
		return base + value;
	}
}
