package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.nukethemoon.libgdxjam.screens.planet.animations.CollectibleStandardAnimation;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.BaseAnimation;

public class Collectible {

	private final Model model;
	private final ModelInstance modelInstance;

	private final btCollisionObject collisionObject;
	private final btBoxShape shape;
	private CollectibleStandardAnimation collectibleStandardAnimation;

	public Collectible(CollisionTypes type) {
		// graphic
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/fuel.obj"));
		Matrix4 transform = new Matrix4();
		transform.setToTranslation(0, 100, 30);
		modelInstance = new ModelInstance(model);
		modelInstance.transform.set(transform);

		// physic
		collisionObject = new btCollisionObject();
		BoundingBox boundingBox = new BoundingBox();
		model.calculateBoundingBox(boundingBox);
		shape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.65f));
		collisionObject.setCollisionShape(shape);
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
		shape.dispose();
	}


}
