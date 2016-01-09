package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.tools.ani.BaseAnimation;

public class TractorBeamAnimation extends BaseAnimation {

	private ModelInstance modelInstance;
	private Vector3 baseScale = new Vector3();
	private Vector3 baseTranslation = new Vector3();
	private float radius;

	public TractorBeamAnimation(ModelInstance beamInstance, float radius) {
		super(500);
		this.radius = radius;
		this.modelInstance = beamInstance;
		modelInstance.transform.getScale(baseScale);
		modelInstance.transform.getTranslation(baseTranslation);
		loopInfinite();
	}

	@Override
	protected void onProgress(float v) {
		float scale = (1 - v) * radius;
		modelInstance.transform.setToTranslationAndScaling(
				baseTranslation.x, baseTranslation.y, baseTranslation.z,
				baseScale.x + scale, baseScale.y + scale, baseScale.z + scale);
	}
}
