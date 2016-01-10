package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.nukethemoon.libgdxjam.ArtifactDefitnitions;
import com.nukethemoon.libgdxjam.screens.planet.PointWithId;

public class ArtifactObject {

	private final ModelInstance modelInstance;

	private PointWithId definition;

	public ArtifactObject(PointWithId artifactDefinition) {
		this.definition = artifactDefinition;
		modelInstance = new ModelInstance(ArtifactDefitnitions.getModelOfArtifact(artifactDefinition.id));
		adjust(0);
	}

	public void adjust(float height) {
		modelInstance.transform.setToTranslation(
				PlanetPart.getTileGraphicX(definition.x),
				PlanetPart.getTileGraphicY(definition.y),
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
