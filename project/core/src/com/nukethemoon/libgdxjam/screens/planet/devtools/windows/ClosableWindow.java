package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ClosableWindow extends Window {
	public ClosableWindow(final Stage stage, String title, Skin skin) {
		super(title, skin);
		TextButton closeButton = new TextButton("x", skin);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ClosableWindow.this.setVisible(false);
				stage.setKeyboardFocus(null);
				onClose();
			}
		});
		getTitleTable().add(closeButton).height(18);
	}

	public void onClose() {

	}
}
