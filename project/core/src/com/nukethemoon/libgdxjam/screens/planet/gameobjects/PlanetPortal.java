package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.nukethemoon.libgdxjam.Models;
import com.nukethemoon.libgdxjam.screens.planet.helper.StandardMotionState;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

public class PlanetPortal extends GameObject {

	private final ModelInstance planetPortal;
	private final ModelInstance planetPortalTorus01;
	private final ModelInstance planetPortalTorus02;
	private final ModelInstance planetPortalTorus03;
	private final ModelInstance planetPortalTorus04;

	public PlanetPortal() {
		// initialize PlanetPortal
		planetPortal = new ModelInstance(Models.PLANET_PORTAL);
		planetPortalTorus01 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus01.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus01.transform.trn(0, 0, 118);

		planetPortalTorus02 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus02.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus02.transform.trn(-20f, 0, 130);


		planetPortalTorus03 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus03.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus03.transform.trn(-40, 0, 142);

		planetPortalTorus04 = new ModelInstance(Models.PLANET_PORTAL_TORUS);
		planetPortalTorus04.transform.setToRotation(0, 1, 0, 30);
		planetPortalTorus04.transform.trn(-60f, 0, 154);

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

		batch.render(planetPortalTorus01, environment);
		batch.render(planetPortalTorus02, environment);
		batch.render(planetPortalTorus03, environment);
		batch.render(planetPortalTorus04, environment);
	}
}
