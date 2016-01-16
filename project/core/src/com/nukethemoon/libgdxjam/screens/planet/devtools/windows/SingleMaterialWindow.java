package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.MaterialForm;

public class SingleMaterialWindow extends ClosableWindow {


	public SingleMaterialWindow(Stage stage, Skin skin, Material material, String id) {
		super(stage, "material " + id, skin);

		MaterialForm form = new MaterialForm(skin, material, id, new MaterialForm.MaterialChangeListener() {
			@Override
			public void onMaterialChange(Material material) {
				pack();
			}
		});

		add(form);
		pack();
	}

	@Override
	public void onClose() {
		this.remove();
	}
}
