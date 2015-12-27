package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.input.FreeCameraInput;
import com.nukethemoon.libgdxjam.screens.planet.devtools.DevWindow;

public class PlanetScreen implements Screen, InputProcessor {


	private final ModelBatch modelBatch;
	private final Environment environment;
	private final PerspectiveCamera camera;
	private final Model model;
	private final ModelInstance ship;

	private final Vector3 shipPosition = new Vector3(0, 0, 20);
	private final ShapeRenderer screenShapeRenderer;
	private final InputMultiplexer multiplexer;
	private final Skin uiSkin;
	private final int worldIndex;


	private float shipRotationZ = 0;

	private int [] shipSpeedLevels = new int []{0, 2, 4, 8, 12, 20};
	private final int MAX_SPEED_LEVEL = shipSpeedLevels.length - 1;
	private static final float SPEED_DECREASE_BY_DECAY_RATE = 0.02f;
	private static final float SPEED_DECREASE_BY_BRAKES_RATE = 0.1f;
	private float currentSpeedDecay = 0;
	private int currentSpeedLevel = 8;

	private Vector3 tmpVector = new Vector3(0, 0, 0);

	private WorldController world;
	private Stage stage;

	private final FreeCameraInput freeCameraInput;

	public PlanetScreen(Skin puiSkin, InputMultiplexer pMultiplexer, int pWorldIndex) {
		uiSkin = puiSkin;
		worldIndex = pWorldIndex;
		multiplexer = pMultiplexer;
		initStage();


		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.5f, 0.5f, -1f, -0.8f, -0.2f));
		environment.add(new DirectionalLight().set(0.8f, 0.5f, 0.5f, 1f, 0.8f, -0.2f));

		screenShapeRenderer = new ShapeRenderer();
		screenShapeRenderer.setAutoShapeType(true);

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 0, 10f);
		camera.lookAt(0, 0, 0);
		camera.near = 0.01f;
		camera.far = 30000f;
		camera.update();

		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/ship_placeholder.obj"));
		ship = new ModelInstance(model);

		world = new WorldController(worldIndex);


		multiplexer.addProcessor(this);
		freeCameraInput = new FreeCameraInput(camera);
		freeCameraInput.setEnabled(false);
		multiplexer.addProcessor(freeCameraInput);
	}

	private void initStage() {
		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);


		if (Config.DEBUG) {
			final DevWindow devWindow = new DevWindow(uiSkin);
			devWindow.setVisible(false);
			stage.addActor(devWindow);

			TextButton devButton = new TextButton("dev", uiSkin);
			devButton.setPosition(10, Gdx.graphics.getHeight() - devButton.getHeight() - 10);
			stage.addActor(devButton);

			devButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					devWindow.setVisible(true);
				}
			});
		}
	}

	@Override
	public void show() {

	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		tmpVector.set(0, -10, 0);
		tmpVector.rotate(shipRotationZ, 0, 0, 1);
		tmpVector.add(shipPosition);

		if (!freeCameraInput.isEnabled()) {
			camera.position.set(tmpVector.x, tmpVector.y, shipPosition.z + 6);
			camera.lookAt(shipPosition);
			camera.up.set(0, 0, 1);
		} else {
			freeCameraInput.update(delta);
		}
		camera.update();


		world.updateRequestCenter(shipPosition.x, shipPosition.y);

		tmpVector.set(0, calculateBoostedSpeed() * 0.1f, 0);
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


		modelBatch.begin(camera);
		modelBatch.render(ship, environment);
		world.render(modelBatch, environment);
		modelBatch.end();

		drawOrigin();

		if (stage != null) {
			stage.act(delta);
			stage.draw();
		}

	}

	private int calculateBoostedSpeed() {
		currentSpeedDecay += SPEED_DECREASE_BY_DECAY_RATE;
		if (currentSpeedDecay > 1) {
			currentSpeedDecay = 0;
			currentSpeedLevel -=1;
		}
		if (Gdx.app.getInput().isKeyPressed(19)) {
			currentSpeedLevel += 1;
		}
		if (Gdx.app.getInput().isKeyPressed(20)) {
			currentSpeedDecay += SPEED_DECREASE_BY_BRAKES_RATE;
		}

		if (currentSpeedLevel < 0) {
			currentSpeedLevel = 0;
		}
		if (currentSpeedLevel > MAX_SPEED_LEVEL) {
			currentSpeedLevel = MAX_SPEED_LEVEL;
		}

		return shipSpeedLevels[currentSpeedLevel];
	}

	private int calculateConstantSpeed() {
		return 4;
	}


	private void drawOrigin() {
		screenShapeRenderer.setProjectionMatrix(camera.combined);
		screenShapeRenderer.begin();

		screenShapeRenderer.setColor(Color.RED); // x
		screenShapeRenderer.line(0, 0, 0, 100, 0, 0);

		screenShapeRenderer.setColor(Color.YELLOW); // y
		screenShapeRenderer.line(0, 0, 0, 0, 100, 0);
		screenShapeRenderer.end();
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



	@Override
	public boolean keyDown(int keycode) {
		if (keycode == 61) {
			freeCameraInput.setEnabled(!freeCameraInput.isEnabled());
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
