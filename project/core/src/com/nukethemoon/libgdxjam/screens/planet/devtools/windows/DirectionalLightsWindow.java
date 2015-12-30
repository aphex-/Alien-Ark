package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.gson.Gson;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.DirectionalLightsForm;

public class DirectionalLightsWindow extends ClosableWindow {

	private final Skin skin;
	private final TextButton saveButton;
	private final DirectionalLightsForm directionalLightsForm;
	private PlanetConfig planetConfig;


	public DirectionalLightsWindow(Skin skin, final ReloadSceneListener reloadSceneListener) {
		super("Directional Lights", skin);
		this.skin = skin;


		directionalLightsForm = new DirectionalLightsForm(skin, reloadSceneListener);
		add(directionalLightsForm);
		row();

		saveButton = new TextButton("save", skin);
		add(saveButton);
	}

	public void load(PlanetConfig pPlanetConfig) {
		this.planetConfig = pPlanetConfig;
		directionalLightsForm.load(pPlanetConfig);
		saveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				savePlanetConfig(planetConfig);
			}
		});
		pack();
	}

	private void savePlanetConfig(PlanetConfig planetConfig) {
		Gson gson = new Gson();
		String str = gson.toJson(planetConfig);

		FileHandle fileHandle = new FileHandle(
				"entities/planets/"+ planetConfig.id +"/sceneConfig.json");
		fileHandle.writeString(str, false);
	}

}
