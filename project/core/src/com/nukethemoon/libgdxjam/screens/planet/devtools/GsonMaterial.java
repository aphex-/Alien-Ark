package com.nukethemoon.libgdxjam.screens.planet.devtools;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GsonMaterial {

	private String id;

	private List<ColorAttribute> colorAttributes = new ArrayList<ColorAttribute>();

	public GsonMaterial(Material material) {
		Iterator<Attribute> iterator = material.iterator();

		while (iterator.hasNext()) {
			Attribute next = iterator.next();
			if (next instanceof ColorAttribute) {
				colorAttributes.add((ColorAttribute) next);
			}
		}

		this.id = material.id;
	}

	public Material createMaterial() {
		Material m = new Material(this.id);
		for (ColorAttribute colorAttribute : colorAttributes) {
			m.set(colorAttribute);
		}
		return m;
	}
}
