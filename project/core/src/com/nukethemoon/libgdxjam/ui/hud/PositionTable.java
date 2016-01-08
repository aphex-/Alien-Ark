package com.nukethemoon.libgdxjam.ui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.libgdxjam.Styles;

public class PositionTable extends Table {

	private Label labelX;
	private Label labelY;

	public PositionTable(Skin uiSkin) {
		labelX = new Label("", Styles.LABEL_HUD_NUMBERS);
		labelY = new Label("", Styles.LABEL_HUD_NUMBERS);

		add(labelX).left().width(100).fill().expand();
		add(labelY).right().width(100).fill().expand();
		pack();

	}

	public void setTilePosition(int x, int y) {
		labelX.setText("x " + x);
		labelY.setText("y " + y);
	}

	@Override
	public void pack() {
		super.pack();
		setPosition(Gdx.graphics.getWidth() - this.getWidth() - 10, 10);
	}
}
