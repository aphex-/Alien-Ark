package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.nukethemoon.libgdxjam.ArtifactDefinitions;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.ObjectPlacementInfo;

public class ArtifactObject {

	private final ModelInstance modelInstance;
	private ObjectPlacementInfo definition;

	public ArtifactObject(ObjectPlacementInfo artifactDefinition) {
		this.definition = artifactDefinition;
		ArtifactDefinitions.ConcreteArtifactType concreteArtifactType =
				ArtifactDefinitions.ConcreteArtifactType.byName(artifactDefinition.type);
		modelInstance = new ModelInstance(Models.ARTIFACT_MODELS.get(concreteArtifactType));
		adjust(0);
	}

	public void adjust(float height) {
		modelInstance.transform.setToRotation(0, 0, 1, definition.rotationZ);
		modelInstance.transform.rotate(0, 1, 0, definition.rotationY);
		modelInstance.transform.rotate(1, 0, 0, definition.rotationX);
		modelInstance.transform.trn(
				definition.x,
				definition.y,
				height + definition.groundOffsetZ);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void dispose() {

	}


	public ObjectPlacementInfo getDefinition() {
		return definition;
	}
}
