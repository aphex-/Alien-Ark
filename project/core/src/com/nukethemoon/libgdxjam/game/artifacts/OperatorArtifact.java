package com.nukethemoon.libgdxjam.game.artifacts;

import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;


public abstract class OperatorArtifact extends Artifact {

	public void apply(Attribute attribute, ValueArtifact value) {
		attribute.setCurrentValue(applyOperator(attribute.getCurrentValue(), value.getValue()));
	}

	protected abstract float applyOperator(float base, float value);
}
