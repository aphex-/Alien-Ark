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
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Shield;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {


	transient public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	public static final int USER_VALUE_MIN = 200;
	public static final int USER_VALUE_MAX = 9999;

	transient public static final int INITIAL_MAX_FUEL = 200;
	transient public static final int INITIAL_MAX_SHIELD = 100;

	private List<String> collectedArtifactIds = new ArrayList<String>();

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	private int currentFuel;
	private int currentShield;

	/* 	Cached internal values that are calculated
		if the ship attributes change. A cache is
		needed to avoid frame-wise calculations.
	*/
	private float cachedInternalSpeed = 0;
	private float cachedInternalManeuverability = 0;
	private float cachedInternalLandslide = 0;
	private float cachedInternalFuelCapacity = 0;
	private float cachedInternalShieldCapacity = 0;
	private float cachedInternalScanRadius = 0;
	private float cachedInternalLuck = 0;


	private Speed speed = new Speed(100);
	private FuelCapacity fuelCapacity = new FuelCapacity(INITIAL_MAX_FUEL);
	private Luck luck = new Luck(.1f);
	private Shield shield = new Shield(INITIAL_MAX_SHIELD);
	private LandingDistance landingDistance = new LandingDistance(10);
	private ItemCollectRadius radius = new ItemCollectRadius(12);
	private Inertia inertia = new Inertia(.75f);

	public Attribute[] getAttributes(){
		return new Attribute[]{fuelCapacity, shield, speed, luck, landingDistance, radius, inertia};
	}

	private SpaceShipProperties() {
		currentFuel = getFuelCapacity();
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

		a = new AttributeArtifact(FuelCapacity.class);
		v = new ValueArtifact(100);
		o = new Decrease();
		alien = Alien.createAlien(a, o, v);
		aliens.add(alien);

		getFuelCapacity();
		computeSpeedPerUnit();

		updateCache();
	}

	private float computeFuelCapacity() {
		// TODO: this changed the current and max value every time called.
		// I had to comment it out to test a fuel progress bar case

		/*if (aliens == null) {
			aliens = new ArrayList<Alien>();
		}
		for (Alien alien : aliens) {
			alien.modifyAttribute(maxFuel);
		}
		return (int) maxFuel.getCurrentValue();*/

		int fuelUserValue = 200; // TODO: calculate by crew members
		return toInternalValue(fuelUserValue, FuelCapacity.INTERNAL_MIN, FuelCapacity.INERNAL_MAX);
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

	public int getCurrentFuel() {
		return currentFuel;
	}

	public int setCurrentFuel(int fuel) {
		currentFuel = Math.max(0, Math.min(fuel, getFuelCapacity()));
		return currentFuel;
	}

	public int addCurrentFuel(int add) {
		return setCurrentFuel(getCurrentFuel() + add);
	}

	public int getCurrentShield() {
		return currentShield;
	}

	public int setCurrentShield(int shield) {
		currentShield = Math.max(0, Math.min(shield, getShieldCapacity()));
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

	/**
	 * Updates the cached values. Call if the crew memvers of
	 * the ship has changed.
	 */
	public void updateCache() {

		// TODO: add compute methods

		cachedInternalSpeed = 30f; 								// internal min  20.00f 	internal max  100.00f
		cachedInternalManeuverability = 2.75f; 					// internal min   0.75f 	internal max    3.00f
		cachedInternalLandslide = 0.2f; 						// internal min   0.20f 	internal max    3.00f
		cachedInternalFuelCapacity = computeFuelCapacity();		// internal min 200.00f 	internal max 9999.00f
		cachedInternalShieldCapacity = 200;						// internal min 200.00f 	internal max 9999.00f
		cachedInternalScanRadius = 5;							// internal min   5.00f 	internal max   50.00f
		cachedInternalLuck = 0;									// internal min   0.00f 	internal max    1.00f
	}

	/*
		======== SHIP ATTRIBUTES =========
		Ship attributes must be scaled.
		There is an 'internal value' (float) that we use for physic etc. calculations
		There is an 'user value' (int) that we use to show in UI and combine artifacts

		The 'internal value' min/max range depends on the attribute
		The 'user value' min/max range is the same for all attributes:
			min = 200, max = 9999
	 */

	/**
	 * Calculates the 'internal value' by the 'user value'.
	 * @param userValue The 'user value' between the min and max.
	 * @param internalMin The minimum 'internal value' of the attribute.
	 * @param internalMax The maximum 'internal value' of the attribute.
	 * @return The 'internal value'.
	 */
	public static float toInternalValue(int userValue, float internalMin, float internalMax) {
		float userValueScale = (float) (userValue - USER_VALUE_MIN) / (float) (USER_VALUE_MAX - USER_VALUE_MIN);
		return ((internalMax - internalMin) * userValueScale) + internalMin;
	}

	public static int toUserValue(float internalValue, float internalMin, float internalMax) {
		float internalValueScale = (internalValue - internalMin) / (internalMax - internalMin);
		return (int) ((USER_VALUE_MAX - USER_VALUE_MIN) * internalValueScale) + USER_VALUE_MIN;
	}

	public float getSpeed() {
		return cachedInternalSpeed;
	}

	public float getManeuverability() {
		return cachedInternalManeuverability;
	}

	public float getLandslide() {
		return cachedInternalLandslide;
	}

	public int getFuelCapacity() {
		return (int) cachedInternalFuelCapacity;
	}

	public int getShieldCapacity() {
		return (int) cachedInternalShieldCapacity;
	}

	public float getScanRadius() {
		return cachedInternalScanRadius;
	}

	public float getLuck() {
		return 0;
	}

}
