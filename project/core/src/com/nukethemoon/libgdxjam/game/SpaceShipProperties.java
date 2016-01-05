package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Decrease;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.attributes.FuelConsumption;
import com.nukethemoon.libgdxjam.game.attributes.MaxFuel;
import com.nukethemoon.libgdxjam.game.attributes.Speed;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {


	public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	public static final float INITIAL_MAX_FUEL = 1000;
	public static final float INITAL_FUEL_CONSUMPTION = 0.1f;

	private Vector2 position;
	private Vector2 movementVector;

	public boolean[] isPlanetVisible = new boolean[SolarSystem.NUMBER_OF_PLANETS];

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	public int currentFuel;

	private SpaceShipProperties() {
		currentFuel = computeMaxFuel();
		for (int i = 0; i < SolarSystem.NUMBER_OF_PLANETS; i++) {
			isPlanetVisible[i] = false;
		}
	}


	public void testInit() {
		AttributeArtifact a = new AttributeArtifact(Speed.class);
		ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(a);
		artifacts.add(v);
		artifacts.add(o);

		Alien alien = Alien.createAlien(a, o, v);
		aliens.add(alien);

		a = new AttributeArtifact(Speed.class);
		v = new ValueArtifact(0.5f);
		o = new Decrease();
		alien = Alien.createAlien(a, o, v);
		aliens.add(alien);

		a = new AttributeArtifact(MaxFuel.class);
		v = new ValueArtifact(100);
		o = new Decrease();
		alien = Alien.createAlien(a, o, v);
		aliens.add(alien);

		a = new AttributeArtifact(FuelConsumption.class);
		v = new ValueArtifact(0.25f);
		o = new Increase();
		alien = Alien.createAlien(a, o, v);
		aliens.add(alien);

		computeMaxFuel();
		computeFuelConsumption();
		computeSpeedPerUnit();
	}


	public float computeSpeedPerUnit() {

		Speed speed = new Speed();
		for (Alien a : aliens) {
			a.modifyAttribute(speed);
		}
		return speed.getCurrentValue();
	}

	public List<Alien> getAliens() {
		return aliens;
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public int computeMaxFuel() {
		if (aliens == null) {
			aliens = new ArrayList<Alien>();
		}
		MaxFuel max = new MaxFuel();
		for (Alien alien : aliens) {
			alien.modifyAttribute(max);
		}

		currentFuel = (int) Math.min(currentFuel, max.getCurrentValue());
		return (int) max.getCurrentValue();
	}

	public float computeFuelConsumption() {
		FuelConsumption consumption = new FuelConsumption();
		for (Alien alien : aliens) {
			alien.modifyAttribute(consumption);
		}
		return consumption.getCurrentValue();
	}

	public int getCurrentFuel() {
		return currentFuel;
	}

	public boolean isPlanetVisible(int index) {
		return isPlanetVisible[index];
	}

	public void setPlanetVisible(int index) {
		this.isPlanetVisible[index]  = true;
	}
}
