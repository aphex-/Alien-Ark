package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class RocketMainTable extends Table {

	private ProgressBar shieldBar;
	private Label shieldLabel;

	private ProgressBar fuelBar;
	private Label fuelLabel;

	public RocketMainTable(Skin skin) {
		setSkin(skin);

		shieldLabel = new Label("", skin);
		add(shieldLabel);
		shieldBar = new ProgressBar(0, 100, 0.1f, false, getSkin());
		add(shieldBar);

		row();
		fuelLabel = new Label("", skin);
		add(fuelLabel);
		fuelBar = new ProgressBar(0, 100, 0.1f, false, getSkin());
		add(fuelBar);

		pack();
	}

	public void setShieldValue(int currentValue, int maxValue) {
		shieldBar.setValue(((float) currentValue / (float) maxValue) * 100);
		shieldLabel.setText("shield " + currentValue);
		pack();
	}

	public void setFuelValue(int currentValue, int maxValue) {
		fuelBar.setValue(((float) currentValue / (float) maxValue) * 100);
		fuelLabel.setText("fuel " + currentValue);
		pack();
	}


	@Override
	public void pack() {
		super.pack();
		setPosition(
				Gdx.graphics.getWidth() - getWidth() - 10,
				Gdx.graphics.getHeight() - getHeight() - 10);
	}


}
