package com.nukethemoon.libgdxjam.screens.planet;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.libgdxjam.screens.planet.devtools.GsonMaterial;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.PlanetRace;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {
	public String id;

	public float gravity = -10;

	public float landscapeHeight = 25;

	public float fuelChance = 0.2f; // chance to find fuel per chunk
	public float fuelChanceGain = -0.001f; // modifies the chance by the chunks distance to position 0,0
	public float fuelChanceMin = 0.0001f;

	public float shieldChance = 0.05f;
	public float shieldChanceGain = -0.001f;
	public float shieldChanceMin = 0.0001f;

	public List<ColorAttribute> environmentColorAttributes = new ArrayList<ColorAttribute>();
	public List<DirectionalLight> environmentDirectionalLights = new ArrayList<DirectionalLight>();

	public List<ObjectPlacementInfo> artifacts = new ArrayList<ObjectPlacementInfo>();
	public List<LandscapeLayerConfig> layerConfigs = new ArrayList<LandscapeLayerConfig>();

	public PlanetRace planetRace;

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

	public static void savePlanetConfig(PlanetConfig planetConfig) {
		for (LandscapeLayerConfig l : planetConfig.layerConfigs) {
			l.serializedMaterial = new GsonMaterial(l.material);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(planetConfig);
		FileHandle fileHandle = new FileHandle(
				"entities/planets/"+ planetConfig.id +"/sceneConfig.json");
		fileHandle.writeString(str, false);
	}

	public String consumeArtifactId() {
		int i = 1;
		while (contains(createArtifactId(i))) {
			i++;
		}
		return createArtifactId(i);
	}

	private String createArtifactId(int number) {
		return "art" + createNumberString(number) + "_" + id;
	}

	public boolean contains(String artifactId) {
		for (ObjectPlacementInfo info : artifacts) {
			if (info.id.equals(artifactId)) {
				return true;
			}
		}
		return false;
	}

	public static String createNumberString(int number) {
		if (number < 10) {
			return "000" + number;
		}
		if (number < 100) {
			return "00" + number;
		}
		if (number < 1000) {
			return "0" + number;
		}
		return "" + number;
	}

}
