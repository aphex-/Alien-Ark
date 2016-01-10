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

public class PopupTable extends Table {

	private Table content;

	public PopupTable(Skin skin) {
		setBackground(Styles.NINE_PATCH_DIALOG_01);
		pad(0);
		content = new Table();
		content.pad(15);
		content.padTop(5);

		Label titleTable = new Label("INFORMATION", Styles.LABEL_DIALOG_HEADLINE);
		add(titleTable).left().top().padLeft(15).fill().padTop(12);

		ImageButton imageButton = new ImageButton(new TextureRegionDrawable(App.TEXTURES.findRegion("exitImage")));
		imageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PopupTable.this.remove();
			}
		});
		add(imageButton).top().right();
		row();

		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		content.add(new Label("sdfjsi sdf dfis asdassdfp", skin));
		content.row();
		add(content).colspan(2);

		pack();
		setPosition(20, Gdx.graphics.getHeight() - this.getHeight() - 50);
	}
}
