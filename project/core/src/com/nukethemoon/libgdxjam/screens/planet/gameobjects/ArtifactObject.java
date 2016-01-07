package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.nukethemoon.libgdxjam.Models;

public class ArtifactObject {

	private final ModelInstance modelInstance;

	public ArtifactObject() {
		modelInstance = new ModelInstance(Models.ARTIFACT);
		modelInstance.transform.setToTranslation(0, 0, 40);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

}
