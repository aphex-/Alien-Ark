package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.DevelopmentWindow;

public class DirectionalLightsForm extends Table {

	private final TextButton addButton;
	private final Table content;
	private PlanetConfig planetConfig;
	private ReloadSceneListener listener;

	public DirectionalLightsForm(Skin skin, ReloadSceneListener reloadSceneListener) {
		setSkin(skin);
		addButton = new TextButton("add new light", skin);
		listener = reloadSceneListener;
		add(addButton);
		row();

		content = new Table();
		content.setBackground(DevelopmentWindow.INNER_BACKGROUND);
		content.pad(5);
		ScrollPane pane = new ScrollPane(content);
		pane.setScrollingDisabled(true, false);
		pane.setScrollBarPositions(false, true);
		pane.setScrollbarsOnTop(true);

		add(pane).height(300);
		row();

	}

	public void load(final PlanetConfig pPlanetConfig) {
		planetConfig = pPlanetConfig;
		content.clear();
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				DirectionalLight directionalLight = new DirectionalLight();
				pPlanetConfig.environmentDirectionalLights.add(directionalLight);
				addLight(directionalLight, pPlanetConfig.environmentDirectionalLights.size() + 1, pPlanetConfig);
				listener.onReloadScene(pPlanetConfig);
				pack();
			}
		});


		// directional lights
		int index = 1;
		for (final DirectionalLight dLight : pPlanetConfig.environmentDirectionalLights) {
			addLight(dLight, index, pPlanetConfig);
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
				listener.onReloadScene(planetConfig);
			}
		});

		content.add(colorAndVectorForm).padTop(10);
		content.row();
	}
}
