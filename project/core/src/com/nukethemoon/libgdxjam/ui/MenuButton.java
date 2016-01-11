package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;

public class MenuButton extends ImageButton {

	public MenuButton(final Skin skin) {
		super(new TextureRegionDrawable(App.TEXTURES.findRegion("buttonMenu")));
		setSkin(skin);
		pack();
		setPosition(10, Gdx.graphics.getHeight() - this.getHeight() - 10
		);
	}

	public void openMenu(Stage stage, MenuTable.CloseListener closeListener) {
		stage.addActor(new MenuTable(getSkin(), closeListener));
	}
}
