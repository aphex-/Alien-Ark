package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.MaterialForm;

public class SingleMaterialWindow extends ClosableWindow {


	public SingleMaterialWindow(Skin skin, Material material, String id) {
		super("material " + id, skin);

		MaterialForm form = new MaterialForm(skin, material, id, new MaterialForm.MaterialChangeListener() {
			@Override
			public void onMaterialChange(Material material) {

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
