package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Styles {

	public static NinePatchDrawable NINE_PATCH_POPUP_BG_01;
	public static NinePatchDrawable NINE_PATCH_BUTTON_BG_01;

	public static TextButton.TextButtonStyle STYLE_BUTTON_01;

	public static Color COLOR_01 = new Color(28f / 255f, 25f / 255f, 18f / 255f, 1);

	public static Skin UI_SKIN;

	public static void init(TextureAtlas atlas) {
		UI_SKIN = new Skin(Gdx.files.internal(Config.UI_SKIN_PATH));
		NINE_PATCH_POPUP_BG_01 = new NinePatchDrawable(new NinePatch(atlas.findRegion("popupbg01"),16, 16, 16, 16));
		NINE_PATCH_BUTTON_BG_01 = new NinePatchDrawable(new NinePatch(atlas.findRegion("buttonbg01"),32, 32, 32, 32));


		TextButton.TextButtonStyle textButtonStyle = UI_SKIN.get(TextButton.TextButtonStyle.class);
		STYLE_BUTTON_01 = new TextButton.TextButtonStyle(textButtonStyle); // copy from default values
		STYLE_BUTTON_01.up = 		Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.down = 		Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.checked = 	Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.fontColor = COLOR_01;
	}
}
