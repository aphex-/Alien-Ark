package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;

public class EnterPlanetTable extends DialogTable {

	private final TextButton enterButton;
	private final int planetIndex;

	public EnterPlanetTable(Skin skin, int planetIndex) {
		super(skin, new String[] {"Do you want to enter", "the planets orbit?"}, "NAVIGATION");
		this.planetIndex = planetIndex;
		enterButton = new TextButton("ENTER", Styles.STYLE_BUTTON_02);

		TextureAtlas.AtlasRegion region = App.TEXTURES.findRegion("planet0" + (planetIndex + 1) + "_preview");
		if (region != null) {
			Image previewImage = new Image(region);
			content.add(previewImage);
			content.row();
		}

		content.add(enterButton).colspan(2);
		content.row();

		if (App.config.debugMode) {
			content.add(new Label("Index " + (planetIndex + 1), Styles.LABEL_01));
		}
		updatePosition(350, 50);
	}

	public void setClickListener(ClickListener clickListener) {
		enterButton.addListener(clickListener);
	}


	public int getPlanetIndex() {
		return planetIndex;
	}
}
