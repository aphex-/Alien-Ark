package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.input.DebugCameraInput;

import java.util.concurrent.ExecutionException;

public class PlanetScreen implements Screen {

	private final ModelBatch modelBatch;
	private final Environment environment;
	private final PerspectiveCamera cam;
	private final Model model;
	private final ModelInstance ship;

	private final Vector3 shipPosition = new Vector3(0, 0, 10);
	private float shipRotationZ = 0;
	private float shipSpeed = 10;


	private WorldController world;

	public PlanetScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 10f);
		cam.lookAt(0,0,0);
		cam.near = 0.01f;
		cam.far = 30000f;
		cam.update();

		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/ship_placeholder.obj"));
		ship = new ModelInstance(model);

		world = new WorldController();

		multiplexer.addProcessor(new DebugCameraInput(cam));


	}

	@Override
	public void show() {

	}

	Vector3 tmpVector = new Vector3(0, 0, 0);
	Vector2 tmpVector2 = new Vector2();

	@Override
	public void render(float delta) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);




		tmpVector.set(0, -10, 0);
		tmpVector.rotate(shipRotationZ, 0, 0, 1);
		tmpVector.add(shipPosition);
		cam.position.set(tmpVector.x, tmpVector.y, 20);
		cam.lookAt(shipPosition);
		cam.up.set(0, 0, 1);
		cam.update();

		int chunkX = (int) (shipPosition.x / world.getChunkSize());
		int chunkY = (int) (shipPosition.y / world.getChunkSize());
		tmpVector2.set(chunkX, chunkY);

		world.requestChunks(tmpVector2);


		tmpVector.set(0, shipSpeed * delta, 0);
		tmpVector.rotate(shipRotationZ, 0, 0, 1);


		ship.transform.idt();

		shipPosition.add(tmpVector);
		ship.transform.translate(shipPosition.x, shipPosition.y, shipPosition.z);

		if (Gdx.app.getInput().isKeyPressed(21)) {
			shipRotationZ += 100 * delta;
		}

		if (Gdx.app.getInput().isKeyPressed(22)) {
			shipRotationZ -= 100 * delta;
		}



		ship.transform.rotate(0, 0, 1, shipRotationZ);


		modelBatch.begin(cam);
		modelBatch.render(ship, environment);
		world.render(modelBatch);
		modelBatch.end();


	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}


}
