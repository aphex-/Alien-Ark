package com.nukethemoon.libgdxjam.screens.solar;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.SolarSystem;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;
import com.nukethemoon.libgdxjam.screens.planet.physics.ControllerPhysic;
import com.nukethemoon.libgdxjam.ui.EnterOrbitTable;
import com.nukethemoon.libgdxjam.ui.GameOverTable;
import com.nukethemoon.libgdxjam.ui.MenuButton;
import com.nukethemoon.libgdxjam.ui.MenuTable;
import com.nukethemoon.libgdxjam.ui.hud.ShipProgressBar;
import com.nukethemoon.tools.ani.Ani;

import java.util.ArrayList;
import java.util.List;


public class SolarScreen implements Screen, ControllerPhysic.PhysicsListener, InputProcessor {

	private Box2DDebugRenderer debugRenderer;
	private ShapeRenderer debugShapeRenderer;

	private static final int RAYS_NUM = 333;
	private static final int SUN_COLLISION = 999;
	private final World world;
	private final InputMultiplexer multiplexer;
	private final Ani ani;
	private final Skin uiSkin;
	private final ParticleEffect thrustEffect;

	private Vector2 tmpVector = new Vector2();
//	private StarsBackground bg;

	private EnterOrbitTable enterOrbitTable = null;

	private Vector2 shipPosition;
	private final RayHandler rayHandler;
	private OrthographicCamera camera;

	//0 - 359
	private float adjustRotation = 0;

	//starts at 90 - ark facing up
	private float currentRotation = 90;

	private float[] shipSpeedLevels = new float[]{0, 0.5f, 1f, 1.5f, 2.0f, 3.0f};
	private final int MAX_SPEED_LEVEL = shipSpeedLevels.length - 1;
	private static final float SPEED_DECREASE_BY_DECAY_RATE = 0.02f;
	private static final float SPEED_DECREASE_BY_BRAKES_RATE = 0.1f;
	private float currentSpeedDecay = 0;
	private int currentSpeedLevel = 0;


	public static final int INITIAL_ARK_POSITION_Y = -300;
	public static final int INITIAL_ARK_POSITION_X = -525;

	private SpriteBatch batch;

	private Sprite arkSprite;
	private Sprite exhaustSprite;

	private final Sprite[] planetSprites = new Sprite[SolarSystem.NUMBER_OF_PLANETS];

	private int screenHeight;
	private int screenWidth;

	float arkHeight;
	float arkWidth;

	float sunRotation = 0;
	Matrix4 starsRotationMatrix = new Matrix4();
	float starsCurrentRotation = 0;

	private Stage stage;
	private Sprite sunSprite;
	private List<Body> bodies;
	private PointLight pointLight;
	private float counter = 0f;
	private Sprite rocketShield;

	private List<PlanetGraphic> planetGraphics = new ArrayList<PlanetGraphic>();

	private ShipProgressBar fuelProgressBar;
	private ShipProgressBar shieldProgressBar;
	private TiledDrawable bgTile;
	private boolean isThrusting;
	private boolean gameOver = false;
	private float shieldAudioCounter = 0;

	private Vector2 corner01 = new Vector2();
	private Vector2 corner02 = new Vector2();
	private Vector2 corner03 = new Vector2();
	private Vector2 corner04 = new Vector2();

	private long lasTimeDamage = -1;

	public SolarScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		Log.d(getClass(), "create SolarScreen");
		this.multiplexer = multiplexer;
		multiplexer.addProcessor(this);
		this.uiSkin = uiSkin;
		batch = new SpriteBatch();
		arkSprite = new Sprite(App.TEXTURES.findRegion("rocket"));
		rocketShield = new Sprite(App.TEXTURES.findRegion("rocket_shield"));
		exhaustSprite = new Sprite(App.TEXTURES.findRegion("exhaust_placeholder"));
		ani = new Ani();

		fuelProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.FUEL);
		fuelProgressBar.updateFromShipProperties();
		shieldProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.SHIELD);
		shieldProgressBar.updateFromShipProperties();


		world = new World(new Vector2(0, 0), true);
		shipPosition = SpaceShipProperties.properties.currentSolarPosition;

		//new DirectionalLight(rayHandler, RAYS_NUM, new Color(1, 0.6f, 0.9f, 0.6f), 45);
		setupSpaceship();
		setupArkButton(uiSkin, multiplexer);
		setupPlanets();

		rayHandler = createRayHandler(world);
		createPointLights();
		//updateShadowBodies(world);

		App.TUTORIAL_CONTROLLER.register(stage, ani);
		App.TUTORIAL_CONTROLLER.nextStepFor(this.getClass());

		stage.addActor(fuelProgressBar);
		stage.addActor(shieldProgressBar);
		Log.d(getClass(), "end of setup");

		thrustEffect = new ParticleEffect();
		thrustEffect.load(Gdx.files.internal("particles/2D/rocket_thruster.pae"), App.TEXTURES);

		int[] distance = new int[] {
				500, 700, 880, 1020, 1200, 1350, 1500, 1650, 1800, 1950};

		planetGraphics.add(new PlanetGraphic("planet07", distance[0], 0.1f, 	0.005f));
		planetGraphics.add(new PlanetGraphic("planet03", distance[1], 0.2f,		-0.004f));
		planetGraphics.add(new PlanetGraphic("planet01", distance[2], -0.3f, 	-0.002f));
		planetGraphics.add(new PlanetGraphic("planet10", distance[3], -0.1f, 	0.001f));
		planetGraphics.add(new PlanetGraphic("planet09", distance[4], -0.4f, 	-0.001f));
		planetGraphics.add(new PlanetGraphic("planet05", distance[5], 0.1f, 	0.0009f));
		planetGraphics.add(new PlanetGraphic("planet06", distance[6], 0.1f, 	0.0008f));
		planetGraphics.add(new PlanetGraphic("planet02", distance[7], 0.13f, 	-0.0005f));
		planetGraphics.add(new PlanetGraphic("planet08", distance[8], 0.2f, 	0.0006f));
		planetGraphics.add(new PlanetGraphic("planet04", distance[9], 0.3f, 	0.0005f));


		for (PlanetGraphic p : planetGraphics) {
			p.addToWorld(world);
		}

		if (Config.DEBUG_RENDERER) {
			debugRenderer = new Box2DDebugRenderer();
			debugShapeRenderer = new ShapeRenderer();
		}
	}

	private void createPointLights() {
		pointLight = new PointLight(rayHandler, RAYS_NUM, new Color(1f, 1f, 0f, 0.9f), 4000, 0, 0);

	}

	private void setupSpaceship() {
		arkSprite.setPosition(shipPosition.x, shipPosition.y);
		arkWidth = arkSprite.getWidth();
		arkHeight = arkSprite.getHeight();
		exhaustSprite.setPosition(shipPosition.x, shipPosition.y);
	}

	private void setupPlanets() {

		planetSprites[0] = new Sprite(App.TEXTURES.findRegion("planet01"));
		planetSprites[1] = new Sprite(App.TEXTURES.findRegion("planet02"));
		planetSprites[2] = new Sprite(App.TEXTURES.findRegion("planet03"));
		planetSprites[3] = new Sprite(App.TEXTURES.findRegion("planet04"));
		planetSprites[4] = new Sprite(App.TEXTURES.findRegion("planet05"));
		planetSprites[5] = new Sprite(App.TEXTURES.findRegion("planet06"));
		planetSprites[6] = new Sprite(App.TEXTURES.findRegion("planet07"));
		planetSprites[7] = new Sprite(App.TEXTURES.findRegion("planet08"));
		planetSprites[8] = new Sprite(App.TEXTURES.findRegion("planet09"));
		planetSprites[9] = new Sprite(App.TEXTURES.findRegion("planet10"));

		sunSprite = new Sprite(App.TEXTURES.findRegion("sun"));
		sunSprite.setPosition(-sunSprite.getWidth() / 2, -sunSprite.getHeight() / 2);

		//pointLight = new PointLight[SolarSystem.NUMBER_OF_PLANETS];
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {
			Vector2 position = App.solarSystem.getPlanetPosition(i);
			planetSprites[i].setPosition(position.x, position.y);
		}
		TextureAtlas.AtlasRegion stars = App.TEXTURES.findRegion("stars");
		bgTile = new TiledDrawable(stars);
	}

	private void setupArkButton(Skin uiSkin, InputMultiplexer multiplexer) {

		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);

		TextureRegionDrawable drawable = new TextureRegionDrawable(App.TEXTURES.findRegion("buttonControlCenter"));
		ImageButton arkScreenButton = new ImageButton(drawable);
		arkScreenButton.setPosition((Gdx.graphics.getWidth() / 2) - (arkScreenButton.getWidth() / 2),
				10);
		stage.addActor(arkScreenButton);
		arkScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				openArkScreen();
			}
		});

		final MenuButton menuButton = new MenuButton(uiSkin);
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				menuButton.openMenu(stage, new MenuTable.CloseListener() {
					@Override
					public void onClose() {

					}
				});
			}
		});

		stage.addActor(menuButton);
	}

	private RayHandler createRayHandler(World world) {
		bodies = new ArrayList<Body>();
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		RayHandler rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.1f);
		rayHandler.setCulling(true);
		rayHandler.pointAtLight(0, 0);
		rayHandler.setBlurNum(4);
		return rayHandler;
	}

	private void updateShadowBodies(World world) {
		for (Body body : bodies) {
			world.destroyBody(body);
		}
		bodies.clear();

		CircleShape shape = new CircleShape();
		for (int i = 0; i < planetSprites.length; i++) {
			Vector2 pos = App.solarSystem.getPlanetShadowPosition(i, (int) (-1 * planetSprites[i].getWidth()));

			shape.setRadius((int) planetSprites[i].getWidth() / 2);
			FixtureDef def = new FixtureDef();
			def.shape = shape;

			BodyDef body = new BodyDef();
			body.type = BodyDef.BodyType.DynamicBody;
			body.position.x = planetSprites[i].getX()/*pos.x*/ + planetSprites[i].getWidth() / 2f;
			body.position.y = planetSprites[i].getY() + planetSprites[i].getHeight() / 2f;

			Body box = world.createBody(body);
			box.createFixture(def);
			bodies.add(box);
		}
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(21 / 255f, 21 / 255f, 21 / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleArkMovementInput(delta);
		handleAppNavigation(delta);

		updateArkBounds();
//		camera.update();
//		batch.setProjectionMatrix(camera.combined);

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		rotatePlanets(delta);
		shieldProgressBar.updateFromShipProperties();
		fuelProgressBar.updateFromShipProperties();

		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();
		renderPlanets();
		camera.position.set(arkSprite.getX(), arkSprite.getY(), 0);
		camera.zoom = 2.0f;

		batch.begin();
		sunRotation -= 0.01;
		sunSprite.setRotation(sunRotation);
		sunSprite.draw(batch);
		drawThrust(delta);
		for (PlanetGraphic planet : planetGraphics) {
			planet.update(delta);
			planet.draw(batch);
		}

		batch.end();

		renderArc();

		stage.act(delta);
		stage.draw();
		debugRender();
		ani.update();

		if (isColliding(tmpVector.set(0, 0), sunSprite.getHeight() / 2)) {
			dealDamage();
		}
	}

	private void debugRender() {
		if (Config.DEBUG_RENDERER) {
			debugShapeRenderer.setProjectionMatrix(camera.combined);
			debugRenderer.render(world, camera.combined);
			debugShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			debugShapeRenderer.setColor(1, 0, 0, 1);
			debugShapeRenderer.line(0, 0, 50, 0);
			debugShapeRenderer.setColor(0, 0, 1, 1);
			debugShapeRenderer.line(0, 0, 0, 50);
			debugShapeRenderer.setColor(1, 1, 1, 1);
			debugShapeRenderer.line(corner01, corner02);
			debugShapeRenderer.line(corner02, corner03);
			debugShapeRenderer.line(corner03, corner04);
			debugShapeRenderer.line(corner04, corner01);
			debugShapeRenderer.end();

		}
	}

	private void updateArkBounds() {
		float w = arkSprite.getWidth() / 2;
		float h = arkSprite.getHeight() / 2;
		float x = arkSprite.getX();
		float y = arkSprite.getY();
		corner01.set(-w, -h).rotate(currentRotation - 90).add(x + w, y + h);
		corner02.set(-w, h).rotate(currentRotation - 90).add(x + w, y + h);
		corner03.set(w, h).rotate(currentRotation - 90).add(x + w, y + h);
		corner04.set(w, -h).rotate(currentRotation - 90).add(x + w, y + h);
	}

	private void drawThrust(float delta) {
		rotateParticle(thrustEffect, currentRotation);
		thrustEffect.update(delta);
		tmpVector.set(1, 0).rotate(currentRotation).scl(-arkSprite.getHeight() / 2);
		float x = arkSprite.getX() + (arkSprite.getWidth() / 2) + tmpVector.x;
		float y = arkSprite.getY() + (arkSprite.getHeight() / 2) + tmpVector.y;
		thrustEffect.setPosition(x, y);
		thrustEffect.draw(batch);
	}

	private void rotateParticle(ParticleEffect particleEffect, float angle) {
		for (int i = 0; i < particleEffect.getEmitters().size; i++) {
			particleEffect.getEmitters().get(i).getAngle().setLow(angle - 180);
			particleEffect.getEmitters().get(i).getAngle().setHigh(angle - 180);
		}
	}

	private void dealDamage() {
		if (System.currentTimeMillis() - lasTimeDamage > Rocket.MIN_DAMAGE_DELAY_MILLIS) {
			lasTimeDamage = System.currentTimeMillis();
			if ((SpaceShipProperties.properties.getCurrentInternalShield() - 10) > 1) {
				// no game over on this screen
				SpaceShipProperties.properties.addCurrentShield(-10);
			} else {
				SpaceShipProperties.properties.setCurrentInternalShield(1);
			}
			App.audioController.playSound("energy_shield.mp3");
		}
	}


	private void renderArc() {
		batch.begin();
		moveArcPosition();
		adjustRotationIfNeccessary();
		if (isThrusting) {
			exhaustSprite.draw(batch);
		}
		arkSprite.draw(batch);
		float shieldAlpha = Rocket.MIN_DAMAGE_DELAY_MILLIS / (System.currentTimeMillis() - lasTimeDamage);
		rocketShield.setAlpha(shieldAlpha);
		rocketShield.setPosition(arkSprite.getX() - 4, arkSprite.getY() - 5);
		rocketShield.setRotation(currentRotation - 90);
		rocketShield.draw(batch);

		batch.end();
	}

	private void adjustRotationIfNeccessary() {
		if (adjustRotation != 0) {
			currentRotation += adjustRotation;
			normalizeRotation();
			arkSprite.rotate(adjustRotation);
			exhaustSprite.rotate(adjustRotation);
			adjustRotation = 0;
		}
	}

	private void normalizeRotation() {
		if (currentRotation < 0) {
			currentRotation += 360;
		}
		currentRotation = currentRotation % 360;
	}



	private void renderPlanets() {
		starsCurrentRotation += 0.017f;
		starsRotationMatrix.setToRotation(0, 0, 1, starsCurrentRotation);

		batch.getProjectionMatrix().mul(starsRotationMatrix);
		batch.begin();
		bgTile.draw(batch, -3000, -3000, 6000, 6000);
		/*sunSprite.draw(batch);
		for (int i = 0; i < planetSprites.length; i++) {
			planetSprites[i].draw(batch);
		}*/
		batch.end();
		batch.getProjectionMatrix().mul(starsRotationMatrix.inv());
	}

	private void handleArkMovementInput(float delta) {
		adjustCurrentSpeed();

		if (Gdx.app.getInput().isKeyPressed(Input.Keys.RIGHT) || Gdx.app.getInput().isKeyPressed(Input.Keys.D)) {
			adjustRotation = adjustRotation - (50 * delta);
		} else if (Gdx.app.getInput().isKeyPressed(Input.Keys.LEFT) || Gdx.app.getInput().isKeyPressed(Input.Keys.A)) {
			adjustRotation = adjustRotation + (50 * delta);
		}
	}

	private void adjustCurrentSpeed() {
		currentSpeedDecay += SPEED_DECREASE_BY_DECAY_RATE;
		if (currentSpeedDecay > 1) {
			currentSpeedDecay = 0;
			currentSpeedLevel -= 1;
		}

		//if (Gdx.app.getInput().isKeyPressed(19) && !rocket.isOutOfFuel()) {
		if (Gdx.app.getInput().isKeyPressed(Input.Keys.UP) || Gdx.app.getInput().isKeyPressed(Input.Keys.W)) {
			currentSpeedLevel += 1;
			isThrusting = true;

		} else {
			isThrusting = false;
		}

		if (Gdx.app.getInput().isKeyPressed(Input.Keys.DOWN) || Gdx.app.getInput().isKeyPressed(Input.Keys.S)) {
			currentSpeedDecay += SPEED_DECREASE_BY_BRAKES_RATE;
		}

		if (currentSpeedLevel < 0) {
			currentSpeedLevel = 0;
		}
		if (currentSpeedLevel > MAX_SPEED_LEVEL) {
			currentSpeedLevel = MAX_SPEED_LEVEL;
		}
	}

	private void moveArcPosition() {
		double radians = Math.toRadians(currentRotation);
		float translateX = (float) Math.cos(radians) * (shipSpeedLevels[currentSpeedLevel]);
		float translateY = (float) Math.sin(radians) * (shipSpeedLevels[currentSpeedLevel]);

		arkSprite.translate(translateX, translateY);

		shipPosition.x = arkSprite.getX();
		shipPosition.y = arkSprite.getY();

		exhaustSprite.setPosition(shipPosition.x, shipPosition.y);
	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		pointLight.dispose();
	}

	@Override
	public void resize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;

		camera = new OrthographicCamera(screenWidth, screenHeight);
		camera.position.set(0, screenWidth / 2f, 0);
		camera.update();
	}

	@Override
	public void pause() {

	}

	private int determinePlanetCollision() {
		for (int i = 0; i < planetGraphics.size(); i++) {
			if (isColliding(planetGraphics.get(i).getPosition(), planetGraphics.get(i).getRadius())) {
				return i;
			}
		}
		return -1;
	}

	private boolean isColliding(Vector2 planetPosition, float planetRadius) {
		if (corner01.dst(planetPosition) < planetRadius) {
			return true;
		}
		if (corner02.dst(planetPosition) < planetRadius) {
			return true;
		}
		if (corner03.dst(planetPosition) < planetRadius) {
			return true;
		}
		if (corner04.dst(planetPosition) < planetRadius) {
			return true;
		}
		return Intersector.isPointInTriangle(planetPosition, corner01, corner02, corner03)
				|| Intersector.isPointInTriangle(planetPosition, corner03, corner04, corner01);
	}

	private boolean isArcSelected() {
		return false;
	}

	private void openPlanetScreen(int planetIndex) {
		multiplexer.removeProcessor(this);
		multiplexer.removeProcessor(stage);
		App.openPlanetScreen(planetIndex);
	}

	private void openArkScreen() {
		multiplexer.removeProcessor(this);
		multiplexer.removeProcessor(stage);
		App.openArkScreen();
	}

	private void handleAppNavigation(float delta) {
		if (SpaceShipProperties.properties.getCurrentInternalShield() <= 0) {
			explodeRocket();
		}
		final int planetIndex = determinePlanetCollision();
		if (planetIndex == SUN_COLLISION) {
			//dealDamageToRocket(delta);
		} else if (planetIndex != -1) {
			if (enterOrbitTable == null) {
				enterOrbitTable = new EnterOrbitTable(Styles.UI_SKIN, planetIndex);
				stage.addActor(enterOrbitTable);
				enterOrbitTable.setClickListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						openPlanetScreen(planetIndex);
					}
				});
			}
		} else {
			if (enterOrbitTable != null) {
				enterOrbitTable.remove();
				enterOrbitTable = null;
			}
		}

		if (isArcSelected()) {
			openArkScreen();
		}
	}

	private void explodeRocket() {
		if (!gameOver) {
			gameOver = true;
			App.audioController.playSound("explosion.mp3");
			onGameOver();
		}
	}

	private void onGameOver() {
		GameOverTable gameOverTable = new GameOverTable(uiSkin, new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				dispose();
				App.onGameOver();
			}
		});
		stage.clear();
		stage.addActor(gameOverTable);
	}

	private void dealDamageToRocket(float delta) {
		shieldAudioCounter += delta;
		if (shieldAudioCounter > 0.45 && !gameOver) {
			shieldAudioCounter = 0;
			App.audioController.playSound("energy_shield.mp3");
		}
		SpaceShipProperties.properties.addCurrentShield(-20);
	}

	private void rotatePlanets(float delta) {
		float limit = 0.1f;
		if (counter < limit) {
			counter += delta;
			return;
		} else {
			counter = 0;

		}
		App.solarSystem.rotate((Math.PI / 512), 0.5f);
//		updateShadowBodies(world);
		updatePlanets();
	}

	private void updatePlanets() {
		for (int i = 0; i < planetSprites.length; i++) {
			Vector2 position = App.solarSystem.getPlanetPosition(i);
			planetSprites[i].setPosition(position.x, position.y);
			planetSprites[i].rotate((float) (0.5f * Math.PI));
		}
	}

	@Override
	public void onRocketCollided(CollisionTypes type, btCollisionObject collisionObject) {

	}

	@Override
	public void onInternalTick() {

	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.ENTER && enterOrbitTable != null) {
			openPlanetScreen(enterOrbitTable.getPlanetIndex());
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
		if (amount < 0) {
			if (camera.zoom > 0.5) {
				camera.zoom -= 0.1;
			}
		} else {
			if (camera.zoom < 3) {
				camera.zoom += 0.1;
			}
		}
		return false;
	}

	public static class PlanetGraphic {
		public Sprite sprite;
		private Vector2 tmpVector = new Vector2();
		private Vector2 tmpVector2 = new Vector2();
		private BodyDef bodyDef;
		private FixtureDef fixtureDef;
		private Body body;
		private int solarDistance;

		private float selfRotation = 0;
		private float solarRotation = 0;

		private float selfRotationSpeed;
		private float solarRotationSpeed;

		public PlanetGraphic(String textureName, int solarDistance, float selfRotationSpeed,
					  float solarRotationSpeed) {
			this.selfRotationSpeed = selfRotationSpeed;
			this.solarRotationSpeed = solarRotationSpeed;
			this.sprite = new Sprite(App.TEXTURES.findRegion(textureName));
			this.solarDistance = solarDistance;
			this.solarRotation = (float) (Math.random() * 360);
		}

		public void addToWorld(World world) {
			CircleShape shape = new CircleShape();
			shape.setRadius(sprite.getWidth() / 2);
			fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.KinematicBody;
			body = world.createBody(bodyDef);
			body.createFixture(fixtureDef);
		}

		private void setPosition(float x, float y) {
			sprite.setPosition(
					x - sprite.getWidth() / 2,
					y - sprite.getHeight() / 2);
			tmpVector.set(x, y);
			body.setTransform(x, y, 0);
		}

		private float getRadius() {
			return sprite.getWidth() / 2;
		}

		public Vector2 getPosition() {
			tmpVector2.set(sprite.getX() + getRadius(), sprite.getY() + getRadius());
			return tmpVector2;
		}

		public void update(float delta) {
			selfRotation += selfRotationSpeed;
			solarRotation += solarRotationSpeed;

			sprite.setRotation(selfRotation);
			float x = (float) (Math.sin(solarRotation) * solarDistance);
			float y = (float) (Math.cos(solarRotation) * solarDistance);

			setPosition(x, y);
		}

		public void draw(SpriteBatch batch) {
			sprite.draw(batch);
		}
	}


}
