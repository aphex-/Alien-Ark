package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;

public class MenuTable extends Table {

	private Table content;

	public MenuTable(Skin skin) {
		setBackground(new TextureRegionDrawable(App.TEXTURES.findRegion("popupMenu")));
		pad(0);
		content = new Table();
		content.pad(15);
		content.padTop(5);


		ImageButton imageButton = new ImageButton(
				new TextureRegionDrawable(App.TEXTURES.findRegion("exitImage2")));
		imageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuTable.this.remove();
			}
		});
		add(imageButton).top().right();
		row();
		add(content).fill().expand();

		pack();
		setPosition((Gdx.graphics.getWidth() / 2) - this.getWidth() / 2,
				(Gdx.graphics.getHeight() / 2) - this.getHeight() / 2);

	}
}
