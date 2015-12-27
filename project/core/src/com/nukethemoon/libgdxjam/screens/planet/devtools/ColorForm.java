package com.nukethemoon.libgdxjam.screens.planet.devtools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ColorForm extends Table {



	public ColorForm(Skin skin, final Color color, final ColorChangeListener listener) {
		pad(2);

		final Slider sliderR = new Slider(0f, 1f, 0.01f, false, skin);
		sliderR.setValue(color.r);
		final Label labelR = new Label("", skin);
		labelR.setWidth(30);
		final Slider sliderG = new Slider(0f, 1f, 0.01f, false, skin);
		sliderG.setValue(color.g);
		final Label labelG = new Label("", skin);
		labelG.setWidth(30);
		final Slider sliderB = new Slider(0f, 1f, 0.01f, false, skin);
		sliderB.setValue(color.b);
		final Label labelB = new Label("", skin);
		labelB.setWidth(30);

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
				pack();
			}
		};

		sliderR.addListener(changeListener);
		sliderG.addListener(changeListener);
		sliderB.addListener(changeListener);

		add(new Label("Color", skin));
		row();

		add(new Label("r", skin));
		add(sliderR);
		add(labelR);
		row();

		add(new Label("g", skin));
		add(sliderG);
		add(labelG);
		row();

		add(new Label("b", skin));
		add(sliderB);
		add(labelB);
		row();

		pack();
	}

	public interface ColorChangeListener {
		void onColorChange(float r, float g, float b);
	}
}
