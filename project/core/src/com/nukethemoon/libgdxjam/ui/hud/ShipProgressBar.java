package com.nukethemoon.libgdxjam.ui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.ShieldCapacity;

public class ShipProgressBar extends Table {

	private final Label valueLabel;
	private final ProgressTable progressTable;

	public enum ProgressType {
		SHIELD(new Vector2(), new Vector2(), new Vector2()),
		FUEL(new Vector2(), new Vector2(), new Vector2());

		public final Vector2 textOffset1;
		public final Vector2 textOffset2;
		public final Vector2 progressOffset;

		ProgressType(Vector2 textOffset1, Vector2 textOffset2, Vector2 progressOffset) {
			this.textOffset1 = textOffset1;
			this.textOffset2 = textOffset2;
			this.progressOffset = progressOffset;
		}
	}

	private ProgressType progressType;

	public ShipProgressBar(ProgressType progressType) {

		this.progressType = progressType;
		TextureAtlas.AtlasRegion bgTextureRegion = App.TEXTURES.findRegion("FuelShieldBackground");
		Sprite bgSprite = new Sprite(bgTextureRegion);

		Label label = new Label(progressType.name(), Styles.LABEL_PROGRESS_TYPE);
		progressTable = new ProgressTable(progressType);
		valueLabel = new Label("0", Styles.LABEL_HUD_NUMBERS);

		if (progressType == ProgressType.SHIELD) {
			bgSprite.flip(true, false);
			right().bottom();
			add(valueLabel).width(100);
			add(progressTable).right().width(205).padLeft(5).padRight(5);
			add(label).right().width(76).fill().padLeft(7);
		} else {
			left().bottom();
			add(label).left().width(73).fill().padLeft(10);
			add(progressTable).width(205).padLeft(5).padRight(5);
			add(valueLabel).width(100).padLeft(5);
		}
		setBackground(new SpriteDrawable(bgSprite));
		setWidth(bgTextureRegion.getRegionWidth());
		setHeight(bgTextureRegion.getRegionHeight());

		pack();
		applyPosition();
	}

	private void applyPosition() {
		if (progressType == ProgressType.SHIELD) {
			setPosition(Gdx.graphics.getWidth() - this.getWidth() - 5, 5);
		} else {
			setPosition(5, 5);
		}
	}

	public void setValue(int value, int maxShield) {
		int userValue;
		if (progressType == ProgressType.SHIELD) {
			userValue = SpaceShipProperties.toUserValue(value, ShieldCapacity.INTERNAL_MIN, ShieldCapacity.INTERNAL_MAX);
		} else {
			userValue = SpaceShipProperties.toUserValue(value, FuelCapacity.INTERNAL_MIN, FuelCapacity.INTERNAL_MAX);
		}
		valueLabel.setText(userValue + "");
		progressTable.setValue(value, maxShield);
	}

	public void updateFromShipProperties() {
		if (progressType == ProgressType.SHIELD) {
			setValue(
					SpaceShipProperties.properties.getCurrentInternalShield(),
					SpaceShipProperties.properties.getShieldCapacity());
		} else {
			setValue(
					SpaceShipProperties.properties.getCurrentInternalFuel(),
					SpaceShipProperties.properties.getFuelCapacity());
		}
	}
}
