package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

public class RaceWayPoint {


	transient private final btCollisionObject trigger;
	transient private final ModelInstance modelInstance;

	public float rotationZ = 0;
	public float rotationX = 0;

	public float x;
	public float y;

	public float zOffset;

	public RaceWayPoint() {
		modelInstance = new ModelInstance(Models.RACE_WAY_POINT);
		trigger = new btCollisionObject();
		int collisionFlags = trigger.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE;
		trigger.setCollisionFlags(collisionFlags);
		trigger.setCollisionShape(Models.RACE_WAY_POINT_COLLISION);
		trigger.setWorldTransform(modelInstance.transform);
		trigger.setUserValue(CollisionTypes.WAY_POINT_TRIGGER.mask);
	}

	public void adjust(float height) {
		modelInstance.transform.setToRotation(0, 0, 1, rotationZ);
		modelInstance.transform.rotate(1, 0, 0, rotationX);
		modelInstance.transform.trn(x, y, height + zOffset);
		trigger.setWorldTransform(modelInstance.transform);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}


	public btCollisionObject getTrigger() {
		return trigger;
	}

	public void dispose() {
		trigger.dispose();
	}
}
