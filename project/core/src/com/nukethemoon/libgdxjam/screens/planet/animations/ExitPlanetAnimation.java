package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;


public class ExitPlanetAnimation extends BaseAnimation {

	private final Vector3 rocketStartPosition;
	private final float xRotation;
	private final float zRotation;

	private PerspectiveCamera camera;
	private Rocket rocket;
	private float baseFieldOfView;

	private final int targetZRotation = 90;
	private final int targetXRotation = 30;

	private Vector3 cameraTarget01 = new Vector3(0, 0, 100);

	private Vector3 cameraStartPosition = new Vector3();

	private Vector3 tmpVector = new Vector3();

	public ExitPlanetAnimation(PerspectiveCamera camera, Rocket rocket, AnimationFinishedListener listener) {
		super(5000, listener);
		this.camera = camera;
		this.rocket = rocket;
		this.baseFieldOfView = camera.fieldOfView;
		rocketStartPosition = rocket.getPosition();
		xRotation = rocket.getXRotation();
		zRotation = rocket.getZRotation();
		rocket.setThirdPersonCam(null);
		cameraStartPosition.set(camera.position);
	}

	@Override
	protected void onProgress(float v) {

		float steadyProgress = computeIntervalProgress(v, 0, 0.2f);
		float fieldOfViewProgress = computeIntervalProgress(v, 0.2f, 0.3f);

		if (steadyProgress > 0) {
			Matrix4 transform = rocket.getRocketModelInstance().transform;
			transform.setToRotation(Vector3.Z, zRotation + ((targetZRotation - zRotation) * steadyProgress)); // 90
			transform.rotate(Vector3.X, xRotation + ((targetXRotation - xRotation) * steadyProgress)); // 30
			transform.trn(rocketStartPosition);

			//tmpVector.set(cameraStartPosition);
			//tmpVector.sub(cameraTarget01).scl(steadyProgress);
			//camera.position.set(tmpVector);
			//camera.update();
		}

		if (fieldOfViewProgress > 0) {
			camera.fieldOfView = baseFieldOfView + (fieldOfViewProgress * 60f);
			camera.update();
		}

	}

}
