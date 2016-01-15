package com.nukethemoon.libgdxjam.screens.planet.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
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
import com.nukethemoon.libgdxjam.Log;

public class ControllerPhysic extends ContactListener {

	private final TickCallback tickCallback;

	private btCollisionConfiguration collisionConfig;
	private btDispatcher dispatcher;
	private final btSequentialImpulseConstraintSolver constraintSolver;

	private btBroadphaseInterface broadphase;
	private btDynamicsWorld dynamicsWorld;
	private DebugDrawer debugDrawer;

	private static final Vector3 rayFrom = new Vector3();
	private static final Vector3 rayTo = new Vector3();
	private static final Vector3 tmpVector = new Vector3();
	private static final Vector3 tmpVector2 = new Vector3();
	private static final ClosestRayResultCallback callback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

	private PhysicsListener listener;

	public ControllerPhysic(float gravity, PhysicsListener listener) {
		this.listener = listener;
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
		dynamicsWorld.setGravity(new Vector3(0, 0, gravity * 1.75f));

		if (Config.DEBUG_BULLET) {
			debugDrawer = new DebugDrawer();
			debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
			dynamicsWorld.setDebugDrawer(debugDrawer);
		}

		tickCallback = new TickCallback(dynamicsWorld, listener);

	}

	@Override
	public boolean onContactAdded (btCollisionObjectWrapper colObj0Wrap, int partId0, int index0,
								   btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {

		int userValue0 = colObj0Wrap.getCollisionObject().getUserValue();
		int userValue1 = colObj1Wrap.getCollisionObject().getUserValue();

		if (userValue0 == CollisionTypes.ROCKET.mask) {
			listener.onRocketCollided(CollisionTypes.byMask((short) userValue1), colObj1Wrap.getCollisionObject());
		} else if (userValue1 == CollisionTypes.ROCKET.mask) {
			listener.onRocketCollided(CollisionTypes.byMask((short) userValue0), colObj0Wrap.getCollisionObject());
		}

		colObj0Wrap.dispose();
		colObj1Wrap.dispose();

		return true;
	}



	public void addRigidBody(btRigidBody object, CollisionTypes type) {
		if (object == null || type == null) {
			Log.e(ControllerPhysic.class, "Tried to ad an invalid rigid body");
			return;
		}
		dynamicsWorld.addRigidBody(object, type.mask, CollisionTypes.toMask(CollisionTypes.getColliders(type)));
	}

	public void addCollisionObject(btCollisionObject object) {
		dynamicsWorld.addCollisionObject(object, CollisionTypes.WATER.mask, CollisionTypes.ROCKET.mask);
	}


	public void removeRigidBody(btRigidBody object) {
		if (object == null) {
			return;
		}
		dynamicsWorld.removeRigidBody(object);
		object.dispose();
	}

	public void removeCollisionObject(btCollisionObject object) {
		if (object == null) {
			return;
		}
		dynamicsWorld.removeCollisionObject(object);
		object.dispose();
	}

	public void stepSimulation(float delta) {
		final float delta2 = Math.min(1f / 60f, Gdx.graphics.getDeltaTime());
		dynamicsWorld.stepSimulation(delta2, 1, 1f/60f);

		/*float timeStep = Math.min(1f / 60f, delta);
		int maxSubSteps = 10;
		float fixedTimeStep = 1f / 60f;
		dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);*/
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
		callback.dispose();
		super.dispose();
	}

	public interface PhysicsListener {
		void onRocketCollided(CollisionTypes type, btCollisionObject collisionObject);
		void onInternalTick();
	}



	public Vector3 calculateVerticalIntersection(Vector3 position, Vector3 out) {
		rayFrom.set(position);
		rayTo.set(position.x, position.y, -500);
		callback.setCollisionObject(null);
		callback.setClosestHitFraction(1f);
		callback.setRayFromWorld(rayFrom);
		callback.setRayToWorld(rayTo);
		callback.setCollisionFilterMask((short) -1);
		callback.setCollisionFilterGroup((short) -1);
		dynamicsWorld.rayTest(rayFrom, rayTo, callback);
		if (callback.hasHit()) {
			callback.getHitPointWorld(out);
			return out;
		}
		return null;
	}

	public Vector3 calculateCameraPickIntersection(Camera camera, int screenX, int screenY, Vector3 out) {
		Ray pickRay = camera.getPickRay(screenX, screenY);
		rayFrom.set(pickRay.origin);
		tmpVector2.set(pickRay.direction).nor().scl(1000);
		tmpVector.set(pickRay.origin).add(tmpVector2);
		rayTo.set(tmpVector);
		callback.setCollisionObject(null);
		callback.setClosestHitFraction(1f);
		callback.setRayFromWorld(rayFrom);
		callback.setRayToWorld(rayTo);
		callback.setCollisionFilterMask((short) -1);
		callback.setCollisionFilterGroup((short) -1);
		dynamicsWorld.rayTest(rayFrom, rayTo, callback);
		if (callback.hasHit()) {
			callback.getHitPointWorld(out);
			return out;
		}
		return null;
	}

	public static class TickCallback extends InternalTickCallback {
		private PhysicsListener listener;
		public TickCallback (btDynamicsWorld dynamicsWorld, PhysicsListener listener) {
			super(dynamicsWorld, true);
			this.listener = listener;
		}
		@Override
		public void onInternalTick (btDynamicsWorld dynamicsWorld, float timeStep) {
			listener.onInternalTick();
		}
	}


}
