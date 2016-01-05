package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject{


	public List<btRigidBody> rigidBodyList = new ArrayList<btRigidBody>();
	public List<btCollisionObject> collisionList = new ArrayList<btCollisionObject>();

	protected void addRigidBody(btCollisionShape shape, float mass, float friction, int userValue, Matrix4 transform) {
		Vector3 localInertia = new Vector3();
		if (mass > 0f) {
			shape.calculateLocalInertia(mass, localInertia);
		} else {
			localInertia.set(0, 0, 0);
		}
		btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(
				mass, null, shape, localInertia);
		constructionInfo.setFriction(friction);

		btRigidBody rigidBody = new btRigidBody(constructionInfo);
		rigidBody.setUserValue(userValue);
		rigidBody.setMotionState(new MotionState(transform));
		rigidBody.setCollisionFlags(rigidBody.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
		constructionInfo.dispose();

		rigidBodyList.add(rigidBody);
	}

	protected void addCollisionObject(btCollisionObject object) {
		collisionList.add(object);
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
