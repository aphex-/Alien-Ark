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
import com.nukethemoon.libgdxjam.input.InputController;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.planet.TutorialController;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.SolarSystem;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;
import com.nukethemoon.libgdxjam.screens.splash.SplashScreen;

import java.io.IOException;

public class App extends Game {


	private static InputMultiplexer MULTIPLEXER;
	private static App app;
	public static TextureAtlas TEXTURES;

	public static AudioController audioController;

	public static SolarSystem solarSystem = new SolarSystem();
	public static TutorialController TUTORIAL_CONTROLLER;

	private static Gson gson;
	public static Save save;
	public static Config config;


	@Override
	public void create () {
		app = this;
		gson = new GsonBuilder().setPrettyPrinting().create();
		loadConfig();
		Bullet.init();
		Models.init(); // needs an initialized Bullet
		loadSaveGame();
		TEXTURES = new TextureAtlas("textures/game.atlas");
		Styles.init(TEXTURES);
		TUTORIAL_CONTROLLER = new TutorialController(Styles.UI_SKIN);
		MULTIPLEXER = new InputMultiplexer();
		MULTIPLEXER.addProcessor(new InputController());
		Gdx.input.setInputProcessor(MULTIPLEXER);

		audioController = new AudioController();
		App.audioController.setSoundEnabled(App.config.playAudio);
		App.audioController.setMusicEnabled(App.config.playAudio);

		solarSystem = new SolarSystem();
		solarSystem.calculatePlanetPositions();

		//SpaceShipProperties.properties.testInit();

		if (config.debugMode) {
			openPlanetScreen(0);
		} else {
			openSplashScreen();
		}
	}

	public static void openSplashScreen() {
		SplashScreen splashScreen = new SplashScreen(MULTIPLEXER);
		splashScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(splashScreen);
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



	public static void saveProgress() {
		FileHandle fileHandle = openFile("save/save.json");
		if (fileHandle != null) {
			String jsonString = gson.toJson(save);
			fileHandle.writeString(jsonString, false);
		}
	}

	public static void saveConfig() {
		FileHandle fileHandle = openFile("save/config.json");
		if (fileHandle != null) {
			String jsonString = gson.toJson(config);
			fileHandle.writeString(jsonString, false);
		}
	}

	private static void loadSaveGame() {
		FileHandle fileHandle = openFile("save/save.json");
		if (fileHandle != null) {
			save = gson.fromJson(fileHandle.readString(), Save.class);
		}
		if (save == null) {
			save = new Save();
		}
	}


	private static void loadConfig() {
		FileHandle fileHandle = openFile("save/config.json");
		if (fileHandle != null) {
			config = gson.fromJson(fileHandle.readString(), Config.class);
		}
		if (config == null) {
			config = new Config();
		}
	}

	private static FileHandle openFile(String path) {
		if (!Gdx.files.local(path).exists()) {
			try {
				Gdx.files.local(path).file().createNewFile();
			} catch (IOException e) {
				Log.e(App.class, "Error creating file " + path);
				return null;
			}
		}
		return Gdx.files.local(path);
	}

	@Override
	public void dispose() {
		super.dispose();
		Models.dispose();
	}
}
