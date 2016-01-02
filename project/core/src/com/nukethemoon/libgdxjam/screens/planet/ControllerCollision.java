package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Log;

public class ControllerCollision extends ContactListener {

	private DebugDrawer debugDrawer;

	public enum CollideType {
		GROUND((short) (1<<8)),
		ROCKET((short) (1<<9)),
		ALL((short)-1);
		public short mask;
		CollideType(short mask) {
			this.mask = mask;
		}
	}


	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;

	private btBroadphaseInterface broadphase;
	private btCollisionWorld collisionWorld;

	public ControllerCollision() {
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);

		if (Config.DEBUG_BULLET) {
			debugDrawer = new DebugDrawer();
			debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
			collisionWorld.setDebugDrawer(debugDrawer);
		}
	}

	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
								   btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

		int objectId1 = colObj0Wrap.getCollisionObject().getUserValue();
		int objectId2 = colObj1Wrap.getCollisionObject().getUserValue();

		Log.e(ControllerCollision.class, "Collision " + objectId1 + " " + objectId2);

		return true;
	}

	/*@Override
	public boolean onContactAdded (btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1,
								   int index1) {
		instances.get(colObj0.getUserValue()).moving = false;
		instances.get(colObj1.getUserValue()).moving = false;
		return true;
	}*/

	/*@Override
	public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
		instances.get(userValue0).moving = false;
		instances.get(userValue1).moving = false;
		return true;
	}*/

	public void addCollisionObject(btCollisionObject object, CollideType type, CollideType... collidesTo) {
		if (object == null) {
			return;
		}
		short collidesToMask = 0;
		for (CollideType c : collidesTo) {
			collidesToMask = (short) (collidesToMask | c.mask);
		}
		collisionWorld.addCollisionObject(object, type.mask, collidesToMask);
	}

	public void addCollisionObject(btCollisionObject object, CollideType type) {
		if (object == null) {
			return;
		}
		collisionWorld.addCollisionObject(object, type.mask, CollideType.ALL.mask);
	}

	public void removeCollisionObject(btCollisionObject object) {
		if (object == null) {
			return;
		}
		collisionWorld.removeCollisionObject(object);
		object.dispose();
	}

	public void discreteDetection() {
		collisionWorld.performDiscreteCollisionDetection();
	}


	public void debugRender(Camera camera) {
		if (Config.DEBUG_BULLET) {
			debugDrawer.begin(camera);
			collisionWorld.debugDrawWorld();
			debugDrawer.end();
		}

	}

	@Override
	public void dispose() {
		collisionConfig.dispose();
		dispatcher.dispose();
		collisionWorld.dispose();
		broadphase.dispose();
		super.dispose();
	}
}
