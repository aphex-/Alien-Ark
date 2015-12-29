package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.input.FreeCameraInput;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.DevelopmentWindow;

public class PlanetScreen implements Screen, InputProcessor, ReloadSceneListener {


	private final ModelBatch modelBatch;
	private final Environment environment;
	private final PerspectiveCamera camera;

	private Rocket rocket;

	private final Vector3 shipPosition = new Vector3(0, 0, 20);
	private final ShapeRenderer screenShapeRenderer;
	private final InputMultiplexer multiplexer;
	private final Skin uiSkin;
	private final int worldIndex;

	private final Gson gson;

	private float shipRotationZ = 0;

	private int [] shipSpeedLevels = new int []{0, 2, 4, 8, 12, 20};
	private final int MAX_SPEED_LEVEL = shipSpeedLevels.length - 1;
	private static final float SPEED_DECREASE_BY_DECAY_RATE = 0.02f;
	private static final float SPEED_DECREASE_BY_BRAKES_RATE = 0.1f;

	private static final int CAMERA_Z_OFFSET = 10;

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

		rocket = new Rocket();
		gson = new GsonBuilder().setPrettyPrinting().create();

		modelBatch = new ModelBatch();
		environment = new Environment();

		screenShapeRenderer = new ShapeRenderer();
		screenShapeRenderer.setAutoShapeType(true);

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.01f;
		camera.far = 30000f;

		world = new WorldController(worldIndex);


		multiplexer.addProcessor(this);
		freeCameraInput = new FreeCameraInput(camera);
		freeCameraInput.setEnabled(false);
		multiplexer.addProcessor(freeCameraInput);

		FileHandle sceneConfigFile = Gdx.files.internal("entities/planets/planet01/sceneConfig.json");
		PlanetConfig planetConfig = gson.fromJson(sceneConfigFile.reader(), PlanetConfig.class);

		/*String str = gson.toJson(planetConfig);
		FileHandle fileHandle = new FileHandle("entities/planets/planet01/sceneConfig.json");
		fileHandle.writeString(str, false);*/

		onReloadScene(planetConfig);

		initStage(planetConfig);
	}

	@Override
	public void onReloadScene(PlanetConfig planetConfig) {
		environment.clear();
		for (ColorAttribute cAttribute : planetConfig.environmentColorAttributes) {
			environment.set(cAttribute);
		}
		for (DirectionalLight dLight : planetConfig.environmentDirectionalLights) {
			environment.add(dLight);
		}
	}



	private void initStage(PlanetConfig planetConfig) {
		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);


		if (Config.DEBUG) {
			final DevelopmentWindow developmentWindow = new DevelopmentWindow(uiSkin, stage, planetConfig, this);
			developmentWindow.setVisible(false);
			stage.addActor(developmentWindow);

			TextButton devButton = new TextButton("dev", uiSkin);
			devButton.setPosition(10, Gdx.graphics.getHeight() - devButton.getHeight() - 10);
			stage.addActor(devButton);

			devButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					developmentWindow.setVisible(true);
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

		world.updateRequestCenter(rocket.getPosition().x, rocket.getPosition().y);

		if (!freeCameraInput.isEnabled()) {
			rocket.applyThirdPerson(camera);

		} else {
			freeCameraInput.update(delta);
		}
		camera.update();

		if (Gdx.app.getInput().isKeyPressed(21)) {
			rocket.rotateLeft();

		}

		if (Gdx.app.getInput().isKeyPressed(22)) {
			rocket.rotateRight();
		}

		if (Gdx.app.getInput().isKeyPressed(19)) {
			rocket.rotateDown();
		}

		if (Gdx.app.getInput().isKeyPressed(20)) {
			rocket.rotateUp();
		}

		rocket.thrust();
		rocket.update();

		modelBatch.begin(camera);
		modelBatch.render(rocket.getModelInstance(), environment);
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
		if (amount > 0) {
			rocket.increaseThirdPersonOffsetY();
		} else {
			rocket.reduceThirdPersonOffsetY();
		}

		return false;
	}


}
