package com.nukethemoon.libgdxjam.game.artifacts;

import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;

public class AttributeArtifact extends Artifact {

	private final Class<? extends Attribute> ownAttribute;

	public AttributeArtifact(Class<? extends Attribute> attribute) {
		this.ownAttribute = attribute;
	}

	public void apply(Attribute attribute, OperatorArtifact operator, ValueArtifact value) {
		if (ownAttribute == attribute.getClass()) {
			operator.apply(attribute, value);
		}
	}


}
