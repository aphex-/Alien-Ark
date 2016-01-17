package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class ScanAnimation extends BaseAnimation {

	private ModelInstance modelInstance;
	private Vector3 baseScale = new Vector3();
	private Vector3 baseTranslation = new Vector3();
	private float radius;

	public ScanAnimation(ModelInstance beamInstance, float radius, AnimationFinishedListener listener) {
 		super(750, listener);
		this.radius = radius;
		this.modelInstance = beamInstance;
		modelInstance.transform.getScale(baseScale);
		modelInstance.transform.getTranslation(baseTranslation);
		setLoopLength(3);
		App.audioController.playSound("scan.mp3");
	}

	@Override
	protected void onProgress(float v) {
		float scale = (1 - v) * radius;
		modelInstance.transform.setToTranslationAndScaling(
				baseTranslation.x, baseTranslation.y, baseTranslation.z,
				scale, scale, scale);
	}

	@Override
	protected void onLoopStart(int pLoopIndex) {

	}
}
