package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class EnterOrbitTable extends DialogTable {

	private final Button enterButton;

	public EnterOrbitTable(Skin skin) {
		super(skin, new String[] {"Do you want to enter", "the planets orbit?"}, "NAVIGATION");
		enterButton = new TextButton("ENTER", skin);
		content.add(enterButton).colspan(2);
		updatePosition(350, 50);
	}

	public void setClickListener(ClickListener clickListener) {
		enterButton.addListener(clickListener);
	}
}
