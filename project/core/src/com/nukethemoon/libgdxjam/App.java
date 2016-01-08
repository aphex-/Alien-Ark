package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.input.InputController;
import com.nukethemoon.libgdxjam.screens.MenuScreen;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.util.HashMap;
import java.util.Map;

public class App extends Game {


	private static InputMultiplexer MULTIPLEXER;
	private static App app;
	public static TextureAtlas TEXTURES;

	private static Map<Class<? extends Screen>, ? extends Screen> SCREENS = new HashMap<Class<? extends Screen>, Screen>();
	public static AudioController audioController;


	@Override
	public void create () {
		app = this;
		Bullet.init();
		TEXTURES = new TextureAtlas("textures/game.atlas");
		Styles.init(TEXTURES);
		MULTIPLEXER = new InputMultiplexer();
		MULTIPLEXER.addProcessor(new InputController());
		Gdx.input.setInputProcessor(MULTIPLEXER);

		audioController = new AudioController();




		// instance space ship
		// load game entities

		// openScreen(SplashScreen.class);
		//openPlanetScreen(1);
		SpaceShipProperties.properties.testInit();
		openArkScreen();


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
}
