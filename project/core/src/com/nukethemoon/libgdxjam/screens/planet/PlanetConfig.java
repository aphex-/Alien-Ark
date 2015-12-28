package com.nukethemoon.libgdxjam.screens.planet;


import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

import java.util.ArrayList;
import java.util.List;

public class PlanetConfig {

	public String id;

	public List<ColorAttribute> environmentColorAttributes = new ArrayList<ColorAttribute>();
	public List<DirectionalLight> environmentDirectionalLights = new ArrayList<DirectionalLight>();
}
