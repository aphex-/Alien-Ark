package com.nukethemoon.libgdxjam.screens.planet;


import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.nukethemoon.libgdxjam.screens.planet.devtools.GsonMaterial;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {
	public String id;

	public float gravity = -10;

	public List<ColorAttribute> environmentColorAttributes = new ArrayList<ColorAttribute>();
	public List<DirectionalLight> environmentDirectionalLights = new ArrayList<DirectionalLight>();

	public List<LandscapeLayerConfig> layerConfigs = new ArrayList<LandscapeLayerConfig>();

	public void deserialize() {
		for (LandscapeLayerConfig c : layerConfigs) {
			c.material = c.serializedMaterial.createMaterial();
		}
	}

	public class LandscapeLayerConfig {
		public String name;
		public String collisionType;
		public transient Material material;
		public GsonMaterial serializedMaterial;
	}
}
