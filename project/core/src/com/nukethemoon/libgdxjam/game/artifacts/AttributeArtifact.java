package com.nukethemoon.libgdxjam.game.artifacts;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

import org.w3c.dom.Attr;

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
	public TextureRegion getBackgroundTexture() {
		return Attribute.getSlotTexture(ownAttribute);
	}

	@Override
	public Actor getForeground() {
		return new Image(App.TEXTURES.findRegion("placeholder_alien"));
	}

	@Override
	public String getDescription() {
		return Attribute.getDescription(ownAttribute);
	}

	public String getName(){
		return Attribute.getName(ownAttribute);
	}

}
