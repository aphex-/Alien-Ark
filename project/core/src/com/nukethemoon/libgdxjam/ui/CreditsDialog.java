package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;

public class CreditsDialog extends DialogTable {

	public CreditsDialog() {
		super(Styles.UI_SKIN, new String[] {}, "CREDITS");
		Image creditsImage = new Image(new TextureRegionDrawable(App.TEXTURES.findRegion("credits")));
		content.add(creditsImage).width(creditsImage.getWidth()).height(creditsImage.getHeight());
		updatePosition(20, 50);
	}
}
