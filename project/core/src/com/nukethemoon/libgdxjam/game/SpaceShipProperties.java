package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {

	private Vector2 position;
	private Vector2 movementVector;
	private float fuelPerUnit = 1f;
	private int maxFuel = 100;
	private int currentFuel;

	private List<Artifact> artifacts = new ArrayList<Artifact>();

	private List<Alien> aliens = new ArrayList<Alien>();



	public float computeSpeedPerUnit() {
		return 0f;
	}

}
