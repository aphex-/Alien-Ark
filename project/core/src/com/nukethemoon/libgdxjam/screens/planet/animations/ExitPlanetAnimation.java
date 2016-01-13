package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;


public class ExitPlanetAnimation extends BaseAnimation {

	private final Bezier<Vector3> exitPath;
	private PerspectiveCamera camera;
	private Rocket rocket;
	private float baseFieldOfView;
	boolean playedLaunchSound = false;
	private Vector3 tmpVector = new Vector3();
	private Vector3 tmpVector2 = new Vector3();

	public ExitPlanetAnimation(PerspectiveCamera camera, Rocket rocket, AnimationFinishedListener listener) {
		super(3000, listener);
		this.camera = camera;
		this.rocket = rocket;
		this.baseFieldOfView = camera.fieldOfView;
		rocket.setThirdPersonCam(null);
		exitPath = new Bezier<Vector3>();
		exitPath.set(
				new Vector3(20, 0, 110),
				new Vector3(-30, 0, 140),
				new Vector3(-300, 0, 280));
	}

	@Override
	protected void onProgress(float v) {
		float p1 = computeIntervalProgress(v, 0, 0.5f);
		float p2 = computeIntervalProgress(v, 0.3f, 1.0f);

		Matrix4 transform = rocket.getRocketModelInstance().transform;
		int targetZRotation = 90;
		transform.setToRotation(Vector3.Z, targetZRotation); // 90
		int targetXRotation = 30;
		transform.rotate(Vector3.X, targetXRotation); // 30

		if (p2 > 0) {
			exitPath.valueAt(tmpVector, v);
			if (!playedLaunchSound) {
				App.audioController.playSound("robo_laser.mp3");
				playedLaunchSound = true;
			}

		} else {
			tmpVector.set(exitPath.points.get(0));
		}

		transform.trn(tmpVector);
		tmpVector2.set(tmpVector).sub(-10, 0, 5);
		camera.position.set(tmpVector2);
		camera.lookAt(transform.getTranslation(tmpVector2));
		if (p1 > 0) {
			camera.fieldOfView = baseFieldOfView + (p1 * 60f);
			camera.update();
		}
		camera.update();
	}

}
