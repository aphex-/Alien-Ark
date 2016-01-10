package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Decrease;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Divide;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Multiply;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
import com.nukethemoon.libgdxjam.game.attributes.FuelConsumption;
import com.nukethemoon.libgdxjam.game.attributes.Inertia;
import com.nukethemoon.libgdxjam.game.attributes.ItemCollectRadius;
import com.nukethemoon.libgdxjam.game.attributes.LandingDistance;
import com.nukethemoon.libgdxjam.game.attributes.Luck;
import com.nukethemoon.libgdxjam.game.attributes.MaxFuel;
import com.nukethemoon.libgdxjam.game.attributes.Shield;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {


	public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	public static final int INITIAL_MAX_FUEL = 1000;
	public static final float INITAL_FUEL_CONSUMPTION = 0.1f;

	public static final int INITIAL_MAX_SHIELD = 1000;

	private Vector2 position;
	private Vector2 movementVector;

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	public int currentFuel;
	public int currentShield;

	private Speed speed = new Speed(100);
	private MaxFuel maxFuel = new MaxFuel(INITIAL_MAX_FUEL);
	private Luck luck = new Luck(.1f);
	private Shield shield = new Shield(INITIAL_MAX_SHIELD);
	private LandingDistance landingDistance = new LandingDistance(10);
	private ItemCollectRadius radius = new ItemCollectRadius(12);
	private Inertia inertia = new Inertia(.75f);

	public Attribute[] getAttributes(){
		return new Attribute[]{maxFuel, shield, speed, luck, landingDistance, radius, inertia};
	}

	private SpaceShipProperties() {
		currentFuel = computeMaxFuel();
	}

	public void testInit() {
		AttributeArtifact a = new AttributeArtifact(Speed.class);
		ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(a);
		artifacts.add(v);
		artifacts.add(o);

		artifacts.add(new Divide());
		artifacts.add(new Multiply());

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

		for (Alien alien : aliens) {
			alien.modifyAttribute(maxFuel);
		}

		currentFuel = (int) Math.min(currentFuel, maxFuel.getCurrentValue());
		return (int) maxFuel.getCurrentValue();
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


	public int getCurrentShield() {
		return currentShield;
	}
}
