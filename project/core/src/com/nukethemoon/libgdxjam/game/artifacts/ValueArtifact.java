package com.nukethemoon.libgdxjam.game.artifacts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
	protected Actor getForeground() {
		Label label = new Label("" + value, Styles.UI_SKIN);
		Label.LabelStyle newStyle = new Label.LabelStyle(label.getStyle());
		newStyle.fontColor = Color.BLACK;
		label.setStyle(newStyle);
		label.setAlignment(Align.center);

		return label;
	}

	@Override
	public TextureRegion getBackgroundTexture() {
		return App.TEXTURES.findRegion("slot00");
	}

	@Override
	public String getDescription() {
		return "Gives you a value of " + value + " to combine it with an OPERATOR and an ATTRIBUTE.";
	}
}
