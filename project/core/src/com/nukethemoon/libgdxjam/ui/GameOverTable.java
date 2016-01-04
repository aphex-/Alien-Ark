package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;

public class GameOverTable extends CenteredTable {


	public GameOverTable(Skin skin) {
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);


		add(new Label("GAME OVER", skin));
		row();

		TextButton okButton = new TextButton("OK", Styles.STYLE_BUTTON_01);



		okButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				App.onGameOver();
			}
		});
		add(okButton);
		pad(20);
		pack();
	}
}
