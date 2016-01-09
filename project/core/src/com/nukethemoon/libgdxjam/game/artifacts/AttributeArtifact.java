package com.nukethemoon.libgdxjam.game.artifacts;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
import com.nukethemoon.libgdxjam.game.attributes.Inertia;
import com.nukethemoon.libgdxjam.game.attributes.ItemCollectRadius;
import com.nukethemoon.libgdxjam.game.attributes.LandingDistance;
import com.nukethemoon.libgdxjam.game.attributes.Luck;
import com.nukethemoon.libgdxjam.game.attributes.MaxFuel;
import com.nukethemoon.libgdxjam.game.attributes.Shield;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

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

	@Override
	public TextureRegion getTexture() {
		return Attribute.getSlotTexture(ownAttribute);
	}
}
