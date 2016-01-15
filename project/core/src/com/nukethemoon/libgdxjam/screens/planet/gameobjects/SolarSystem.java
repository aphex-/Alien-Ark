package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.Planet;

import java.util.Random;

public class SolarSystem implements Disposable {

	public static final int NUMBER_OF_PLANETS = 10;
	public static final Vector2 SUN_POSITION = new Vector2(-150, -150);
	private int[] radius = new int[]{400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300};
	private float[] radians = new float[]{(float) (7 * Math.PI / 8), (float) (12 * Math.PI / 8), (float) (9 * Math.PI / 8), (float) (4 * Math.PI / 8),
			(float) (15 * Math.PI / 8), (float) (2 * Math.PI / 8), (float) (3 * Math.PI / 8), (float) (6 * Math.PI / 8),
			(float) (11 * Math.PI / 8), (float) (16 * Math.PI / 8)};

	private Planet[] planets = new Planet[NUMBER_OF_PLANETS];

	public void calculatePlanetPositions() {
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {
			Planet planet = calculateSuitablePlanet(i);
			planets[i] = planet;
		}
	}

	private Planet calculateSuitablePlanet(int index) {
		Log.d(getClass(), "calculate suitable planet " + index);
		Planet result = new Planet();
		result.radians = radians[index];
		result.radius = radius[index];
		return result;
	}

	private boolean isSeparateOrbit(int radius) {
		for (int i = 0; i < NUMBER_OF_PLANETS; i++) {
			Planet planet = planets[i];
			if (planet == null) {
				continue;
			}
			if (radius < planet.radius + 35 && radius > planet.radius - 35) {
				return false;
			}
		}
		return true;
	}

	private boolean arePlanetsColliding(float radians, int index) {
		for (int i = 0; i < index; i++) {
			Planet planet = planets[i];
			if (Math.abs(radians - planet.radians) < Math.PI / 6) {
				return true;
			}
		}
		return false;

	}

	public Vector2 getPosition(int radius, float radians) {
		int x = (int) (Math.cos(radians) * radius);
		int y = (int) (Math.sin(radians) * radius);
		return new Vector2(x, y);
	}

	public void rotate(double rad, float delta) {
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {
			planets[i].radians += (float) (rad * delta);
		}
	}

	public Vector2 getPlanetPosition(int index) {
		return getPosition(planets[index].radius, planets[index].radians);
	}

	public Vector2 getPlanetShadowPosition(int index, int offset) {
		return getPosition(planets[index].radius - offset, planets[index].radians);
	}


	@Override
	public void dispose() {

	}
}
