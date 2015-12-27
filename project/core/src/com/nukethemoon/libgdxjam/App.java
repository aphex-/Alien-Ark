package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.input.InputController;
import com.nukethemoon.libgdxjam.screens.MenuScreen;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.SplashScreen;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.util.HashMap;
import java.util.Map;

public class App extends Game {

	private static Skin UI_SKIN;
	private static InputMultiplexer MULTIPLEXER;
	private static App app;
	public static TextureAtlas TEXTURES;

	private static Map<Class<? extends Screen>, ? extends Screen> SCREENS = new HashMap<Class<? extends Screen>, Screen>();


	@Override
	public void create () {
		app = this;
		MULTIPLEXER = new InputMultiplexer();
		MULTIPLEXER.addProcessor(new InputController());
		Gdx.input.setInputProcessor(MULTIPLEXER);
		UI_SKIN = new Skin(Gdx.files.internal(Config.UI_SKIN_PATH));

		TEXTURES = new TextureAtlas("textures/game.atlas");

		// instance space ship
		// load game entities

		// openScreen(SplashScreen.class);
		//openPlanetScreen(1);
		openSolarScreen();
	}


	public static void openScreen(Class<? extends Screen> screenClass) {
		/*Screen screen = SCREENS.get(screenClass);
		if (screen == null) {

		}*/

		MenuScreen menuScreen = new MenuScreen(UI_SKIN, MULTIPLEXER);
		menuScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(menuScreen);
	}

	public static void openPlanetScreen(int worldIndex) {
		PlanetScreen screen = new PlanetScreen(UI_SKIN, MULTIPLEXER, worldIndex);
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(screen);
	}

	public static void openSolarScreen() {
		SolarScreen screen = new SolarScreen(UI_SKIN, MULTIPLEXER);
		screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		app.setScreen(screen);
	}


}
