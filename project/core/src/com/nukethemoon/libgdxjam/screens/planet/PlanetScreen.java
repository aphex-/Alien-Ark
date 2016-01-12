package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Balancing;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.input.FreeCameraInput;
import com.nukethemoon.libgdxjam.screens.planet.animations.ArtifactCollectAnimation;
import com.nukethemoon.libgdxjam.screens.planet.animations.ScanAnimation;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.DevelopmentWindow;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Collectible;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.RocketListener;
import com.nukethemoon.libgdxjam.screens.planet.helper.SphereTextureProvider;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;
import com.nukethemoon.libgdxjam.screens.planet.physics.ControllerPhysic;
import com.nukethemoon.libgdxjam.ui.GameOverTable;
import com.nukethemoon.libgdxjam.ui.MenuButton;
import com.nukethemoon.libgdxjam.ui.MenuTable;
import com.nukethemoon.libgdxjam.ui.ToastTable;
import com.nukethemoon.libgdxjam.ui.animation.FadeTableAnimation;
import com.nukethemoon.libgdxjam.ui.hud.ShipProgressBar;
import com.nukethemoon.tools.ani.Ani;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class PlanetScreen implements Screen, InputProcessor, ReloadSceneListener, RocketListener,
		ControllerPhysic.PhysicsListener  {

	private ModelInstance environmentSphere;
	private ModelBatch modelBatch;
	private Environment environment;

	private PerspectiveCamera camera;

	private ParticleSystem particleSystem;
	private BufferedParticleBatch particleSpriteBatch;

	private Rocket rocket;

	private ScanAnimation scanAnimation;

	private final ShapeRenderer shapeRenderer;

	private final InputMultiplexer multiplexer;
	private final Skin uiSkin;
	private final int planetIndex;

	private ControllerPlanet planetController;
	private ControllerPhysic physicsController;

	private Stage stage;

	private final FreeCameraInput freeCameraInput;

	private ParticleEffect effectThrust;
	private ParticleEffect effectExplosion;

	private final ShipProgressBar shieldProgressBar;
	private final ShipProgressBar fuelProgressBar;

	private boolean gameOver = false;
	private boolean pause = false;
	private boolean renderEnabled = true;

	public static Gson gson;
	private AssetManager assetManager;
	private Model sphereModel;

	private final MiniMap miniMap;

	private static String[] KNOWN_PLANETS = new String[] {
		"planet01",
		"planet02",
		"planet03"
	};

	private Ani ani;
	private DevelopmentWindow developmentWindow;
	private MenuButton menuButton;


	public PlanetScreen(Skin pUISkin, InputMultiplexer pMultiplexer, int pPlanetIndex) {
		pPlanetIndex = pPlanetIndex % KNOWN_PLANETS.length;
		stage = new Stage(new ScreenViewport());

		ani = new Ani();
		uiSkin = pUISkin;
		planetIndex = pPlanetIndex;
		multiplexer = pMultiplexer;

		rocket = new Rocket();
		rocket.setListener(this);
		gson = new GsonBuilder().setPrettyPrinting().create();

		modelBatch = new ModelBatch();
		environment = new Environment();

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 1f;
		camera.far = 30000f;

		FileHandle sceneConfigFile = Gdx.files.internal("entities/planets/" + KNOWN_PLANETS[planetIndex] + "/sceneConfig.json");
		PlanetConfig planetConfig = gson.fromJson(sceneConfigFile.reader(), PlanetConfig.class);
		planetConfig.deserialize();

		physicsController = new ControllerPhysic(planetConfig.gravity, this);
		planetController = new ControllerPlanet(KNOWN_PLANETS[planetIndex], planetConfig, physicsController, ani);
		physicsController.addRigidBody(
				rocket.rigidBodyList.get(0),
				com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes.ROCKET);

		multiplexer.addProcessor(this);
		freeCameraInput = new FreeCameraInput(camera);
		freeCameraInput.setEnabled(false);
		multiplexer.addProcessor(freeCameraInput);

		miniMap = new MiniMap(rocket, planetController);

		loadSphere(planetConfig.id);
		onReloadScene(planetConfig);
		initParticles();
		initStage(planetConfig);

		shieldProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.SHIELD);
		stage.addActor(shieldProgressBar);
		shieldProgressBar.updateFromShipProperties();
		fuelProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.FUEL);
		stage.addActor(fuelProgressBar);
		fuelProgressBar.updateFromShipProperties();

		App.TUTORIAL_CONTROLLER.register(stage, ani);
		App.TUTORIAL_CONTROLLER.nextStepFor(this.getClass());
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
		assetManager.load("particles/rocket_explosion.pfx", ParticleEffect.class, loadParam);
		assetManager.finishLoading();

		effectThrust = ((ParticleEffect) assetManager.get("particles/rocket_thruster.pfx")).copy();
		effectExplosion = ((ParticleEffect) assetManager.get("particles/rocket_explosion.pfx")).copy();
		effectThrust.init();
		effectExplosion.init();
		effectThrust.start();

		particleSystem.add(effectThrust);
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

	private void initStage(final PlanetConfig planetConfig) {
		multiplexer.addProcessor(stage);

		if (App.config.debugMode) {
			developmentWindow = new DevelopmentWindow(uiSkin, stage, planetConfig, this, this);
			developmentWindow.setVisible(false);
			stage.addActor(developmentWindow);

			final TextButton devButton = new TextButton("dev", uiSkin);

			ClickListener clickListener = new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					developmentWindow.pack();
					developmentWindow.setVisible(true);
				}
			};
			devButton.addListener(clickListener);
			devButton.setPosition(50, Gdx.graphics.getHeight() - devButton.getHeight() - 10);
			stage.addActor(devButton);
		}

		menuButton = new MenuButton(uiSkin);
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onPauseClicked();
			}
		});
		stage.addActor(menuButton);
	}

	private void onPauseClicked() {
		if (!pause) {
			pause = true;
			menuButton.openMenu(stage, new MenuTable.CloseListener() {
				@Override
				public void onClose() {
					pause = false;
				}
			});
		}
	}

	public void leavePlanet() {
		App.TUTORIAL_CONTROLLER.onLeavePlanet();
		renderEnabled = false;
		multiplexer.removeProcessor(stage);
		multiplexer.removeProcessor(this);
		multiplexer.removeProcessor(freeCameraInput);
		dispose();
		App.saveProgress();
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

		planetController.updateNearestArtifact(rocket.getPosition().x, rocket.getPosition().y);
		App.TUTORIAL_CONTROLLER.applyNearestArtifact(planetController.getNearestArtifactPosition(), rocket.getPosition());

		if (!freeCameraInput.isEnabled()) {
			if (!pause) {
				rocket.applyThirdPerson(camera);
			}
		} else {
			freeCameraInput.update(delta);
		}
		camera.update();

		if (Gdx.app.getInput().isKeyPressed(Input.Keys.LEFT) || Gdx.app.getInput().isKeyPressed(Input.Keys.A)) {
			rocket.rotateLeft();
		}
		if (Gdx.app.getInput().isKeyPressed(Input.Keys.RIGHT) || Gdx.app.getInput().isKeyPressed(Input.Keys.D)) {
			rocket.rotateRight();
		}
		if (Gdx.app.getInput().isKeyPressed(Input.Keys.UP) || Gdx.app.getInput().isKeyPressed(Input.Keys.W)) {
			rocket.rotateUp();
		}
		if (Gdx.app.getInput().isKeyPressed(Input.Keys.DOWN) || Gdx.app.getInput().isKeyPressed(Input.Keys.S)) {
			rocket.rotateDown();
		}

		if (!pause) {
			//rocket.thrust();
			rocket.update();
		}

		modelBatch.begin(camera);
		rocket.drawModel(modelBatch, environment, effectThrust, effectExplosion);
		planetController.render(modelBatch, environment, false);
		environmentSphere.transform.idt();
		environmentSphere.transform.setToTranslation(rocket.getPosition().x, rocket.getPosition().y, 0);
		environmentSphere.transform.scl(1000);

		modelBatch.render(environmentSphere);
		modelBatch.end();

		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();

		modelBatch.render(particleSystem);
		drawOrigin();
		physicsController.debugRender(camera);

		if (!pause) {
			particleSystem.update();
			ani.update();
			if (!gameOver) {
				physicsController.stepSimulation(delta);
			}
		}
		if (stage != null) {
			stage.act(delta);
			stage.draw();
		}
		miniMap.drawMiniMap();
	}


	private void showToast(String text) {
		final ToastTable t = new ToastTable(uiSkin);
		t.setText(text);
		stage.addActor(t);
		ToastTable.ShowToastAnimation animation = new ToastTable.ShowToastAnimation(t, new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation baseAnimation) {
				t.remove();
			}
		});
		ani.add(animation);
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
	public boolean keyDown(int keycode) {
		if (keycode == 61) {
			freeCameraInput.setEnabled(!freeCameraInput.isEnabled());
		}
		if (keycode == 44) {
			onPauseClicked();
		}
		if (keycode == 62) {
			rocket.toggleThrust();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		App.TUTORIAL_CONTROLLER.onKeyTyped(keycode);
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

	private void onGameOver() {
		gameOver = true;
		GameOverTable gameOverTable = new GameOverTable(uiSkin, new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				renderEnabled = false;
				dispose();
				App.onGameOver();
			}
		});
		gameOverTable.setColor(1, 1, 1, 0);
		stage.addActor(gameOverTable);
		ani.add(2500, new FadeTableAnimation(gameOverTable));
	}

	@Override
	public void onRocketLanded() {
		showToast("Rocked landed");
		App.audioController.playSound("hit_deep.mp3");
	}

	@Override
	public void onRocketLaunched() {

	}

	@Override
	public void onRocketDisabledThrust() {
		fuelProgressBar.updateFromShipProperties();

		showToast("Landing procedure...");
		particleSystem.remove(effectThrust);
	}

	@Override
	public void onRocketEnabledThrust() {
		particleSystem.add(effectThrust);
	}

	@Override
	public void onRocketDamage() {
		App.audioController.playSound("energy_shield.mp3");
		shieldProgressBar.updateFromShipProperties();
	}

	@Override
	public void onRocketFuelConsumed() {
		fuelProgressBar.updateFromShipProperties();
	}

	@Override
	public void onRocketExploded() {
		App.audioController.playSound("explosion.mp3");
		particleSystem.remove(effectThrust);
		effectExplosion.start();
		particleSystem.add(effectExplosion);
		onGameOver();
	}

	@Override
	public void onRocketFuelBonus() {
		showToast("Fuel +" + Balancing.FUEL_BONUS);
		App.audioController.playSound("bonus.mp3");
		fuelProgressBar.updateFromShipProperties();
	}

	@Override
	public void onRocketShieldBonus() {
		showToast("Shield +" + Balancing.SHIELD_BONUS);
		App.audioController.playSound("bonus.mp3");
		shieldProgressBar.updateFromShipProperties();
	}


	@Override
	public void onRocketChangedTilePosition() {
		if (!pause) {
			planetController.updateRequestCenter(rocket.getPosition(), rocket.getDirection());
		}
	}

	@Override
	public void onRocketScanStart() {
		showToast("Scan started!");
		rocket.setTractorBeamVisibility(true);
		scanAnimation = new ScanAnimation(rocket.getTractorBeamModelInstance(),
				SpaceShipProperties.properties.getScanRadius(),
				new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation baseAnimation) {
				onScanAnimationFinished(baseAnimation.getRemainingLoopCount() != 0);
			}
		});
		ani.add(scanAnimation);
	}

	private void onScanAnimationFinished(boolean wasCanceled) {
		if (!wasCanceled) {
			rocket.setTractorBeamVisibility(false);
			final ArtifactObject artifactObject = planetController.tryCollect(rocket.getPosition(),
					SpaceShipProperties.properties.getScanRadius());
			if (artifactObject == null) {
				showToast("Not close enough to an Artifact");
			} else {
				showToast("Artifact collected!");
				App.audioController.playSound("bonus_stream.mp3");
				ArtifactCollectAnimation artifactCollectAnimation = new ArtifactCollectAnimation(artifactObject,
						rocket.getPosition(), new AnimationFinishedListener() {
					@Override
					public void onAnimationFinished(BaseAnimation baseAnimation) {
						onCollectAnimationFinished(artifactObject);
					}
				});
				ani.add(artifactCollectAnimation);
			}
		}
	}

	private void onCollectAnimationFinished(ArtifactObject artifactObject) {
		planetController.collectArtifact(artifactObject);
		App.TUTORIAL_CONTROLLER.onArtifactCollected();
	}

	@Override
	public void onRocketScanEnd() {
		showToast("Scan canceled!");
		rocket.setTractorBeamVisibility(false);
		ani.forceStop(scanAnimation);
	}

	// === physic events ===

	@Override
	public void onRocketCollided(CollisionTypes type, btCollisionObject collisionObject) {
		rocket.handleCollision(type);

		if (type == CollisionTypes.FUEL || type == CollisionTypes.SHIELD) {
			Collectible collectible = planetController.getCollectible(collisionObject);
			planetController.removeCollectible(collectible);
			planetController.getCollectedItemCache().registerCollected(
					collectible.getPlanetPartPosition().x,
					collectible.getPlanetPartPosition().y,
					collectible.getType());
		}
	}

	@Override
	public void onInternalTick() {
		rocket.handlePhysicTick();
	}

	@Override
	public void dispose() {
		particleSystem.removeAll();
		particleSystem.getBatches().clear();
		planetController.dispose();
		modelBatch.dispose();
		rocket.dispose();
		assetManager.dispose();
		//sphereModel.dispose();
		//shapeRenderer.dispose();
		effectThrust.dispose();
		effectExplosion.dispose();
	}

}
