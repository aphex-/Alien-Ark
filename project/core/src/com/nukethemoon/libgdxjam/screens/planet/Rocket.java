package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Rocket {

	private final ModelInstance modelInstance;

	private float currentSpeed = 0.3f;
	private float maneuverability = 1f;


	private Vector3 position = new Vector3(0, 0, 10);
	private Matrix4 rotationMatrix = new Matrix4();
	private Matrix4 translationMatrix = new Matrix4();

	float drill = 0;
	float xRotation = 0;
	float zRotation = 0;

	private Vector3 tmpPositionVector = new Vector3();

	public Rocket() {
		ModelLoader loader = new ObjLoader();
		Model model = loader.loadModel(Gdx.files.internal("models/rocket.obj"));
		modelInstance = new ModelInstance(model);
	}

	public void rotateLeft() {
		zRotation = (zRotation + maneuverability) % 360;
	}

	public void rotateRight() {
		zRotation = (zRotation - maneuverability) % 360;
	}

	public void rotateUp() {
		xRotation = (xRotation - maneuverability) % 360;
	}

	public void rotateDown() {
		xRotation = (xRotation + maneuverability)  % 360;
	}



	public void update() {
		modelInstance.transform.idt();
		drill += 1;

		rotationMatrix.setToRotation(Vector3.Z, zRotation);
		rotationMatrix.rotate(Vector3.X, xRotation);
		rotationMatrix.rotate(Vector3.Y, drill);

		translationMatrix.idt();
		translationMatrix.translate(position);

		modelInstance.transform.mul(translationMatrix);
		modelInstance.transform.mul(rotationMatrix);


	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}


	public void thrust() {
		tmpPositionVector.set(0, 1, 0);
		tmpPositionVector.rotate(Vector3.X, xRotation);
		tmpPositionVector.rotate(Vector3.Z, zRotation);
		tmpPositionVector.nor().scl(currentSpeed);
		position.add(tmpPositionVector);
	}


	public Vector3 getPosition() {
		return position;
	}
}
