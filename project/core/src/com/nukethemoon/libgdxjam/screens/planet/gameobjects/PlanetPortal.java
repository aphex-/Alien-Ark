package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.helper.StandardMotionState;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

public class PlanetPortal extends GameObject {

	private final ModelInstance planetPortal;
	private final ModelInstance planetPortalTorus01;
	private final Vector3 ppt01Position = new Vector3(0, 0, 118);
	private final ModelInstance planetPortalTorus02;
	private final Vector3 ppt02Position = new Vector3(-20f, 0, 130);
	private final ModelInstance planetPortalTorus03;
	private final Vector3 ppt03Position = new Vector3(-40, 0, 142);
	private final ModelInstance planetPortalTorus04;
	private final Vector3 ppt04Position = new Vector3(-60f, 0, 154);

	private float rot01 = 0;
	private float rot02 = 45;
	private float rot03 = 160;
	private float rot04 = 200;

	public PlanetPortal() {
		// initialize PlanetPortal
		planetPortal = new ModelInstance(Models.PLANET_PORTAL);
		planetPortalTorus01 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus02 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus03 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus04 = new ModelInstance(Models.PLANET_PORTAL_TORUS);

		addRigidBody(Models.PORTAL_TUBE_COLLISION, 0, 0,
				CollisionTypes.GROUND.mask, new StandardMotionState(new Matrix4()));

		addRigidBody(Models.PORTAL_STAND_COLLISION, 0, 0,
				CollisionTypes.GROUND.mask, new StandardMotionState(new Matrix4()));

		btCollisionObject triggerObject = new btCollisionObject();
		triggerObject.setCollisionShape(Models.PORTAL_TRIGGER_COLLISION);
		triggerObject.setWorldTransform(new Matrix4());
		triggerObject.setUserValue(CollisionTypes.PORTAL_EXIT.mask);
		int collisionFlags = triggerObject.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE;
		triggerObject.setCollisionFlags(collisionFlags);
		addCollisionObject(triggerObject);

	}

	public void render(ModelBatch batch, Environment environment) {
		batch.render(planetPortal, environment);

		rot01++;
		planetPortalTorus01.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus01.transform.rotate(1, 0, 0, rot01);
		planetPortalTorus01.transform.trn(ppt01Position);

		rot02--;
		planetPortalTorus02.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus02.transform.rotate(1, 0, 0, rot02);
		planetPortalTorus02.transform.trn(ppt02Position);

		rot03++;
		planetPortalTorus03.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus03.transform.rotate(1, 0, 0, rot03);
		planetPortalTorus03.transform.trn(ppt03Position);

		rot04--;
		planetPortalTorus04.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus04.transform.rotate(1, 0, 0, rot04);
		planetPortalTorus04.transform.trn(ppt04Position);

		batch.render(planetPortalTorus01, environment);
		batch.render(planetPortalTorus02, environment);
		batch.render(planetPortalTorus03, environment);
		batch.render(planetPortalTorus04, environment);
	}
}
