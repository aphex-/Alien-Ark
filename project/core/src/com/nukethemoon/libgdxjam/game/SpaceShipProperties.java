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
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Inertia;
import com.nukethemoon.libgdxjam.game.attributes.ItemCollectRadius;
import com.nukethemoon.libgdxjam.game.attributes.LandingDistance;
import com.nukethemoon.libgdxjam.game.attributes.Luck;
import com.nukethemoon.libgdxjam.game.attributes.ShieldCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Speed;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {


	transient public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	public static final int USER_VALUE_MAX = 9999; // the maximum value for crew member bonus

	private List<String> collectedArtifactIds = new ArrayList<String>();

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	private int currentInternalFuel;
	private int currentInternalShield;

	public Vector2 currentSolarPosition;

		/*
		======== SHIP ATTRIBUTES =========
		Ship attributes must be scaled.
		There is an 'internal value' (float) that we use for physic etc. calculations
		There is an 'user value' (int) that we use to show in the UI and to combine artifacts

		The 'internal value' min/max range depends on the attribute
		The 'user value' is between 0 and USER_VALUE_MAX

		shield's and fuel's	internal should match 1 : 1 to the user value
		since this is the only value the player see the direct result (in the progress bars)


		we should rename 'speed' to 'engine power' (more abstract)
		we should rename 'scanradius' to 'scan accuracy' (more abstract)
	 */


	/* 	Cached internal values that are calculated
		if the ship attributes change. A cache is
		needed to avoid frame-wise calculations.
	*/
	private float cachedInternalSpeed = 0; 				// balanced = better
	private float cachedInternalManeuverability = 0; 	// higher = better
	private float cachedInternalLandslide = 0; 			// lower = better
	private float cachedInternalFuelCapacity = 0;		// higher = better
	private float cachedInternalShieldCapacity = 0;		// higher = better
	private float cachedInternalScanRadius = 0;			// higher = better
	private float cachedInternalMisfortune = 0; 		// lower = better


	private Speed speed = new Speed(100);
	private FuelCapacity fuelCapacity = new FuelCapacity((int) FuelCapacity.INTERNAL_INITIAL);
	private Luck luck = new Luck(.1f);
	private ShieldCapacity shieldCapacity = new ShieldCapacity((int) ShieldCapacity.INTERNAL_INITIAL);
	private LandingDistance landingDistance = new LandingDistance(10);
	private ItemCollectRadius radius = new ItemCollectRadius(12);
	private Inertia inertia = new Inertia(.75f);

	public Attribute[] getAttributes(){
		return new Attribute[]{fuelCapacity, shieldCapacity, speed, luck, landingDistance, radius, inertia};
	}

	private SpaceShipProperties() {
		currentInternalFuel = (int) computeFuelCapacity(); // max fuel on start
		currentInternalShield = (int) computeShieldCapacity(); // max shield on start
		currentSolarPosition = new Vector2(SolarScreen.INITIAL_ARK_POSITION_X, SolarScreen.INITIAL_ARK_POSITION_Y);
		updateCache();
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

		int initialUserValue = toUserValue(FuelCapacity.INTERNAL_INITIAL, FuelCapacity.INTERNAL_MIN, FuelCapacity.INTERNAL_MAX);
		int alienUserBonus = 0; // TODO: calculate by crew members
		int userValue = initialUserValue + alienUserBonus;
		return toInternalValue(userValue, FuelCapacity.INTERNAL_MIN, FuelCapacity.INTERNAL_MAX);
	}

	private float computeShieldCapacity() {
		int initialUserValue = toUserValue(ShieldCapacity.INTERNAL_INITIAL, ShieldCapacity.INTERNAL_MIN, ShieldCapacity.INTERNAL_MAX);
		int alienUserBonus = 0; // TODO: calculate by crew members
		int userValue = initialUserValue + alienUserBonus;
		return toInternalValue(userValue, ShieldCapacity.INTERNAL_MIN, ShieldCapacity.INTERNAL_MAX);
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

	public int getCurrentInternalFuel() {
		return currentInternalFuel;
	}

	public int setCurrentInternalFuel(int fuel) {
		currentInternalFuel = Math.max(0, Math.min(fuel, getFuelCapacity()));
		return currentInternalFuel;
	}

	public int addCurrentInternalFuel(int add) {
		return setCurrentInternalFuel(getCurrentInternalFuel() + add);
	}

	public int getCurrentInternalShield() {
		return currentInternalShield;
	}

	public int setCurrentInternalShield(int shield) {
		currentInternalShield = Math.max(0, Math.min(shield, getShieldCapacity()));
		return currentInternalShield;
	}

	public int addCurrentShield(int add) {
		return setCurrentInternalShield(getCurrentInternalShield() + add);
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
		cachedInternalShieldCapacity = computeShieldCapacity();	// internal min 200.00f 	internal max 9999.00f
		cachedInternalScanRadius = 5;							// internal min   5.00f 	internal max   50.00f
		cachedInternalMisfortune = 0;							// internal min   0.00f 	internal max    1.00f
	}

	/**
	 * Calculates the 'internal value' by the 'user value'.
	 * @param userValue The 'user value' between the min and max.
	 * @param internalMin The minimum 'internal value' of the attribute.
	 * @param internalMax The maximum 'internal value' of the attribute.
	 * @return The 'internal value'.
	 */
	public static float toInternalValue(int userValue, float internalMin, float internalMax) {
		float userValueScale = (float) (userValue) / (float) (USER_VALUE_MAX);
		return ((internalMax - internalMin) * userValueScale) + internalMin;
	}

	public static int toUserValue(float internalValue, float internalMin, float internalMax) {
		float internalValueScale = (internalValue - internalMin) / (internalMax - internalMin);
		return (int) ((USER_VALUE_MAX) * internalValueScale);
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

	public float misFortune() {
		return 0;
	}

}
