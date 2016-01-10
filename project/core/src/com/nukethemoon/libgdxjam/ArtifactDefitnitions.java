package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.graphics.g3d.Model;

import java.util.HashMap;
import java.util.Map;

public class ArtifactDefitnitions {

	private static Map<String, ArtifactDefinition> DEFINITIONS = new HashMap<String, ArtifactDefinition>();

	static {
		DEFINITIONS.put("art0001_planet01", new ArtifactDefinition(ArtifactModelType.E));
		DEFINITIONS.put("art0002_planet01", new ArtifactDefinition(ArtifactModelType.F));
		DEFINITIONS.put("art0003_planet01", new ArtifactDefinition(ArtifactModelType.W));
		DEFINITIONS.put("art0004_planet01", new ArtifactDefinition(ArtifactModelType.E));
		DEFINITIONS.put("art0005_planet01", new ArtifactDefinition(ArtifactModelType.W));


		DEFINITIONS.put("art0001_planet02", new ArtifactDefinition(ArtifactModelType.W));
	}


	public enum ArtifactModelType {
		W,
		F,
		E
	}


	public static Model getModelOfArtifact(String artifactId) {
		ArtifactDefinition artifactDefinition = DEFINITIONS.get(artifactId);
		if (artifactDefinition != null) {
			ArtifactModelType modelType = artifactDefinition.modelType;
			if (modelType != null) {
				if (modelType == ArtifactModelType.E) {
					return Models.ARTIFACT_E;
				}
				if (modelType == ArtifactModelType.W) {
					return Models.ARTIFACT_W;
				}
				if (modelType == ArtifactModelType.F) {
					return Models.ARTIFACT_F;
				}
			}
		}
		return null;
	}


	public static class ArtifactDefinition {
		ArtifactModelType modelType;
		public ArtifactDefinition(ArtifactModelType type) {
			modelType = type;
		}
	}

}
