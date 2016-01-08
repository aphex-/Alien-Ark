package com.nukethemoon.libgdxjam.screens.solar;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.RocketListener;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.SolarSystem;
import com.nukethemoon.libgdxjam.ui.RocketMainTable;

import java.util.ArrayList;
import java.util.List;


public class SolarScreen implements Screen, RocketListener {

	//TODO:
	/*
	- in die mitte kommt eine fette sonne, wenn du da rein fliegst, explodiert das schiff und sind alle aliens tot
	- apply attributes to solarscreen
	- planeten müssen entdeckt werden per radar */

	private static final int RAYS_NUM = 333;
	private final World world;

	private Vector2 shipPosition = new Vector2(INITIAL_ARK_POSITION_X, INITIAL_ARK_POSITION_Y);
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


	private static final int INITIAL_ARK_POSITION_Y = 10;
	private static final int INITIAL_ARK_POSITION_X = 10;

	private SpriteBatch batch;

	private Sprite arkSprite;
	private Sprite exhaustSprite;

	private final Sprite[] planetSprites = new Sprite[SolarSystem.NUMBER_OF_PLANETS];

	private int screenHeight;
	private int screenWidth;

	float arkHeight;
	float arkWidth;

	private Stage stage;
	private Sprite sunSprite;
	private RocketMainTable mainUI;
	private Rocket rocket;
	private List<Body> bodies;
	private PointLight[] pointLight;


	public SolarScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		batch = new SpriteBatch();
		arkSprite = new Sprite(App.TEXTURES.findRegion("ship_placeholder"));
		exhaustSprite = new Sprite(App.TEXTURES.findRegion("exhaust_placeholder"));

		world = new World(new Vector2(0, 0), true);


		//new DirectionalLight(rayHandler, RAYS_NUM, new Color(1, 0.6f, 0.9f, 0.6f), 45);
		setupSpaceship();
		setupArkButton(uiSkin, multiplexer);
		setupPlanets();

		rayHandler = createRayHandler(world);
		createPointLights();
		updateShadowBodies(world);

	}

	private void createPointLights() {

		new PointLight(rayHandler, RAYS_NUM, new Color(1, 0.8f, 0.8f, 1f), 2000, 0, 0);
	}

	private void setupSpaceship() {
		rocket = new Rocket();
		rocket.setListener(this);

		arkSprite.setPosition(INITIAL_ARK_POSITION_X, INITIAL_ARK_POSITION_Y);
		arkWidth = arkSprite.getWidth();
		arkHeight = arkSprite.getHeight();

		exhaustSprite.setPosition(INITIAL_ARK_POSITION_X, INITIAL_ARK_POSITION_Y);
	}

	private void setupPlanets() {
		planetSprites[0] = new Sprite(App.TEXTURES.findRegion("planet_1_placeholder"));
		planetSprites[1] = new Sprite(App.TEXTURES.findRegion("planet_2_placeholder"));
		planetSprites[2] = new Sprite(App.TEXTURES.findRegion("planet_3_placeholder"));
		planetSprites[3] = new Sprite(App.TEXTURES.findRegion("planet_1_placeholder"));


		sunSprite = new Sprite(App.TEXTURES.findRegion("sun_placeholder"));
		sunSprite.setPosition(SolarSystem.SUN_POSITION.x, SolarSystem.SUN_POSITION.y);

		pointLight = new PointLight[SolarSystem.NUMBER_OF_PLANETS];
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {
			Vector2 position = App.solarSystem.getPlanetPosition(i);
			planetSprites[i].setPosition(position.x, position.y);
		}
	}

	private void setupArkButton(Skin uiSkin, InputMultiplexer multiplexer) {

		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);

		mainUI = new RocketMainTable(uiSkin);
		mainUI.setShieldValue(rocket.getShield(), rocket.getMaxShield());
		mainUI.setFuelValue(rocket.getFuel(), rocket.getMaxFuel());

		TextButton arkScreenButton = new TextButton("open Ark", uiSkin);
		arkScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				openArkScreen();
			}
		});
		mainUI.add(arkScreenButton);
		stage.addActor(mainUI);
	}

	private RayHandler createRayHandler(World world) {
		bodies = new ArrayList<Body>();
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		RayHandler rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.1f);
		rayHandler.setCulling(true);
		rayHandler.pointAtLight(50, 0);
		rayHandler.setBlurNum(1);
		return rayHandler;
	}

	private void updateShadowBodies(World world) {
		for (Body body : bodies) {
			world.destroyBody(body);
		}
		bodies.clear();

//		PolygonShape shape = new PolygonShape();
		CircleShape shape = new CircleShape();
		for (int i = 0; i < planetSprites.length; i++) {
			Vector2 pos = App.solarSystem.getPlanetLightPosition(i, (int) (-1 * planetSprites[i].getWidth()));
			//shape.setPosition(pos);//setAsBox(planetSprites[i].getWidth() / 2, planetSprites[i].getHeight() / 2);
			shape.setRadius((int) planetSprites[i].getWidth()/2);
			FixtureDef def = new FixtureDef();
			def.restitution = 0.8f;
			def.friction = 0.01f;
			def.shape = shape;
			def.density = 1f;

			BodyDef body = new BodyDef();
			body.type = BodyDef.BodyType.DynamicBody;
			body.position.x = pos.x + planetSprites[i].getWidth() / 2;
			body.position.y = pos.y + planetSprites[i].getHeight() / 2;

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
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		handleArkMovementInput(delta);
		handleAppNavigation();

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.disableBlending();
		renderPlanets();
		renderArc();


		App.solarSystem.rotate((Math.PI / 32), delta);
		updateShadowBodies(world);


		//rayHandler.setCombinedMatrix(camera);
		rayHandler.setCombinedMatrix(camera.combined);
		rayHandler.updateAndRender();

		rocket.handlePhysicTick();

		camera.position.set(arkSprite.getX(), arkSprite.getY(), 0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		stage.act(delta);
		stage.draw();
	}

	private void renderArc() {
		batch.begin();
		moveArcPosition();
		adjustRotationIfNeccessary();
		if (rocket.isThrusting()) {
			exhaustSprite.draw(batch);
		}
		arkSprite.draw(batch);
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
		batch.begin();
		sunSprite.draw(batch);
		for (int i = 0; i < planetSprites.length; i++) {
			Vector2 position = App.solarSystem.getPlanetPosition(i);
			planetSprites[i].setPosition(position.x, position.y);
			planetSprites[i].draw(batch);
		}
		batch.end();
	}

	private void handleArkMovementInput(float delta) {
		adjustCurrentSpeed();

		if (Gdx.app.getInput().isKeyPressed(Input.Keys.RIGHT)) {
			adjustRotation = adjustRotation - (50 * delta);
		} else if (Gdx.app.getInput().isKeyPressed(Input.Keys.LEFT)) {
			adjustRotation = adjustRotation + (50 * delta);
		}
	}

	private void adjustCurrentSpeed() {
		currentSpeedDecay += SPEED_DECREASE_BY_DECAY_RATE;
		if (currentSpeedDecay > 1) {
			currentSpeedDecay = 0;
			currentSpeedLevel -= 1;
		}


		if (Gdx.app.getInput().isKeyPressed(19) && !rocket.isOutOfFuel()) {
			currentSpeedLevel += 1;
			rocket.setThrust(true);

		} else {
			rocket.setThrust(false);
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
	}

	private void moveArcPosition() {
		double radians = Math.toRadians(currentRotation);
		float translateX = (float) Math.cos(radians) * (shipSpeedLevels[currentSpeedLevel]);
		float translateY = (float) Math.sin(radians) * (shipSpeedLevels[currentSpeedLevel]);

		arkSprite.translate(translateX, translateY);

		shipPosition.x = arkSprite.getX();
		shipPosition.y = arkSprite.getY();

		exhaustSprite.setPosition(shipPosition.x, shipPosition.y);
		//checkIfArkIsOffScreenAndCorrect();
	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		rocket.dispose();
		rayHandler.dispose();
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

	private int determinePlanetCollison() {
		for (int i = 0; i < planetSprites.length; i++) {
			Sprite planetSprite = planetSprites[i];
			Rectangle planetBounds = planetSprite.getBoundingRectangle();
			if (planetBounds.contains(shipPosition.x, shipPosition.y)) {
				return i;

			}
		}
		return -1;
	}

	private boolean isArcSelected() {
		return false;
	}

	private void openPlanetScreen(int planetIndex) {
		App.openPlanetScreen(planetIndex);
	}

	private void openArkScreen() {
		App.openArkScreen();
	}

	private void checkIfArkIsOffScreenAndCorrect() {
		if (isArkOffScreenLeft()) {
			arkSprite.translateX(0 - shipPosition.x + screenWidth - 20);
		}
		if (isArkOffScreenRight()) {
			arkSprite.translateX(-1 * (arkWidth + screenWidth - 20));
		}
		if (isArkOffScreenTop()) {
			arkSprite.translateY(-1 * (arkHeight + screenHeight - 20));
		}
		if (isArkOffScreenBottom()) {
			arkSprite.translateY(0 - shipPosition.y + screenHeight - 20);
		}

	}

	private boolean isArkOffScreenBottom() {
		return shipPosition.y < 0 - arkHeight;
	}

	private boolean isArkOffScreenTop() {
		return screenHeight < shipPosition.y;
	}

	private boolean isArkOffScreenRight() {
		return screenWidth < shipPosition.x;
	}

	private boolean isArkOffScreenLeft() {
		return shipPosition.x < 0 - arkWidth;
	}

	private void handleAppNavigation() {
		int planetIndex = determinePlanetCollison();
		if (planetIndex != -1) {
			openPlanetScreen(planetIndex);
		}

		if (isArcSelected()) {
			openArkScreen();
		}
	}

	@Override
	public void onRocketLanded() {

	}

	@Override
	public void onRocketLaunched() {

	}

	@Override
	public void onRocketDisabledThrust() {

	}

	@Override
	public void onRocketEnabledThrust() {

	}

	@Override
	public void onRocketDamage() {
		App.audioController.playSound("hit_high.mp3");
		mainUI.setShieldValue(rocket.getShield(), rocket.getMaxShield());
	}

	@Override
	public void onRocketFuelConsumed() {
		mainUI.setFuelValue(rocket.getFuel(), rocket.getMaxFuel());
	}

	@Override
	public void onRocketExploded() {
		App.audioController.playSound("explosion.mp3");
	}

	@Override
	public void onRocketFuelBonus() {

	}

	@Override
	public void onRocketShieldBonus() {

	}

	@Override
	public void onRocketChangedTilePosition() {

	}
}
