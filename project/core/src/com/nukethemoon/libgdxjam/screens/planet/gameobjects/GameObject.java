package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public abstract class GameObject{

	protected btCollisionObject collisionObject;


	public btCollisionObject getCollisionObject() {
		return collisionObject;
	}
}
