package com.nukethemoon.libgdxjam.screens.planet.animations;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class EnterPlanetAnimation extends BaseAnimation {

	private final Bezier<Vector3> exitPath;
	private PerspectiveCamera camera;
	private Rocket rocket;
	private Vector3 tmpVector = new Vector3();
	private Vector3 tmpVector2 = new Vector3();

	public EnterPlanetAnimation(PerspectiveCamera camera, Rocket rocket, AnimationFinishedListener listener) {
		super(3000, listener);
		this.camera = camera;
		this.rocket = rocket;

		camera.up.set(Vector3.Z);

		exitPath = new Bezier<Vector3>();
		exitPath.set(
				new Vector3(-300, 0, 280),
				new Vector3(-30, 0, 140),
				new Vector3(20, 0, 110));
	}

	@Override
	protected void onProgress(float v) {

		Matrix4 transform = rocket.getRocketModelInstance().transform;
		transform.setToRotation(Vector3.Z, -90);
		transform.rotate(Vector3.X, -30);

		exitPath.valueAt(tmpVector, v);
		transform.trn(tmpVector);

		tmpVector2.set(tmpVector).sub(10, 0, -5);
		camera.position.set(tmpVector2);
		camera.lookAt(transform.getTranslation(tmpVector2));
		camera.fieldOfView = App.config.FOV + ((1 - v) * 60f);
		camera.update();

	}

	@Override
	protected void onFinish() {
		rocket.getLastCamPosition().set(camera.position);
		camera.update();
		rocket.setXRotation(0);
		rocket.setZRotation(-90);
	}
}
