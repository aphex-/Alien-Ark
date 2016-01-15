package com.nukethemoon.libgdxjam.ui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;

public class RaceTable extends Table {

	private final Label timeLabel;

	public RaceTable() {
		defaults().bottom().pad(0);
		bottom();

		TextureAtlas.AtlasRegion raceTimeBGRegion = App.TEXTURES.findRegion("raceTimeBG");
		TextureRegionDrawable raceTimeBG = new TextureRegionDrawable(raceTimeBGRegion);
		setBackground(raceTimeBG);
		timeLabel = new Label("", Styles.LABEL_RACE_TIME);
		add(timeLabel).bottom();

		setWidth(raceTimeBGRegion.getRegionWidth());
		setHeight(raceTimeBGRegion.getRegionHeight());

		setPosition((Gdx.graphics.getWidth() / 2) - (raceTimeBGRegion.getRegionWidth() / 2), 5);
	}

	public void setTime(float time) {
		timeLabel.setText(time + "s");
	}
}
