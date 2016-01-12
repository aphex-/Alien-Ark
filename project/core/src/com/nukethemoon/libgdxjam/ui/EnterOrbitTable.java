package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;

public class EnterOrbitTable extends DialogTable {

	private final TextButton enterButton;
	private final int planetIndex;

	public EnterOrbitTable(Skin skin, int planetIndex) {
		super(skin, new String[] {"Do you want to enter", "the planets orbit?", "planet index " + planetIndex}, "NAVIGATION");
		this.planetIndex = planetIndex;
		enterButton = new TextButton("ENTER", Styles.STYLE_BUTTON_02);
		content.add(enterButton).colspan(2);
		updatePosition(350, 50);
	}

	public void setClickListener(ClickListener clickListener) {

	}


	public int getPlanetIndex() {
		return planetIndex;
	}
}
