package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public abstract class GameObject{

	public btRigidBody rigidBody;
	private static Vector3 localInertia = new Vector3();

	protected void initRigidBody(btCollisionShape shape, float mass, float friction, float damping, int userValue, Matrix4 transform) {
		if (mass > 0f) {
			shape.calculateLocalInertia(mass, localInertia);
		} else {
			localInertia.set(0, 0, 0);
		}
		btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(
				mass, null, shape, localInertia);
		constructionInfo.setFriction(friction);
		rigidBody = new btRigidBody(constructionInfo);
		rigidBody.setUserValue(userValue);
		rigidBody.setMotionState(new MotionState(transform));
		rigidBody.setCollisionFlags(rigidBody.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		constructionInfo.dispose();
	}

	public btRigidBody getRigidBody() {
		return rigidBody;
	}

	static class MotionState extends btMotionState {
		private Matrix4 transform;

		private Vector3 tmpVector = new Vector3();

		public MotionState(Matrix4 transform) {
			this.transform = transform;
		}
		@Override
		public void getWorldTransform (Matrix4 worldTrans) {
			worldTrans.set(transform);
		}
		@Override
		public void setWorldTransform (Matrix4 worldTrans) {
			// ignore rotation
			transform.idt();
			transform.trn(worldTrans.getTranslation(tmpVector));
		}
	}
}
