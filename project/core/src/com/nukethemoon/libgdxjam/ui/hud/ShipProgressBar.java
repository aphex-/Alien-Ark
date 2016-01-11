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

public class ShipProgressBar extends Table {

	private final Label valueLabel;

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
		if (progressType == ProgressType.SHIELD) {
			bgSprite.flip(true, false);
			right().bottom();
		} else {
			left().bottom();
		}
		setBackground(new SpriteDrawable(bgSprite));
		setWidth(bgTextureRegion.getRegionWidth());
		setHeight(bgTextureRegion.getRegionHeight());

		Label label = new Label(progressType.name(), Styles.LABEL_PROGRESS_TYPE);
		Table progressTable = new Table();

		valueLabel = new Label("0", Styles.LABEL_HUD_NUMBERS);

		if (progressType == ProgressType.SHIELD) {
			add(valueLabel).width(100);
			add(progressTable).right().width(205).padLeft(5).padRight(5);
			add(label).right().width(76).fill().padLeft(7);
		} else {
			add(label).left().width(73).fill().padLeft(10);
			add(progressTable).width(205).padLeft(5).padRight(5);
			add(valueLabel).width(100).padLeft(5);
		}
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
		valueLabel.setText(value + "");
	}
}
