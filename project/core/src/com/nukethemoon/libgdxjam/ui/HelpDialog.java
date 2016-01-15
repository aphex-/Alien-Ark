package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;

public class HelpDialog extends DialogTable {

	public HelpDialog() {
		super(Styles.UI_SKIN, new String[] {}, "HELP");
		Image creditsImage = new Image(new TextureRegionDrawable(App.TEXTURES.findRegion("help")));
		content.add(creditsImage).width(creditsImage.getWidth()).height(creditsImage.getHeight());
		updatePosition(20, 50);
	}
}
