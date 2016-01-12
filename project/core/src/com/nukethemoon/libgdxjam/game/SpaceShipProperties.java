package com.nukethemoon.libgdxjam.game;

import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Decrease;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Divide;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Multiply;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
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


	transient public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	transient public static final int INITIAL_MAX_FUEL = 200;
	transient public static final int INITIAL_MAX_SHIELD = 100;

	private List<String> collectedArtifactIds = new ArrayList<String>();

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	private int currentFuel;
	private int currentShield;

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

		computeMaxFuel();
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
		// this changed the current and max value every time called
		// I had to comment it out to test a fuel progress bar case
		/*if (aliens == null) {
			aliens = new ArrayList<Alien>();
		}
		for (Alien alien : aliens) {
			alien.modifyAttribute(maxFuel);
		}
		return (int) maxFuel.getCurrentValue();*/
		// TODO: depend on aliens
		return INITIAL_MAX_FUEL;
	}

	public int computeMaxShield() {
		// TODO: depend on aliens
		return INITIAL_MAX_SHIELD;
	}

	public int getCurrentFuel() {
		return currentFuel;
	}

	public int setCurrentFuel(int fuel) {
		currentFuel = Math.max(0, Math.min(fuel, computeMaxFuel()));
		return currentFuel;
	}

	public int addCurrentFuel(int add) {
		return setCurrentFuel(getCurrentFuel() + add);
	}

	public int getCurrentShield() {
		return currentShield;
	}

	public int setCurrentShield(int shield) {
		currentShield = Math.max(0, Math.min(shield, computeMaxShield()));
		return currentShield;
	}

	public int addCurrentShield(int add) {
		return setCurrentShield(getCurrentShield() + add);
	}

	public void addArtifact(String id) {
		collectedArtifactIds.add(id);
	}

	public boolean isCollectedArtifact(String id) {
		return collectedArtifactIds.contains(id);
	}
}
