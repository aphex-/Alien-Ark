package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Iterator;

public class MaterialForm extends Table {

	public MaterialForm(Skin skin, final Material material, final MaterialChangeListener listener) {
		final Iterator<Attribute> iterator = material.iterator();

		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();

			if (attribute instanceof ColorAttribute) {
				ColorAttribute colorAttribute = (ColorAttribute) attribute;
				ColorAttributeForm colorAttributeForm = new ColorAttributeForm(
						skin, colorAttribute, new ColorAttributeForm.ColorAttributeChangeListener() {
					@Override
					public void onColorAttributeChange(ColorAttribute attribute) {
						iterator.remove();
						material.set(attribute);
						listener.onMaterialChange(material);
					}
				});
				add(colorAttributeForm);
				row();
			}

		}

	}

	public interface MaterialChangeListener {
		void onMaterialChange(Material material);
	}
}
