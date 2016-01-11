package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.input.InputController;
import com.nukethemoon.libgdxjam.screens.MenuScreen;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.SolarSystem;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.io.IOException;

public class App extends Game {


	private static InputMultiplexer MULTIPLEXER;
	private static App app;
	public static TextureAtlas TEXTURES;

	public static AudioController audioController;

	public static SolarSystem solarSystem = new SolarSystem();

	private static Gson gson;
	private static Save save;

	@Override
	public void create () {
		app = this;
		Bullet.init();
		Models.init();
		gson = new GsonBuilder().setPrettyPrinting().create();
		loadSaveGame();
		TEXTURES = new TextureAtlas("textures/game.atlas");
		Styles.init(TEXTURES);
		MULTIPLEXER = new InputMultiplexer();
		MULTIPLEXER.addProcessor(new InputController());
		Gdx.input.setInputProcessor(MULTIPLEXER);

		audioController = new AudioController();
		solarSystem = new SolarSystem();
		solarSystem.calculatePlanetPositions();

		// instance space ship
		// load game entities
		// openScreen(SplashScreen.class);
		openPlanetScreen(0);
		SpaceShipProperties.properties.testInit();
		//openArkScreen();


		//openSolarScreen();
	}

	public static void openScreen(Class<? extends Screen> screenClass) {
		/*Screen screen = SCREENS.get(screenClass);
		if (screen == null) {

		}*/
		MenuScreen menuScreen = new MenuScreen(Styles.UI_SKIN, MULTIPLEXER);
		menuScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(menuScreen);
	}

	public static void openPlanetScreen(int worldIndex) {
		PlanetScreen screen = new PlanetScreen(Styles.UI_SKIN, MULTIPLEXER, worldIndex);
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(screen);
	}

	public static void openSolarScreen() {
		SolarScreen screen = new SolarScreen(Styles.UI_SKIN, MULTIPLEXER);
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(screen);
	}

	public static void openArkScreen() {

		Screen screen = new ArkScreen(Styles.UI_SKIN, MULTIPLEXER);
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(screen);
	}


	public static void onGameOver() {
		openSolarScreen();
	}

	public static void saveProgress() {
		FileHandle fileHandle = openSaveFile();
		if (fileHandle != null) {
			String jsonString = gson.toJson(save);
			fileHandle.writeString(jsonString, false);
		}
	}

	private static void loadSaveGame() {
		FileHandle fileHandle = openSaveFile();
		if (fileHandle != null) {
			save = gson.fromJson(fileHandle.readString(), Save.class);
		}
		if (save == null) {
			save = new Save();
		}
	}

	private static FileHandle openSaveFile() {
		if (!Gdx.files.local("save/save.json").exists()) {
			try {
				Gdx.files.local("save/save.json").file().createNewFile();
			} catch (IOException e) {
				Log.e(App.class, "Error creating save file");
				return null;
			}
		}
		return Gdx.files.local("save/save.json");
	}

	@Override
	public void dispose() {
		super.dispose();
		Models.dispose();
	}
}
