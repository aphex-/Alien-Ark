package com.nukethemoon.libgdxjam.screens.planet.devtools;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DevWindow extends Window {

	public DevWindow(Skin skin) {
		super("Development", skin);
		TextButton closeButton = new TextButton("x", skin);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DevWindow.this.setVisible(false);
			}
		});
		getTitleTable().add(closeButton).height(18);
	}
}
