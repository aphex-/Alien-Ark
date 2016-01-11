package com.nukethemoon.libgdxjam;

import com.badlogic.gdx.Gdx;

public class Log {

	public static void d(Class source, String message) {
		if (App.config.debugMode) {
			Gdx.app.log(source.getSimpleName(), message);
		}
	}

	public static void l(Class source, String message) {
		if (App.config.debugMode) {
			Gdx.app.log(source.getSimpleName(), message);
		}
	}


	public static void ex(Class source, Exception exception, String message) {
		Gdx.app.error(source.getSimpleName(), exception.getMessage() + " " + message);
		exception.printStackTrace();
	}

	public static void e(Class source, String message) {
		Gdx.app.error(source.getSimpleName(), message);
	}


}
