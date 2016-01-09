package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;


import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ColorAttributeForm extends ColorForm {


	public ColorAttributeForm(Skin skin, String title, final ColorAttribute colorAttribute, final ColorAttributeChangeListener listener) {
		super(skin, title, colorAttribute.color, new ColorChangeListener() {
			@Override
			public void onColorChange(float r, float g, float b) {
				colorAttribute.color.r = r;
				colorAttribute.color.g = g;
				colorAttribute.color.b = b;
				listener.onColorAttributeChange(colorAttribute);
			}
		}, null);

		row();
		add(new Label("Attribute", skin));
		final SelectBox<String> attributeSelection = new SelectBox<String>(skin);

		attributeSelection.setItems(
				ColorAttribute.DiffuseAlias,
				ColorAttribute.SpecularAlias,
				ColorAttribute.AmbientAlias,
				ColorAttribute.EmissiveAlias,
				ColorAttribute.ReflectionAlias,
				ColorAttribute.AmbientLightAlias,
				ColorAttribute.FogAlias
		);
		attributeSelection.setSelected(ColorAttribute.DiffuseAlias);
		attributeSelection.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String selectedType = attributeSelection.getSelected();
				long attributeType = ColorAttribute.getAttributeType(selectedType);
				listener.onColorAttributeChange(new ColorAttribute(attributeType, colorAttribute.color));
			}
		});

		add(attributeSelection);

		titleLabel.setText("ColorAttribute");

		pad(10);
		pack();
	}

	public interface ColorAttributeChangeListener {
		void onColorAttributeChange(ColorAttribute attribute);
	}


}
