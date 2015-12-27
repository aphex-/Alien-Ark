package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;

public class MaterialInterpreter {

	private final Material waterMaterial;
	private final Material grassMaterial;
	private final Material sandMaterial;
	private final Color waterColor;
	private final Color sandColor;
	private final Color grassColor;

	public MaterialInterpreter() {

		waterColor = new Color(0.13f, 0.05f, 0.8f, 1);
		waterMaterial = new Material(
				ColorAttribute.createDiffuse(waterColor),
				ColorAttribute.createSpecular(1, 1, 1, 1),
				FloatAttribute.createShininess(8f));

		sandColor = new Color(0.9f, 0.733f, 0.58f, 1);
		sandMaterial = new Material(ColorAttribute.createDiffuse(sandColor),
				ColorAttribute.createSpecular(1, 1, 1, 1),
				FloatAttribute.createShininess(8f));

		grassColor = new Color(0f, 1f, 0f, 1f);
		grassMaterial = new Material(ColorAttribute.createDiffuse(grassColor),
				ColorAttribute.createSpecular(1, 1, 1, 1),
				FloatAttribute.createShininess(8f));

	}

	public Color getColor(float value) {
		if (value < 0.18) {
			return sandColor;
		}
		return grassColor;

	}

	public Material getMaterial(float value) {

		if (value < 0.1) {
			return waterMaterial;
		}
		if (value < 0.18) {
			return sandMaterial;
		}
		return grassMaterial;
	}
}
