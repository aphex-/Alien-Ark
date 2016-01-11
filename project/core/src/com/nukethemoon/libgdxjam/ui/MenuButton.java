package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;

public class MenuButton extends ImageButton {

	private MenuTable menuTable;

	public MenuButton(final Skin skin) {
		super(new TextureRegionDrawable(App.TEXTURES.findRegion("buttonMenu")));
		setSkin(skin);

		pack();
		setPosition(
				10,
				Gdx.graphics.getHeight() - this.getHeight() - 10
		);
	}

	public void openMenu(Stage stage) {
		if (menuTable == null) {
			menuTable = new MenuTable(getSkin(), new MenuTable.CloseListener() {
				@Override
				public void onClose() {
					menuTable = null;
				}
			});
			stage.addActor(menuTable);
		}
	}



}
