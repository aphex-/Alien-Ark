package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;

public class MaterialsWindow extends ClosableWindow {

	private Table content = new Table();

	public MaterialsWindow(Skin skin, ReloadSceneListener reloadSceneListener) {
		super("Materials", skin);

		add(content);

		pack();
	}

	public void load(PlanetConfig config) {
		/*content.clear();
		for (Map.Entry<String, Material> entry : config.materials.entrySet()) {
			Material material = entry.getValue();
			String materialId = entry.getKey();

			MaterialForm materialForm = new MaterialForm(getSkin(),
					material, materialId, new MaterialForm.MaterialChangeListener() {
				@Override
				public void onMaterialChange(Material material) {

				}
			});

			content.add(materialForm);
			content.row();
		}
		content.pack();
		pack();*/
	}
}
