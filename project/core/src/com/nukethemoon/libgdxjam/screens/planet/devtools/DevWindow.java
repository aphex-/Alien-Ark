package com.nukethemoon.libgdxjam.screens.planet.devtools;


import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;

public class DevWindow extends Window {

	public DevWindow(Skin skin, PlanetConfig planetConfig) {
		super("Development", skin);
		TextButton closeButton = new TextButton("x", skin);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DevWindow.this.setVisible(false);
			}
		});
		getTitleTable().add(closeButton).height(18);

		ColorAttribute colorAttribute = planetConfig.environmentColorAttributes.get(0);
		ColorForm cf = new ColorForm(skin, colorAttribute.color, new ColorForm.ColorChangeListener() {
			@Override
			public void onColorChange(float v, float v1, float v2) {
				pack();
			}
		});
		add(cf);
		row();

		for (DirectionalLight dLight : planetConfig.environmentDirectionalLights) {
			ColorForm colorForm = new ColorForm(skin, dLight.color, new ColorForm.ColorChangeListener() {
				@Override
				public void onColorChange(float r, float g, float b) {
					pack();
				}
			});

			VectorForm vectorForm = new VectorForm(skin, dLight.direction, new VectorForm.VectorChangeListener() {
				@Override
				public void onVectorChange(float r, float g, float b) {
					pack();
				}
			});

			add(vectorForm);
			add(colorForm);
			row();
		}



		pack();
	}
}
