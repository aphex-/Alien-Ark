package com.nukethemoon.libgdxjam.game.artifacts;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.game.Artifact;

public class ValueArtifact extends Artifact {

	private final float value;

	public ValueArtifact(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	@Override
	public TextureRegion getBackgroundTexture() {
		return App.TEXTURES.findRegion("slot00");
	}

	@Override
	public Actor createActor(float w, float h) {
		Actor a  = super.createActor(w,h);
		Group g = new Group();
		Label l = new Label(String.valueOf((int)value), Styles.UI_SKIN);
		l.setStyle(Styles.LABEL_VALUE_ARTIFACT);
		l.setAlignment(Align.center);
		l.setWidth(w);
		l.setHeight(h);
		g.addActor(a);
		g.addActor(l);
		return g;
	}

	@Override
	public String getDescription() {
		return "Gives you a value of " + value + " to combine it with an OPERATOR and an ATTRIBUTE.";
	}
}
