package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.RemoveTableListener;

import java.util.Iterator;

public class MaterialForm extends Table {


	private final MaterialChangeListener listener;

	public MaterialForm(Skin skin, final Material material, String materialId,
						final MaterialChangeListener listener) {
		this.listener = listener;

		final Iterator<Attribute> iterator = material.iterator();
		defaults().left().top().pad(4);
		add(new Label("Material: " + materialId, skin)).left().top().colspan(2);
		row();
		setSkin(skin);

		final SelectBox<String> attributeSelectBox = new SelectBox<String>(skin);
		attributeSelectBox.setItems(
				"diffuseColor",
				"specularColor",
				"ambientColor",
				"emissiveColor",
				"reflectionColor",
				"ambientLightColor",
				"fogColor"
		);

		add(attributeSelectBox);
		TextButton addButton = new TextButton("add", skin);
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String selected = attributeSelectBox.getSelected();
				long attributeType = ColorAttribute.getAttributeType(selected);
				ColorAttribute colorAttribute = new ColorAttribute(attributeType);
				material.set(colorAttribute);
				addAttributeForm(colorAttribute, material);
				listener.onMaterialChange(material);
			}
		});

		add(addButton);
		row();

		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			addAttributeForm(attribute, material);
		}
	}

	private void addAttributeForm(Attribute attribute, final Material material) {
		if (attribute instanceof ColorAttribute) {
			final ColorAttribute colorAttribute = (ColorAttribute) attribute;
			String type = ColorAttribute.getAttributeAlias(colorAttribute.type);


			ColorForm.ColorChangeListener colorChangeListener = new ColorForm.ColorChangeListener() {
				@Override
				public void onColorChange(float r, float g, float b) {
					//replace(material, colorAttribute, attribute);
					listener.onMaterialChange(material);
				}
			};

			final ColorForm colorAttributeForm = new ColorForm(
					getSkin(), "ColorAttribute " + type, colorAttribute.color, colorChangeListener, new RemoveTableListener() {
				@Override
				public void onRemove(Table table) {
					material.remove(colorAttribute.type);
					listener.onMaterialChange(material);
					removeActor(table);
					pack();
					listener.onMaterialChange(material);
				}
			});
			add(colorAttributeForm).colspan(2).left().fill();
			row();
		}
		pack();
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
