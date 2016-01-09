package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.RemoveTableListener;

public class ColorForm extends Table {


	protected final Label titleLabel;

	public ColorForm(Skin skin, String title, final Color color, final ColorChangeListener listener, final RemoveTableListener removeTableListener) {
		pad(2);
		defaults().pad(2).left();
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);

		final Slider sliderR = new Slider(0f, 1f, 0.01f, false, skin);
		sliderR.setValue(color.r);
		final Label labelR = new Label(color.r + "", Styles.LABEL_DEV);
		labelR.setWidth(50);
		final Slider sliderG = new Slider(0f, 1f, 0.01f, false, skin);
		sliderG.setValue(color.g);
		final Label labelG = new Label(color.g + "", Styles.LABEL_DEV);
		labelG.setWidth(50);
		final Slider sliderB = new Slider(0f, 1f, 0.01f, false, skin);
		sliderB.setValue(color.b);
		final Label labelB = new Label(color.b + "", Styles.LABEL_DEV);
		labelB.setWidth(50);

		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float r = (float) Math.floor(sliderR.getValue() * 100) / 100f;
				float g = (float) Math.floor(sliderG.getValue() * 100) / 100f;
				float b = (float) Math.floor(sliderB.getValue() * 100) / 100f;

				color.set(r, g, b, 1);

				labelR.setText(r + "");
				labelG.setText(g + "");
				labelB.setText(b + "");
				listener.onColorChange(r, g, b);
			}
		};

		sliderR.addListener(changeListener);
		sliderG.addListener(changeListener);
		sliderB.addListener(changeListener);

		int colspan = removeTableListener == null ? 3 : 2;

		titleLabel = new Label(title, Styles.LABEL_DEV);
		add(titleLabel).colspan(colspan).fill().left();

		if (removeTableListener != null) {
			TextButton removeButton = new TextButton("X", skin);
			removeButton.addListener(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y) {
					removeTableListener.onRemove(ColorForm.this);
				}
			});
			add(removeButton).right();
		}

		row();

		add(new Label("r", Styles.LABEL_DEV));
		add(sliderR).expand().fill();
		add(labelR).width(50);
		row();

		add(new Label("g", Styles.LABEL_DEV));
		add(sliderG).expand().fill();
		add(labelG).width(50);
		row();

		add(new Label("b", Styles.LABEL_DEV));
		add(sliderB).expand().fill();
		add(labelB).width(50);
		row();

		pack();
	}

	public interface ColorChangeListener {
		void onColorChange(float r, float g, float b);
	}
}
