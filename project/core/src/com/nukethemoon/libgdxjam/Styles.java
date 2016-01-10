package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Styles {

	public static NinePatchDrawable NINE_PATCH_POPUP_BG_01;
	public static NinePatchDrawable NINE_PATCH_BUTTON_BG_01;

	public static NinePatchDrawable NINE_PATCH_DIALOG_01;

	public static TextButton.TextButtonStyle STYLE_BUTTON_01;

	public static Color COLOR_01 = new Color(28f / 255f, 25f / 255f, 18f / 255f, 1);

	public static BitmapFont FONT_DOSIS_SMALL;
	public static BitmapFont FONT_DOSIS_MEDIUM_BORDER;
	public static BitmapFont FONT_DOSIS_SMALL_DIALOG_HEADLINE;

	public static BitmapFont FONT_ENTSANS_SMALL_BORDER;

	public static BitmapFont FONT_LIBERATION_SMALL_BORDER;

	public static Label.LabelStyle LABEL_01;
	public static Label.LabelStyle LABEL_02;

	public static Label.LabelStyle LABEL_DEV;

	public static Label.LabelStyle LABEL_HUD_NUMBERS;
	public static Label.LabelStyle LABEL_DIALOG_HEADLINE;

	public static Skin UI_SKIN;

	public static void init(TextureAtlas atlas) {
		UI_SKIN = new Skin();

		FONT_DOSIS_SMALL = 					createFont("fonts/Dosis-Medium.ttf", 	18, Color.WHITE, null, 			0);
		FONT_DOSIS_SMALL_DIALOG_HEADLINE = 	createFont("fonts/Dosis-Medium.ttf", 	18, new Color(52f / 255f, 90f / 255f, 65f / 255f, 1), null, 			0);

		FONT_DOSIS_MEDIUM_BORDER = 	createFont("fonts/Dosis-Medium.ttf", 	26, Color.WHITE, Color.BLACK, 	2);
		FONT_ENTSANS_SMALL_BORDER = createFont("fonts/entsans.ttf", 		16, Color.WHITE, Color.BLACK, 	1);

		FONT_LIBERATION_SMALL_BORDER = 	createFont("fonts/LiberationMono-Bold.ttf",	22, Color.WHITE, Color.BLACK, 2);

		loadDefaultSkin(FONT_DOSIS_SMALL);


		NINE_PATCH_POPUP_BG_01 = 	new NinePatchDrawable(new NinePatch(atlas.findRegion("popupbg01"),16, 16, 16, 16));
		NINE_PATCH_BUTTON_BG_01 = 	new NinePatchDrawable(new NinePatch(atlas.findRegion("buttonbg01"),32, 32, 32, 32));
		NINE_PATCH_DIALOG_01 = 		new NinePatchDrawable(new NinePatch(atlas.findRegion("dialogBg01"), 33, 33, 42, 21));

		TextButton.TextButtonStyle textButtonStyle = UI_SKIN.get(TextButton.TextButtonStyle.class);
		STYLE_BUTTON_01 = new TextButton.TextButtonStyle(textButtonStyle); // copy from default values
		STYLE_BUTTON_01.up = 		Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.down = 		Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.checked = 	Styles.NINE_PATCH_BUTTON_BG_01;
		STYLE_BUTTON_01.fontColor = COLOR_01;

		Label.LabelStyle standardLabelStyle = UI_SKIN.get(Label.LabelStyle.class);
		LABEL_01 = new Label.LabelStyle(standardLabelStyle);
		LABEL_01.font = FONT_DOSIS_SMALL;

		LABEL_02 = new Label.LabelStyle(standardLabelStyle);
		LABEL_02.font = FONT_DOSIS_MEDIUM_BORDER;

		LABEL_DEV = new Label.LabelStyle(standardLabelStyle);
		LABEL_DEV.font = FONT_ENTSANS_SMALL_BORDER;

		LABEL_HUD_NUMBERS = new Label.LabelStyle(standardLabelStyle);
		LABEL_HUD_NUMBERS.font = FONT_ENTSANS_SMALL_BORDER;

		LABEL_DIALOG_HEADLINE = new Label.LabelStyle(standardLabelStyle);
		LABEL_DIALOG_HEADLINE.font = FONT_DOSIS_SMALL_DIALOG_HEADLINE;

	}

	private static BitmapFont createFont(String path, int size, Color color, Color borderColor, float borderWidth) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		parameter.color = color;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		if (borderColor != null) {
			parameter.borderColor = borderColor;
			parameter.borderWidth = borderWidth;
		}
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}

	private static void loadDefaultSkin(BitmapFont defaultFont) {
		UI_SKIN.add("default-font", defaultFont, BitmapFont.class);
		FileHandle fileHandle = Gdx.files.internal(Config.UI_SKIN_PATH);
		FileHandle atlasFile = fileHandle.sibling("uiskin.atlas");

		if (atlasFile.exists()) {
			UI_SKIN.addRegions(new TextureAtlas(atlasFile));
		}
		UI_SKIN.load(fileHandle);
	}

}
