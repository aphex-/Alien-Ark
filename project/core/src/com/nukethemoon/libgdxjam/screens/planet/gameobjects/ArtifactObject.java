package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.PointWithId;

public class ArtifactObject {

	private final ModelInstance modelInstance;

	private PointWithId definition;

	public ArtifactObject(PointWithId artifactDefinition) {
		this.definition = artifactDefinition;
		modelInstance = new ModelInstance(Models.ARTIFACT_E);
		adjust(0);
	}

	public void adjust(float height) {
		modelInstance.transform.setToTranslation(
				definition.x * ControllerPlanet.TILE_GRAPHIC_SIZE,
				definition.y * ControllerPlanet.TILE_GRAPHIC_SIZE,
				height);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void dispose() {

	}


	public PointWithId getDefinition() {
		return definition;
	}
}
