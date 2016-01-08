package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.tools.ani.BaseAnimation;

public class CollectibleStandardAnimation extends BaseAnimation {

	private ModelInstance modelInstance;
	private Vector3 baseScale = new Vector3();
	private Vector3 baseTranslation = new Vector3();
	private float rotation = 0;


	public CollectibleStandardAnimation(ModelInstance modelInstance) {
		super(1000);
		this.modelInstance = modelInstance;
		modelInstance.transform.getScale(baseScale);
		modelInstance.transform.getTranslation(baseTranslation);
		loopInfinite();
	}

	@Override
	protected void onProgress(float v) {
		float scale = (float) Math.sin(v * Math.PI) * 0.25f;

		modelInstance.transform.setToTranslationAndScaling(
				baseTranslation.x, baseTranslation.y, baseTranslation.z,
				baseScale.x + scale, baseScale.y + scale, baseScale.z + scale);
		rotation += 0.5f;
		modelInstance.transform.rotate(0, 0, 1, rotation);
	}
}
