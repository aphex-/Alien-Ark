package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.ui.animation.TableHighlightAnimation;

public class DialogTable extends Table {

	protected Table content;

	public DialogTable(Skin skin, String[] textLines, String title) {
		setBackground(Styles.NINE_PATCH_DIALOG_01);
		pad(0);
		content = new Table();
		content.pad(15);
		content.padTop(5);

		Label titleTable = new Label(title, Styles.LABEL_DIALOG_HEADLINE);
		add(titleTable).left().top().padLeft(15).fill().padTop(12);

		ImageButton imageButton = new ImageButton(
				new TextureRegionDrawable(App.TEXTURES.findRegion("exitImage")));
		imageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DialogTable.this.remove();
			}
		});
		add(imageButton).top().right();
		row();

		for (String line : textLines) {
			content.add(new Label(line, skin)).left();
			content.row();
		}

		add(content).colspan(2);

		updatePosition(20, 50);
	}

	public void updatePosition(int left, int top) {
		pack();
		setPosition(left, Gdx.graphics.getHeight() - this.getHeight() - top);
	}

	public TableHighlightAnimation createAnimation() {
		return new TableHighlightAnimation(this);
	}
}
