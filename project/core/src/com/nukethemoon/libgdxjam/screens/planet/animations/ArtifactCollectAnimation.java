package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class ArtifactCollectAnimation extends BaseAnimation {

	private final Vector3 baseScale = new Vector3();
	private final Vector3 baseTranslation = new Vector3();
	private ModelInstance modelInstance;
	private Vector3 target;

	public ArtifactCollectAnimation(ArtifactObject artifactObject, Vector3 target, AnimationFinishedListener listener) {
		super(1000, listener);
		this.target = target;
		modelInstance = artifactObject.getModelInstance();
		modelInstance.transform.getScale(baseScale);
		modelInstance.transform.getTranslation(baseTranslation);
	}

	@Override
	protected void onProgress(float v) {
		float scale = (1 - v);

		float x = (target.x - baseTranslation.x) * v + baseTranslation.x;
		float y = (target.y - baseTranslation.y) * v + baseTranslation.y;
		float z = (target.z - baseTranslation.z) * v + baseTranslation.z;

		modelInstance.transform.setToTranslationAndScaling(x, y, z,
				baseScale.x * scale, baseScale.y * scale, baseScale.z * scale);
	}
}
