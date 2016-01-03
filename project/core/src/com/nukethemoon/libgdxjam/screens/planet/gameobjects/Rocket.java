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
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Disposable;

public class Rocket extends GameObject implements Disposable {

	private static final float THIRD_PERSON_OFFSET_Z = 10;
	private static final Vector3 START_POSITION = new Vector3(0, 50, 30);

	private final ModelInstance modelInstance;
	private final Model model;

	private float maneuverability = 1f;

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

	private ParticleEffect particleEffect;
	private ParticleSystem particleSystem;


	public Rocket() {
		// init graphic
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/rocket.obj"));
		modelInstance = new ModelInstance(model);

		// init physic
		btCollisionShape shape = new btSphereShape(1);
		modelInstance.transform.setToTranslation(START_POSITION);
		initRigidBody(shape, 1, 0, modelInstance.transform);
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

		rotationMatrix.setToRotation(Vector3.Z, zRotation);
		rotationMatrix.rotate(Vector3.X, xRotation);
		rotationMatrix.rotate(Vector3.Y, drill);
		modelInstance.transform.mul(rotationMatrix);
	}

	public void toggleThrust() {
		thrusting = !thrusting;
		if (!thrusting) {
			particleSystem.remove(particleEffect);
		} else {
			particleSystem.add(particleEffect);
		}
	}

	private void thrust() {
		tmpMovement.set(getDirection()).nor().scl(35);
		rigidBody.setLinearVelocity(tmpMovement);
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
		tmpCamPosition.set(getPosition());

		tmpCamOffset.set(getDirection());
		tmpCamOffset.scl(-thirdPersonOffsetY);

		tmpCamPosition.add(tmpCamOffset);
		tmpCamPosition.z += THIRD_PERSON_OFFSET_Z;

		// creating a delay for the camera
		tmpCamPosition.x = lastCamPosition.x + (tmpCamPosition.x - lastCamPosition.x) / 30f;
		tmpCamPosition.y = lastCamPosition.y + (tmpCamPosition.y - lastCamPosition.y) / 30f;
		tmpCamPosition.z = lastCamPosition.z + (tmpCamPosition.z - lastCamPosition.z) / 30f;

		camera.position.set(tmpCamPosition);
		camera.lookAt(getPosition());
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

}
