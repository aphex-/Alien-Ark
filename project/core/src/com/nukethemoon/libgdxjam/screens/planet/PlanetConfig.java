package com.nukethemoon.libgdxjam.screens.planet;


import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.nukethemoon.libgdxjam.screens.planet.devtools.GsonMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetConfig {
	public String id;

	public float gravity = -10;

	public List<ColorAttribute> environmentColorAttributes = new ArrayList<ColorAttribute>();
	public List<DirectionalLight> environmentDirectionalLights = new ArrayList<DirectionalLight>();

	// just for serialization
	public  Map<String, GsonMaterial> serializedMaterials = new HashMap<String, GsonMaterial>();

	public transient Map<String, Material> materials = new HashMap<String, Material>();

	public void deserialize() {
		for (Map.Entry<String, GsonMaterial> entry : serializedMaterials.entrySet()) {
			materials.put(entry.getKey(), entry.getValue().createMaterial());
		}
	}
}
