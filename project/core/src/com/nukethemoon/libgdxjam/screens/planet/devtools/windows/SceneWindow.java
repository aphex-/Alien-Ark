package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.ColorAndVectorForm;

public class SceneWindow extends ClosableWindow {

	private final Table content;
	private final Skin skin;
	private final ReloadSceneListener reloadSceneListener;
	private final TextButton addButton;


	public SceneWindow(Skin skin, final ReloadSceneListener reloadSceneListener) {
		super("Scene", skin);
		this.skin = skin;
		this.reloadSceneListener = reloadSceneListener;

		addButton = new TextButton("add light", skin);
		add(addButton);
		row();

		content = new Table();
		add(content);
	}

	public void load(final PlanetConfig planetConfig) {
		getTitleLabel().setText("Scene " + planetConfig.id);


		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DirectionalLight directionalLight = new DirectionalLight();
				planetConfig.environmentDirectionalLights.add(directionalLight);
				addLight(directionalLight, planetConfig.environmentDirectionalLights.size() + 1, planetConfig);
				reloadSceneListener.onReloadScene(planetConfig);
				pack();
			}
		});


		// directional lights
		int index = 1;
		for (final DirectionalLight dLight : planetConfig.environmentDirectionalLights) {
			addLight(dLight, index, planetConfig);
			index++;
		}

		pack();
	}

	private void addLight(final DirectionalLight dLight, int index, final PlanetConfig planetConfig) {
		final ColorAndVectorForm colorAndVectorForm = new ColorAndVectorForm(getSkin(),
				"Environment Directional Light " + index, dLight.color, dLight.direction,
				new ColorAndVectorForm.ColorAndVectorChangeListener() {
					@Override
					public void onChange(Color color, Vector3 vector) {

					}
				});

		colorAndVectorForm.addDeleteButtonListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				content.removeActor(colorAndVectorForm);
				pack();
				planetConfig.environmentDirectionalLights.remove(dLight);
				reloadSceneListener.onReloadScene(planetConfig);
			}
		});

		content.add(colorAndVectorForm).padTop(10);
		content.row();
	}


}
