package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.InternalTickCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;

public class ControllerPhysic extends ContactListener {

	private final TickCallback tickCallback;
	private Rocket rocket;


	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private final btSequentialImpulseConstraintSolver constraintSolver;

	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private DebugDrawer debugDrawer;

	public ControllerPhysic(float gravity, Rocket rocket) {
		this.rocket = rocket;
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

		tickCallback = new TickCallback(dynamicsWorld, rocket);

	}

	/*@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
								   btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

		int objectId1 = colObj0Wrap.getCollisionObject().getUserValue();
		int objectId2 = colObj1Wrap.getCollisionObject().getUserValue();

		Log.e(ControllerPhysic.class, "Collision " + objectId1 + " " + objectId2);




		Log.e(ControllerPhysic.class, "impact");

		return true;
	}*/

	/*@Override
	public boolean onContactAdded (btCollisionObject colObj0, int partId0, int index0, btCollisionObject colObj1, int partId1,
								   int index1) {


		return true;
	}*/

	@Override
	public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
		if (userValue0 == Rocket.USER_VALUE) {
			rocket.collidedWith(userValue1);
		} else if (userValue1 == Rocket.USER_VALUE) {
			rocket.collidedWith(userValue0);
		}
		return true;
	}

	public void addRigidBody(btRigidBody object, CollisionTypes type, CollisionTypes... collidesTo) {
		if (object == null) {
			return;
		}
		short collidesToMask = 0;
		for (CollisionTypes c : collidesTo) {
			collidesToMask = (short) (collidesToMask | c.mask);
		}
		dynamicsWorld.addRigidBody(object, type.mask, collidesToMask);
	}

	public void addRigidBody(btRigidBody object, CollisionTypes type) {
		if (object == null) {
			return;
		}
		dynamicsWorld.addRigidBody(object, type.mask, CollisionTypes.ALL.mask);
	}

	public void removeRigidBody(btRigidBody object) {
		if (object == null) {
			return;
		}
		dynamicsWorld.removeRigidBody(object);
		object.dispose();
	}

	public void stepSimulation(float delta) {
		float timeStep = Math.min(1f / 60f, delta);
		int maxSubSteps = 10;
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
		tickCallback.dispose();
		super.dispose();
	}

	public static class TickCallback extends InternalTickCallback {

		private Rocket rocket;

		public TickCallback (btDynamicsWorld dynamicsWorld, Rocket rocket) {
			super(dynamicsWorld, true);
			this.rocket = rocket;
		}

		@Override
		public void onInternalTick (btDynamicsWorld dynamicsWorld, float timeStep) {
			rocket.onBulletTick();
		}

	}
}
