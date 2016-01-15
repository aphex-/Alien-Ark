package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;

public class GameOverTable extends DialogTable {

	private ClickListener clickListener;

	public GameOverTable(Skin skin, final ClickListener clickListener) {
		super(Styles.UI_SKIN, new String[]{"Your journey ends here.", "Try it again."}, "GAME OVER");
		this.clickListener = clickListener;
		setBackground(Styles.NINE_PATCH_DIALOG_01);

		TextButton okButton = new TextButton("OK", Styles.STYLE_BUTTON_02);
		okButton.addListener(clickListener);
		content.add(okButton);
		pack();
	}

	@Override
	protected void onClose() {
		clickListener.clicked(null, 0, 0);
	}

	@Override
	public void updatePosition(int left, int top) {
		pack();
		setPosition((Gdx.graphics.getWidth() / 2) - (this.getWidth() / 2),
				(Gdx.graphics.getHeight() / 2) - (this.getHeight() / 2));
	}

}
