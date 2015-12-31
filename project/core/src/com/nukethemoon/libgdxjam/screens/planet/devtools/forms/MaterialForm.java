package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Iterator;

public class MaterialForm extends Table {

	public MaterialForm(Skin skin, final Material material, String materialId, final MaterialChangeListener listener) {
		final Iterator<Attribute> iterator = material.iterator();

		add(new Label("Material: " + materialId, skin));
		row();

		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();

			if (attribute instanceof ColorAttribute) {
				final ColorAttribute colorAttribute = (ColorAttribute) attribute;
				ColorAttributeForm colorAttributeForm = new ColorAttributeForm(
						skin, colorAttribute, new ColorAttributeForm.ColorAttributeChangeListener() {
					@Override
					public void onColorAttributeChange(ColorAttribute attribute) {
						replace(material, colorAttribute, attribute);
						listener.onMaterialChange(material);
					}
				});
				add(colorAttributeForm);
				row();
			}
		}
	}

	private void replace(Material m, Attribute oldAttribute, Attribute newAttribute) {
		Iterator<Attribute> iterator = m.iterator();
		while (iterator.hasNext()) {
			Attribute next = iterator.next();
			if (next.equals(oldAttribute)) {
				iterator.remove();
			}
		}
		m.set(newAttribute);
	}

	public interface MaterialChangeListener {
		void onMaterialChange(Material material);
	}
}
