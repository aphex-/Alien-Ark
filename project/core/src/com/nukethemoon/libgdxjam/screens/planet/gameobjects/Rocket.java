package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPhysic;

public class Rocket extends GameObject implements Disposable {

	public static final int USER_VALUE = 1337;

	private static final float THIRD_PERSON_OFFSET_Z = 10;
	private static final Vector3 START_POSITION = new Vector3(0, 50, 30);
	private static final Vector3 LAUNCH_IMPULSE = new Vector3(0, 0, 55);

	private final ModelInstance modelInstance;
	private final Model model;

	private float maneuverability = 1f;
	private float speed = 35f;
	private float friction = 0.5f;
	private int shield = 100;
	private int maxShield = 1000;
	private int fuel = 100;
	private int maxFuel = 1000;


	float drill = 0;
	float xRotation = 0;
	float zRotation = 0;

	private float thirdPersonOffsetY = 10;

	private Vector3 lastCamPosition = new Vector3();
	private Vector3 tmpCamPosition = new Vector3();
	private Vector3 tmpCamOffset = new Vector3();

	private Matrix4 rotationMatrix = new Matrix4();

	private Vector3 tmpDirection = new Vector3();
	private Vector3 tmpPosition = new Vector3();
	private Vector3 tmpMovement = new Vector3();

	private boolean thrusting = true;
	private boolean moving = true;

	private ParticleEffect particleEffect;
	private ParticleSystem particleSystem;

	private RocketListener listener;

	private float tickFuelCount = 1;


	public Rocket() {
		// init graphic
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/rocket.obj"));
		modelInstance = new ModelInstance(model);

		// init physic

		BoundingBox boundingBox = new BoundingBox();
		model.calculateBoundingBox(boundingBox);
		btCollisionShape shape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.5f));

		//btCollisionShape shape = new btSphereShape(1);

		modelInstance.transform.setToTranslation(START_POSITION);

		float mass = 1;
		initRigidBody(shape, mass, friction, USER_VALUE, modelInstance.transform);
		rigidBody.setActivationState(4); // disable deactivation
	}

	public void setListener(RocketListener listener) {
		this.listener = listener;
	}


	public void setParticleEffect(ParticleEffect particleEffect, ParticleSystem particleSystem) {
		this.particleEffect = particleEffect;
		this.particleSystem = particleSystem;
	}

	public void rotateLeft() {
		if (thrusting) {
			zRotation = (zRotation + maneuverability) % 360;
		}
	}

	public void rotateRight() {
		if (thrusting) {
			zRotation = (zRotation - maneuverability) % 360;
		}
	}

	public void rotateUp() {
		if (thrusting) {
			xRotation = (xRotation - maneuverability) % 360;
		}
	}

	public void rotateDown() {
		if (thrusting) {
			xRotation = (xRotation + maneuverability) % 360;
		}
	}

	public void update() {
		if (thrusting) {
			thrust();
			drill += 2;
		}

		if (rigidBody.getLinearVelocity().len() != 0) {
			rotationMatrix.setToRotation(Vector3.Z, zRotation);
			rotationMatrix.rotate(Vector3.X, xRotation);
			rotationMatrix.rotate(Vector3.Y, drill);
			modelInstance.transform.mul(rotationMatrix);
		} else {
			if (moving) {
				moving = false;
				onStops();
			}
		}
	}

	private void thrust() {
		if (fuel <= 0) {
			return;
		}
		tmpMovement.set(getDirection()).nor().scl(speed);
		rigidBody.applyCentralForce(tmpMovement);
	}

	private void onLaunch() {
		if (fuel <= 0) {
			return;
		}

		rigidBody.applyCentralImpulse(LAUNCH_IMPULSE);
		moving = true;

		if (listener != null) {
			listener.onRocketLaunched();
		}
	}

	private void onStops() {

		if (listener != null) {
			listener.onRocketStopped();
		}
	}

	private void onThrustEnabled() {
		if (fuel <= 0) {
			return;
		}
		particleSystem.add(particleEffect);
		if (!moving) {
			onLaunch();
		}

		if (listener != null) {
			listener.onRocketEnabledThrust();
		}
	}

	private void onThrustDisabled() {
		particleSystem.remove(particleEffect);

		if (listener != null) {
			listener.onRocketDisabledThrust();
		}
	}

	private void onExplode() {
		if (listener != null) {
			listener.onRocketExploded();
		}
	}

	public void toggleThrust() {
		thrusting = !thrusting;
		if (!thrusting) {
			onThrustDisabled();
		} else {
			onThrustEnabled();
		}
	}

	public Vector3 getDirection() {
		tmpDirection.set(0, 1, 0);
		tmpDirection.rotate(Vector3.X, xRotation);
		tmpDirection.rotate(Vector3.Z, zRotation);
		return tmpDirection.nor();
	}


	public Vector3 getPosition() {
		return rigidBody.getWorldTransform().getTranslation(tmpPosition);
	}

	public void reduceThirdPersonOffsetY() {
		thirdPersonOffsetY--;
	}

	public void increaseThirdPersonOffsetY() {
		thirdPersonOffsetY++;
	}

	public void applyThirdPerson(Camera camera) {

		Vector3 position = getPosition();
		if (position.z < 0) {
			return;
		}

		tmpCamPosition.set(position);

		tmpCamOffset.set(getDirection());
		tmpCamOffset.scl(-thirdPersonOffsetY);

		tmpCamPosition.add(tmpCamOffset);
		tmpCamPosition.z += THIRD_PERSON_OFFSET_Z;

		// creating a delay for the camera
		//tmpCamPosition.x = lastCamPosition.x + (tmpCamPosition.x - lastCamPosition.x) / 20f;
		//tmpCamPosition.y = lastCamPosition.y + (tmpCamPosition.y - lastCamPosition.y) / 20f;
		//tmpCamPosition.z = lastCamPosition.z + (tmpCamPosition.z - lastCamPosition.z) / 20f;

		tmpCamPosition.z = Math.max(tmpCamPosition.z, 1);

		camera.position.set(tmpCamPosition);
		camera.lookAt(position);
		camera.up.set(Vector3.Z);

		lastCamPosition.set(camera.position);
	}


	public void drawModel(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(modelInstance, environment);
		particleEffect.setTransform(modelInstance.transform);
	}

	@Override
	public void dispose() {
		model.dispose();
		particleEffect.dispose();
	}

	public void onBulletTick() {
		if (fuel <= 0) {
			if (thrusting) {
				onThrustDisabled();
			}
			return;
		}

		if (thrusting) {
			tickFuelCount = tickFuelCount - 0.01f;
			if (tickFuelCount <= 0) {
				tickFuelCount = 1;
				fuel = fuel -1;
				if (listener != null) {
					listener.onRocketFuelConsumed();
				}
			}

			// cap the peed
			Vector3 linearVelocity = rigidBody.getLinearVelocity();
			float tickSpeed = linearVelocity.len();
			if (tickSpeed > speed) {
				linearVelocity.scl(speed / tickSpeed);
				rigidBody.setLinearVelocity(tmpMovement);
			}
		}
	}

	public void collidedWith(int userValue1) {
		if (userValue1 == ControllerPhysic.DAMAGE_ON_COLLIDE_USER_VALUE) {
			shield--;

			if (listener != null) {
				listener.onRocketDamage();
			}
		}

		if (shield == 0) {
			onExplode();
		}
	}

	public int getShield() {
		return shield;
	}

	public int getMaxShield() {
		return maxShield;
	}

	public int getFuel() {
		return fuel;
	}

	public int getMaxFuel() {
		return maxFuel;
	}
}
