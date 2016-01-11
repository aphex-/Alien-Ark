package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class Models {

	public static Model FUEL;
	public static btCollisionShape FUEL_SHAPE;

	public static Model SHIELD;
	public static btCollisionShape SHIELD_SHAPE;

	public static Model ARTIFACT_E;
	public static Model ARTIFACT_W;
	public static Model ARTIFACT_F;

	public static Model PLANET_PORTAL;
	public static Model PLANET_PORTAL_TORUS;


	public static void init() {
		ModelLoader loader = new ObjLoader();
		BoundingBox boundingBox = new BoundingBox();
		ObjLoader.ObjLoaderParameters param = new ObjLoader.ObjLoaderParameters(true);

		FUEL = loader.loadModel(Gdx.files.internal("models/fuel.obj"), param);
		FUEL.calculateBoundingBox(boundingBox);
		FUEL_SHAPE = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.65f));

		SHIELD = loader.loadModel(Gdx.files.internal("models/shield.obj"), param);
		SHIELD.calculateBoundingBox(boundingBox);
		SHIELD_SHAPE = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.65f));

		ARTIFACT_E = loader.loadModel(Gdx.files.internal("models/artifact_e.obj"), param);
		ARTIFACT_W = loader.loadModel(Gdx.files.internal("models/artifact_w.obj"), param);
		ARTIFACT_F = loader.loadModel(Gdx.files.internal("models/artifact_f.obj"), param);

		PLANET_PORTAL = 		loader.loadModel(Gdx.files.internal("models/planetPortal.obj"), param);
		PLANET_PORTAL_TORUS = 	loader.loadModel(Gdx.files.internal("models/portalTorus.obj"), param);
	}

	public static void dispose() {
		FUEL.dispose();
		FUEL_SHAPE.dispose();
		SHIELD.dispose();
		SHIELD_SHAPE.dispose();
		ARTIFACT_E.dispose();
	}
}
