package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;

public class MaterialsWindow extends ClosableWindow {

	private Table content = new Table();
	private Stage stage;

	public MaterialsWindow(Stage stage, Skin skin, ReloadSceneListener reloadSceneListener) {
		super("Materials", skin);
		this.stage = stage;
		add(content);

		pack();
	}

	public void load(PlanetConfig config) {
		content.clear();
		int i = 0;
		for (final PlanetConfig.LandscapeLayerConfig layerConfig : config.layerConfigs) {
			TextButton button = new TextButton(i + " " + layerConfig.name, getSkin());
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SingleMaterialWindow w = new SingleMaterialWindow(getSkin(), layerConfig.material, layerConfig.name);
					stage.addActor(w);
				}
			});
			content.add(button);
			content.row();
			i++;
		}
		content.pack();
		pack();
	}
}
