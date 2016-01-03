package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Log;

public class ControllerPhysic extends ContactListener {


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
	private final btSequentialImpulseConstraintSolver constraintSolver;

	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private DebugDrawer debugDrawer;

	public ControllerPhysic(float gravity) {
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, 0, gravity));

		if (Config.DEBUG_BULLET) {
			debugDrawer = new DebugDrawer();
			debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
			dynamicsWorld.setDebugDrawer(debugDrawer);
		}
	}

	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
								   btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

		int objectId1 = colObj0Wrap.getCollisionObject().getUserValue();
		int objectId2 = colObj1Wrap.getCollisionObject().getUserValue();

		Log.e(ControllerPhysic.class, "Collision " + objectId1 + " " + objectId2);

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

	public void addRigidBody(btRigidBody object, CollideType type, CollideType... collidesTo) {
		if (object == null) {
			return;
		}
		short collidesToMask = 0;
		for (CollideType c : collidesTo) {
			collidesToMask = (short) (collidesToMask | c.mask);
		}
		dynamicsWorld.addRigidBody(object, type.mask, collidesToMask);
	}

	public void addRigidBody(btRigidBody object, CollideType type) {
		if (object == null) {
			return;
		}
		dynamicsWorld.addRigidBody(object, type.mask, CollideType.ALL.mask);
	}

	public void removeRigidBody(btRigidBody object) {
		if (object == null) {
			return;
		}
		dynamicsWorld.removeRigidBody(object);
		object.dispose();
	}

	public void stepSimulation() {
		float timeStep = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
		int maxSubSteps = 5;
		float fixedTimeStep = 1f / 60f;
		dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
	}


	public void debugRender(Camera camera) {
		if (Config.DEBUG_BULLET) {
			debugDrawer.begin(camera);
			dynamicsWorld.debugDrawWorld();
			debugDrawer.end();
		}

	}

	@Override
	public void dispose() {
		collisionConfig.dispose();
		dispatcher.dispose();
		dynamicsWorld.dispose();
		broadphase.dispose();
		constraintSolver.dispose();
		super.dispose();
	}
}
