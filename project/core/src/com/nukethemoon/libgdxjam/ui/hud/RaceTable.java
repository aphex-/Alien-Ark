package com.nukethemoon.libgdxjam.ui.hud;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.libgdxjam.Styles;

public class RaceTable extends Table {

	private final Label timeLabel;

	public RaceTable() {
		setBackground(Styles.NINE_PATCH_BUTTON_BG_01);
		timeLabel = new Label("", Styles.LABEL_DEV);
		add(timeLabel).width(100);
		pack();
	}

	public void setTime(float time) {
		timeLabel.setText(time + "");
	}
}
