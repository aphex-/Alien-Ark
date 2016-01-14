package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.BufferedParticleBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
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
import com.nukethemoon.libgdxjam.screens.planet.animations.ArtifactCollectAnimation;
import com.nukethemoon.libgdxjam.screens.planet.animations.EnterPlanetAnimation;
import com.nukethemoon.libgdxjam.screens.planet.animations.ExitPlanetAnimation;
import com.nukethemoon.libgdxjam.screens.planet.animations.ScanAnimation;
import com.nukethemoon.libgdxjam.screens.planet.devtools.DevelopmentPlacementRenderer;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;
import com.nukethemoon.libgdxjam.screens.planet.devtools.windows.DevelopmentWindow;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Collectible;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.RocketListener;
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


	private final FirstPersonCameraController firstPersonCameraController;
	private DevelopmentPlacementRenderer placementRenderer;
	private ModelBatch modelBatch;
	private Environment environment;

	private PerspectiveCamera camera;

	private ParticleSystem particleSystem;
	private BufferedParticleBatch particleSpriteBatch;
	private ParticleEffect effectPortal;
	private ParticleEffect effectThrust;
	private ParticleEffect effectExplosion;

	private Rocket rocket;

	private ScanAnimation scanAnimation;

	private final ShapeRenderer shapeRenderer;

	private final InputMultiplexer multiplexer;
	private final Skin uiSkin;
	private final int planetIndex;
	private ControllerPlanet planetController;
	private ControllerPhysic physicsController;

	private Stage stage;

	private final ShipProgressBar shieldProgressBar;
	private final ShipProgressBar fuelProgressBar;

	private boolean gameOver = false;
	private boolean pause = false;
	private boolean renderEnabled = true;
	private boolean physicEnabled = false;
	private boolean rocketEnabled = false;
	private boolean debugCameraEnabled = false;

	public static Gson gson;
	private AssetManager assetManager;

	private Vector3 tmpVector = new Vector3();

	private final MiniMap miniMap;

	private static String[] KNOWN_PLANETS = new String[] {
		"planet01",
		"planet02",
		"planet03",
		"planet04",
		"planet05"
	};

	private Ani ani;
	private DevelopmentWindow developmentWindow;
	private MenuButton menuButton;



	public PlanetScreen(Skin pUISkin, InputMultiplexer pMultiplexer, int pPlanetIndex) {
		pPlanetIndex = pPlanetIndex % KNOWN_PLANETS.length;

		stage = new Stage(new ScreenViewport());

		camera = new PerspectiveCamera(App.config.FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 1f;
		camera.far = 30000f;

		ani = new Ani();
		uiSkin = pUISkin;
		planetIndex = pPlanetIndex;
		multiplexer = pMultiplexer;

		rocket = new Rocket();
		rocket.setThirdPersonCam(camera);
		rocket.setListener(this);
		gson = new GsonBuilder().setPrettyPrinting().create();

		final String vertexShaderText = Gdx.files.internal("shaders/default.vertex.glsl").readString();
		final String fragmentShaderText = Gdx.files.internal("shaders/default.fragment.glsl").readString();


		modelBatch = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected Shader createShader (Renderable renderable) {
				DefaultShader.Config config = new DefaultShader.Config(vertexShaderText, fragmentShaderText);
				DefaultShader shader = new RocketShadowShader(renderable, config);
				return shader;
			}
		});

		environment = new Environment();

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);



		FileHandle sceneConfigFile = Gdx.files.internal("entities/planets/" + KNOWN_PLANETS[planetIndex] + "/sceneConfig.json");
		PlanetConfig planetConfig = gson.fromJson(sceneConfigFile.reader(), PlanetConfig.class);
		planetConfig.deserialize();

		physicsController = new ControllerPhysic(planetConfig.gravity, this);
		planetController = new ControllerPlanet(KNOWN_PLANETS[planetIndex], planetConfig, physicsController, ani);
		physicsController.addRigidBody(
				rocket.rigidBodyList.get(0),
				com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes.ROCKET);



		miniMap = new MiniMap(rocket, planetController);

		if (App.config.debugMode) {
			placementRenderer = new DevelopmentPlacementRenderer();
		}

		onReloadScene(planetConfig);
		initParticles();
		initStage(planetConfig);
		multiplexer.addProcessor(this);

		shieldProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.SHIELD);
		stage.addActor(shieldProgressBar);
		shieldProgressBar.updateFromShipProperties();
		fuelProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.FUEL);
		stage.addActor(fuelProgressBar);
		fuelProgressBar.updateFromShipProperties();

		App.TUTORIAL_CONTROLLER.register(stage, ani);
		App.TUTORIAL_CONTROLLER.nextStepFor(this.getClass());

		planetController.updateRequestCenter(new Vector3(0, 0, 0));

		EnterPlanetAnimation enterPlanetAnimation = new EnterPlanetAnimation(camera, rocket, new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation baseAnimation) {
				physicEnabled = true;
				rocketEnabled = true;
			}
		});


		firstPersonCameraController = new FirstPersonCameraController(camera);
		multiplexer.addProcessor(firstPersonCameraController);

		ani.add(enterPlanetAnimation);

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
		assetManager.load("particles/planet_portal.pfx", ParticleEffect.class, loadParam);
		assetManager.finishLoading();

		effectThrust = 		((ParticleEffect) assetManager.get("particles/rocket_thruster.pfx")).copy();
		effectExplosion = 	((ParticleEffect) assetManager.get("particles/rocket_explosion.pfx")).copy();
		effectPortal = 		((ParticleEffect) assetManager.get("particles/planet_portal.pfx")).copy();
		effectThrust.init();
		effectExplosion.init();
		effectThrust.start();

		effectPortal.init();
		effectPortal.start();
		effectPortal.translate(new Vector3(-30, 0, 135));
		effectPortal.rotate(Vector3.Z, 90);
		effectPortal.rotate(Vector3.X, 30);
		effectPortal.scale(2f, 2f, 2f);

		particleSystem.add(effectThrust);
		particleSystem.add(effectPortal);
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

	private void initStage(final PlanetConfig planetConfig) {
		multiplexer.addProcessor(stage);

		if (App.config.debugMode) {
			developmentWindow = new DevelopmentWindow(uiSkin, stage, planetConfig, this, this, placementRenderer, planetController);
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
		multiplexer.removeProcessor(firstPersonCameraController);
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

		if (rocketEnabled) {
			planetController.updateNearestArtifact(rocket.getPosition().x, rocket.getPosition().y);
			App.TUTORIAL_CONTROLLER.applyNearestArtifact(planetController.getNearestArtifactPosition(), rocket.getPosition());
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
		}

		if (debugCameraEnabled) {
			firstPersonCameraController.update(delta * 10);
		}
		camera.update();

		if (!pause && rocketEnabled && !debugCameraEnabled) {
			//rocket.thrust();
			rocket.update();
		}

		modelBatch.begin(camera);
		rocket.drawModel(modelBatch, environment, effectThrust, effectExplosion);
		planetController.render(modelBatch, environment, false, rocket.getPosition());
		if (App.config.debugMode) {
			placementRenderer.render(modelBatch);
		}
		modelBatch.end();

		particleSystem.begin();
		particleSystem.draw();
		particleSystem.end();

		modelBatch.render(particleSystem);
		drawOrigin();


		if (!pause) {
			particleSystem.update();
			ani.update();
			if (!gameOver && physicEnabled) {
				if (!debugCameraEnabled) {
					physicsController.stepSimulation(delta);
				}
				physicsController.debugRender(camera);
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
		if (keycode == Input.Keys.TAB) {
			if (App.config.debugMode) {
				debugCameraEnabled = !debugCameraEnabled;
			}
		}
		if (keycode == Input.Keys.P) {
			pause = !pause;
		}
		if (keycode == Input.Keys.SPACE) {
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
		if (App.config.debugMode) {
			physicsController.calculateCameraPickIntersection(camera, screenX, screenY, tmpVector);
			placementRenderer.setCursorPosition(tmpVector);

		}
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
			planetController.updateRequestCenter(rocket.getPosition());
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

	@Override
	public void onRocketEntersPortal() {
		physicEnabled = false;
		rocketEnabled = false;
		ExitPlanetAnimation exitPlanetAnimation = new ExitPlanetAnimation(camera, rocket, new AnimationFinishedListener() {
			@Override
			public void onAnimationFinished(BaseAnimation baseAnimation) {
				leavePlanet();
			}
		});
		ani.add(exitPlanetAnimation);
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

	public void devJumpTo(float graphicX, float graphicY) {
		camera.position.set(graphicX - 50, graphicY, 60);
		camera.lookAt(graphicX, graphicY, 0);
		camera.up.set(Vector3.Z);
		planetController.updateRequestCenter(new Vector3(graphicX, graphicY, 0f));
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
		effectPortal.dispose();
		if (App.config.debugMode) {
			placementRenderer.dispose();
		}
	}

}
