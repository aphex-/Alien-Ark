package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;
import com.nukethemoon.tools.opusproto.gemoetry.PointList;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionConfig;
import com.nukethemoon.tools.opusproto.gemoetry.scatterer.massspring.SimplePositionScattering;
import com.nukethemoon.tools.opusproto.noise.Algorithms;

import java.util.Random;

public class SolarSystem {


	public static final int NUMBER_OF_PLANETS = 12;

	public static final Vector2 SUN_POSITION = new Vector2(600,600);

	public boolean[] isPlanetVisible = new boolean[NUMBER_OF_PLANETS];

	public boolean isPlanetVisible(int index) {
		return isPlanetVisible[index];
	}

	public void setPlanetVisible(int index) {
		this.isPlanetVisible[index]  = true;
	}


	private Vector2[] planetPositions = new Vector2[NUMBER_OF_PLANETS];


	public void calculatePlanetPositions() {
		Algorithms algorithms = new Algorithms();
		SimplePositionConfig positionConfig = new SimplePositionConfig("internal");
		SimplePositionScattering scattering = new SimplePositionScattering(positionConfig, 2323.34523, algorithms, null);
		PointList pointList = (PointList) scattering.createGeometries(150, 150, 5000, 5000, 994234.234234);
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {

			float[] points = pointList.getPoints();
			Vector2 planetPosition = calculateSuitablePlanetPosition(points, i);
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

				Rectangle proposedRect = getRectFromPosition(planetPositions[lastIndex]);
				Rectangle newRect = getRectFromPosition(result);
				Rectangle sunRect = getRectFromPosition(SUN_POSITION);
				if (Intersector.overlaps(newRect, proposedRect)) {
					foundPosition = false;
					break;
				} else if (Intersector.overlaps(newRect, proposedRect)) {
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

	private Rectangle getRectFromPosition(Vector2 planetPosition) {
		return new Rectangle(planetPosition.x - 50, planetPosition.y -50, 100, 100);
	}

}
