package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.BufferedParticleBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.input.FreeCameraInput;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.DevelopmentWindow;

public class PlanetScreen implements Screen, InputProcessor, ReloadSceneListener {


	private ModelInstance environmentSphere;
	private ModelBatch modelBatch;
	private Environment environment;
	private PerspectiveCamera camera;
	private ParticleSystem particleSystem;
	private BufferedParticleBatch particleSpriteBatch;

	private Rocket rocket;

	private final ShapeRenderer shapeRenderer;
	private final InputMultiplexer multiplexer;
	private final Skin uiSkin;
	private final int worldIndex;

	private WorldController world;
	private Stage stage;

	private final FreeCameraInput freeCameraInput;
	private ParticleEffect effect;

	private boolean pause = false;
	private boolean renderEnabled = true;

	public static Gson gson;
	private AssetManager assetManager;
	private Model sphereModel;

	public PlanetScreen(Skin puiSkin, InputMultiplexer pMultiplexer, int pWorldIndex) {
		uiSkin = puiSkin;
		worldIndex = pWorldIndex;
		multiplexer = pMultiplexer;

		rocket = new Rocket();
		gson = new GsonBuilder().setPrettyPrinting().create();


		modelBatch = new ModelBatch();
		environment = new Environment();

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.01f;
		camera.far = 30000f;

		FileHandle sceneConfigFile = Gdx.files.internal("entities/planets/planet01/sceneConfig.json");
		PlanetConfig planetConfig = gson.fromJson(sceneConfigFile.reader(), PlanetConfig.class);
		planetConfig.deserialize();

		world = new WorldController(worldIndex, planetConfig);

		multiplexer.addProcessor(this);
		freeCameraInput = new FreeCameraInput(camera);
		freeCameraInput.setEnabled(false);
		multiplexer.addProcessor(freeCameraInput);


		loadSphere(planetConfig.id);
		onReloadScene(planetConfig);
		initParticles();
		initStage(planetConfig);
	}

	private void initParticles() {
		// particles
		particleSystem = ParticleSystem.get();
		particleSpriteBatch = new BillboardParticleBatch();

		particleSpriteBatch.setCamera(camera);
		particleSystem.add(particleSpriteBatch);

		assetManager = new AssetManager();
		ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
		ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
		assetManager.setLoader(ParticleEffect.class, loader);
		assetManager.load("particles/rocket_thruster.pfx", ParticleEffect.class, loadParam);
		assetManager.finishLoading();

		ParticleEffect originalEffect = assetManager.get("particles/rocket_thruster.pfx");
		// we cannot use the originalEffect, we must make a copy each time we create new particle effect
		effect = originalEffect.copy();
		effect.init();
		effect.start();  // optional: particle will begin playing immediately
		particleSystem.add(effect);
		rocket.setParticleEffect(effect);
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

	private void loadSphere(String planetId) {
		ModelLoader loader = new ObjLoader();
		sphereModel = loader.loadModel(Gdx.files.internal("models/sphere01.obj"),
				new SphereTextureProvider(planetId));
		environmentSphere = new ModelInstance(sphereModel);
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

		final TextButton leaveButton = new TextButton("Leave Planet", uiSkin);
		leaveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				leavePlanet();
			}
		});
		leaveButton.setPosition(10, 10);
		stage.addActor(leaveButton);
	}

	private void leavePlanet() {
		renderEnabled = false;
		dispose();
		App.openSolarScreen();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if (!renderEnabled) {
			return;
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (!pause) {
			world.updateRequestCenter(rocket.getPosition().x, rocket.getPosition().y);
		}

		if (!freeCameraInput.isEnabled()) {

			if (!pause) {
				rocket.applyThirdPerson(camera);
			}


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

		if (!pause) {
			rocket.thrust();
			rocket.update();
		}

		modelBatch.begin(camera);
		rocket.drawModel(modelBatch, environment);
		world.render(modelBatch, environment);

		environmentSphere.transform.idt();
		environmentSphere.transform.setToTranslation(rocket.getPosition().x, rocket.getPosition().y, 0);
		environmentSphere.transform.scl(1000);

		modelBatch.render(environmentSphere);
		modelBatch.end();

		if (!pause) {
			particleSystem.update();
		}

		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();
		modelBatch.render(particleSystem);

		drawOrigin();

		if (stage != null) {
			stage.act(delta);
			stage.draw();
		}

	}

	private void drawOrigin() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();

		shapeRenderer.setColor(Color.RED); // x
		shapeRenderer.line(0, 0, 0, 100, 0, 0);

		shapeRenderer.setColor(Color.YELLOW); // y
		shapeRenderer.line(0, 0, 0, 0, 100, 0);
		shapeRenderer.end();
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
		world.dispose();
		modelBatch.dispose();
		rocket.dispose();
		assetManager.dispose();
		sphereModel.dispose();
		shapeRenderer.dispose();
	}



	@Override
	public boolean keyDown(int keycode) {
		if (keycode == 61) {
			freeCameraInput.setEnabled(!freeCameraInput.isEnabled());
		}
		if (keycode == 44) {
			pause = !pause;
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
