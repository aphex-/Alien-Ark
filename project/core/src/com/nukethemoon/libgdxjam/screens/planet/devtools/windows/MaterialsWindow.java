package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.GsonMaterial;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.MaterialForm;

import java.util.Map;

public class MaterialsWindow extends ClosableWindow {

	public MaterialsWindow(Skin skin, ReloadSceneListener reloadSceneListener) {
		super("Materials", skin);



		pack();
	}

	public void load(PlanetConfig config) {
		for (Map.Entry<String, GsonMaterial> entry : config.serializedMaterials.entrySet()) {
			Material material = entry.getValue().createMaterial();
			String materialId = entry.getKey();

			MaterialForm materialForm = new MaterialForm(getSkin(), material, materialId, new MaterialForm.MaterialChangeListener() {
				@Override
				public void onMaterialChange(Material material) {

				}
			});

			add(materialForm);
			row();
		}
		pack();
	}
}
