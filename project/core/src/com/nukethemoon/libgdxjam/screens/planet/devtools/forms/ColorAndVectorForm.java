package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ColorAndVectorForm extends Table {

	private final TextButton deleteButton;

	public ColorAndVectorForm(Skin skin, String title, final Color color, final Vector3 vector3, final ColorAndVectorChangeListener listener) {

		ColorForm colorForm = new ColorForm(skin, color, new ColorForm.ColorChangeListener() {
			@Override
			public void onColorChange(float r, float g, float b) {
				color.set(r, g, b, 1);
				listener.onChange(color, vector3);
			}
		});

		VectorForm vectorForm = new VectorForm(skin, vector3, new VectorForm.VectorChangeListener() {
			@Override
			public void onVectorChange(float x, float y, float z) {
				vector3.set(x, y, z);
				listener.onChange(color, vector3);
			}
		});

		add(new Label(title, skin)).left().fill().expand();
		deleteButton = new TextButton("remove", skin);
		add(deleteButton).right();
		row();
		add(colorForm);
		add(vectorForm);
		pack();
	}

	public void addDeleteButtonListener(ClickListener listener) {
		deleteButton.addListener(listener);
	}


	public interface ColorAndVectorChangeListener {
		void onChange(Color color, Vector3 vector);
	}
}
