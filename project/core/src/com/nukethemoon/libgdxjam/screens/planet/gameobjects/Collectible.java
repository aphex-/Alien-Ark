package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.animations.CollectibleStandardAnimation;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.BaseAnimation;

import java.awt.*;

public class Collectible {

	private final ModelInstance modelInstance;

	private final btCollisionObject collisionObject;
	private CollectibleStandardAnimation collectibleStandardAnimation;

	private Point planetPartPosition;

	private CollisionTypes type;

	public Collectible(CollisionTypes type, Vector3 position, Point planetPartPosition) {
		this.type = type;
		this.planetPartPosition = planetPartPosition;
		Matrix4 transform = new Matrix4();
		transform.setToTranslation(position.x, position.y, position.z);
		collisionObject = new btCollisionObject();

		if (type == CollisionTypes.FUEL) {
			modelInstance = new ModelInstance(Models.FUEL);
			collisionObject.setCollisionShape(Models.FUEL_SHAPE);
		} else {
			modelInstance = new ModelInstance(Models.SHIELD);
			collisionObject.setCollisionShape(Models.SHIELD_SHAPE);
		}

		modelInstance.transform.set(transform);
		int collisionFlags = collisionObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE;
		collisionObject.setCollisionFlags(collisionFlags);
		collisionObject.setWorldTransform(transform);
		collisionObject.setUserValue(type.mask);
	}

	public BaseAnimation createScaleAnimation() {
		collectibleStandardAnimation = new CollectibleStandardAnimation(modelInstance);
		return collectibleStandardAnimation;
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public btCollisionObject getCollisionObject() {
		return collisionObject;
	}

	public void dispose(Ani ani) {
		if (collectibleStandardAnimation != null) {
			ani.forceStop(collectibleStandardAnimation);
		}
	}

	public Point getPlanetPartPosition() {
		return planetPartPosition;
	}

	public CollisionTypes getType() {
		return type;
	}
}
