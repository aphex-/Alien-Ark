package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;

public class GameOverTable extends CenteredTable {


	public GameOverTable(Skin skin, final ClickListener clickListener) {
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);

		add(new Label("GAME OVER", Styles.LABEL_01));
		row();

		TextButton okButton = new TextButton("OK", Styles.STYLE_BUTTON_01);

		okButton.addListener(clickListener);
		add(okButton);
		pad(20);
		pack();
	}


}
