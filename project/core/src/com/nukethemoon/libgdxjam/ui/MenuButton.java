package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;

public class MenuButton extends ImageButton {

	private MenuTable menuTable;

	public MenuButton(final Skin skin, final Stage stage) {
		super(new TextureRegionDrawable(App.TEXTURES.findRegion("buttonMenu")));

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (menuTable == null) {
					menuTable = new MenuTable(skin, new MenuTable.CloseListener() {
						@Override
						public void onClose() {
							menuTable = null;
						}
					});
					stage.addActor(menuTable);
				}
			}
		});

		pack();
		setPosition(
				10,
				Gdx.graphics.getHeight() - this.getHeight() - 10
		);
	}



}
