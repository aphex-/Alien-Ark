package com.nukethemoon.libgdxjam.screens.planet.devtools;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class VectorForm extends Table {

	public VectorForm(Skin skin, final Vector3 vector, final VectorChangeListener listener) {
		pad(2);

		final Slider sliderX = new Slider(-1f, 1f, 0.01f, false, skin);
		sliderX.setValue(vector.x);
		final Label labelX = new Label("", skin);
		labelX.setWidth(30);
		final Slider sliderY = new Slider(-1f, 1f, 0.01f, false, skin);
		sliderY.setValue(vector.y);
		final Label labelY = new Label("", skin);
		labelY.setWidth(30);
		final Slider sliderZ = new Slider(-1f, 1f, 0.01f, false, skin);
		sliderZ.setValue(vector.z);
		final Label labelZ = new Label("", skin);
		labelZ.setWidth(30);

		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				float x = (float) Math.floor(sliderX.getValue() * 100) / 100f;
				float y = (float) Math.floor(sliderY.getValue() * 100) / 100f;
				float z = (float) Math.floor(sliderZ.getValue() * 100) / 100f;

				vector.set(x, y, z);

				labelX.setText(x + "");
				labelY.setText(y + "");
				labelZ.setText(z + "");
				listener.onVectorChange(x, y, z);
				pack();
			}
		};

		sliderX.addListener(changeListener);
		sliderY.addListener(changeListener);
		sliderZ.addListener(changeListener);

		add(new Label("Vector", skin));
		row();

		add(new Label("x", skin));
		add(sliderX);
		add(labelX);
		row();

		add(new Label("y", skin));
		add(sliderY);
		add(labelY);
		row();

		add(new Label("z", skin));
		add(sliderZ);
		add(labelZ);
		row();

		pack();
	}

	public interface VectorChangeListener {
		void onVectorChange(float r, float g, float b);
	}
}
