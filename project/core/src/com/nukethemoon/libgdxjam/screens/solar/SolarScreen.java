package com.nukethemoon.libgdxjam.screens.solar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.tools.opusproto.gemoetry.PointList;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionConfig;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionScattering;
import com.nukethemoon.tools.opusproto.noise.Algorithms;

import java.security.SecureRandom;
import java.util.Random;

import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class SolarScreen implements Screen {

	//TODO:
	/*
	//- wir machen den solarscreen größer so dass wir ein bisschen in alle richtungen scrollen können
	//wenn der sichtbare bereich verlassen wird wird der screen automatisch mit allen objekten gescrollt
	- in die mitte kommt eine fette sonne, wenn du da rein fliegst, explodiert das schiff und sind alle aliens tot
	- apply attributes to solarscreen
	- show fuel/stats
	- planeten müssen entdeckt werden per radar */

	private static final int NUMBER_OF_PLANETS = 12;
	private static final int RAYS_NUM = 33;

	private Vector2 shipPosition = new Vector2(INITIAL_ARK_POSITION_X, INITIAL_ARK_POSITION_Y);
	private final RayHandler rayHandler;
	private OrthographicCamera camera;

	//0 - 359
	private float adjustRotation = 0;

	//starts at 90 - ark facing up
	private float currentRotation = 90;

	private Vector2[] planetPositions = new Vector2[NUMBER_OF_PLANETS];

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

	private final Sprite[] planetSprites = new Sprite[NUMBER_OF_PLANETS];

	private int screenHeight;
	private int screenWidth;

	float arkHeight;
	float arkWidth;
	private boolean showExhaust = false;

	private Stage stage;
	private Table contentTable;
	private Sprite sunSprite;


	public SolarScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		batch = new SpriteBatch();
		arkSprite = new Sprite(App.TEXTURES.findRegion("ship_placeholder"));
		exhaustSprite = new Sprite(App.TEXTURES.findRegion("exhaust_placeholder"));

		World world = new World(new Vector2(0, 0), true);

		rayHandler = new RayHandler(world);
		rayHandler.setShadows(true);

		//new DirectionalLight(rayHandler, RAYS_NUM, new Color(1, 0.6f, 0.9f, 0.6f), 45);

		setupSpaceship();
		setupArkButton(uiSkin, multiplexer);
	}

	private void setupSpaceship() {
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
		planetSprites[4] = new Sprite(App.TEXTURES.findRegion("planet_2_placeholder"));
		planetSprites[5] = new Sprite(App.TEXTURES.findRegion("planet_3_placeholder"));
		planetSprites[6] = new Sprite(App.TEXTURES.findRegion("planet_1_placeholder"));
		planetSprites[7] = new Sprite(App.TEXTURES.findRegion("planet_2_placeholder"));
		planetSprites[8] = new Sprite(App.TEXTURES.findRegion("planet_3_placeholder"));
		planetSprites[9] = new Sprite(App.TEXTURES.findRegion("planet_1_placeholder"));
		planetSprites[10] = new Sprite(App.TEXTURES.findRegion("planet_2_placeholder"));
		planetSprites[11] = new Sprite(App.TEXTURES.findRegion("planet_3_placeholder"));




		sunSprite = new Sprite(App.TEXTURES.findRegion("sun_placeholder"));
		sunSprite.setPosition(600, 600);

		Algorithms algorithms = new Algorithms();
		SimplePositionConfig positionConfig = new SimplePositionConfig("internal");
		SimplePositionScattering scattering = new SimplePositionScattering(positionConfig, 2323.34523, algorithms, null);
		PointList pointList = (PointList) scattering.createGeometries(150, 150, 5000, 5000, 994234.234234);
		for (int i = 0; i < NUMBER_OF_PLANETS; i++) {

			float[] points = pointList.getPoints();
			Vector2 planetPosition = calculateSuitablePlanetPosition(points, i);
			planetSprites[i].setPosition(planetPosition.x, planetPosition.y);
			planetPositions[i] = planetPosition;
		}
	}

	private Vector2 calculateSuitablePlanetPosition(float[] points, int lastIndex) {
		boolean foundPosition = false;
		Vector2 result = new Vector2(100,100);
		int pointCounter = lastIndex;
		Random random = new Random(System.currentTimeMillis());
		while (!foundPosition && pointCounter < points.length - 1) {

			int x  = random.nextInt(1200);
			int y = random.nextInt(1200);
			result = new Vector2(x, y);
			for (int i = 0; i < lastIndex; i++) {
				planetSprites[lastIndex].setPosition(result.x, result.y);
				Rectangle proposedRect = planetSprites[lastIndex].getBoundingRectangle();
				if (Intersector.overlaps(sunSprite.getBoundingRectangle(), proposedRect)) {
					foundPosition = false;
					break;
				} else if (Intersector.overlaps(planetSprites[i].getBoundingRectangle(), proposedRect)) {
					foundPosition = false;
					break;
				} else {
					foundPosition = true;
				}
			}
			pointCounter++;
		}
		return result;

	}

	private void setupArkButton(Skin uiSkin, InputMultiplexer multiplexer) {
		stage = new Stage(new ScreenViewport());
		multiplexer.addProcessor(stage);

		contentTable = new Table(uiSkin).debug();
		contentTable.setPosition(1200, 600);

		TextButton arkScreenButton = new TextButton("open Ark", uiSkin);
		arkScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				openArkScreen();
			}
		});
		contentTable.add(arkScreenButton);
		stage.addActor(contentTable);
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
		renderPlanets();
		renderArc();
		//handleAppNavigation();


		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();

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
		if (showExhaust) {
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
		for (Sprite planetSprite : planetSprites) {
			planetSprite.draw(batch);
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

		if (Gdx.app.getInput().isKeyPressed(19)) {
			currentSpeedLevel += 1;
			showExhaust = true;
		} else {
			showExhaust = false;
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
		rayHandler.dispose();
	}

	@Override
	public void resize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;

		camera = new OrthographicCamera(screenWidth, screenHeight);
		camera.position.set(0, screenWidth / 2f, 0);
		camera.update();

		new PointLight(rayHandler, RAYS_NUM, new Color(1, 1, 1, 0.50f), 2000, 700, 700);


		setupPlanets();
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
			openPlanetScreen(planetIndex + 1);
		}

		if (isArcSelected()) {
			openArkScreen();
		}
	}
}
