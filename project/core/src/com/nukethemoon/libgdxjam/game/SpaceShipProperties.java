package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Divide;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Multiply;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Inertia;
import com.nukethemoon.libgdxjam.game.attributes.ScanRadius;
import com.nukethemoon.libgdxjam.game.attributes.Landslide;
import com.nukethemoon.libgdxjam.game.attributes.Luck;
import com.nukethemoon.libgdxjam.game.attributes.ShieldCapacity;
import com.nukethemoon.libgdxjam.game.attributes.EnginePower;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {


	transient public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	public static final int USER_VALUE_MAX = 9999; // the maximum value for crew member bonus

	public static final int INITIAL_ENGINE_POWER = 500;
	public static final int INITIAL_SHIELD_CAPACITY = 100;
	public static final int INITIAL_FUEL_CAPACITY = 200;
	public static final float INITIAL_LUCK = 200f;
	public static final int INITIAL_LANDSLIDE = 9900;
	public static final int INITIAL_SCAN_RADIUS = 200;
	public static final float INITIAL_INERTIA = 9900;

	private List<String> collectedArtifactIds = new ArrayList<String>(); // just to save the already collected artifacts
	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	private int currentInternalFuel;
	private int currentInternalShield;
	public Vector2 currentSolarPosition;

	private EnginePower enginePower;
	private FuelCapacity fuelCapacity;
	private Luck luck;
	private ShieldCapacity shieldCapacity;
	private Landslide landslide;
	private ScanRadius scanRadius;
	private Inertia inertia;

	public Attribute[] getAttributes() {
		return new Attribute[]{enginePower, fuelCapacity, shieldCapacity, landslide, luck, scanRadius, inertia};
	}

	private SpaceShipProperties() {
		reset();
		artifacts.add(new ValueArtifact(100));
		artifacts.add( new Increase());
	}

	public void testInit() {

		ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(new AttributeArtifact(EnginePower.class));
		artifacts.add(new AttributeArtifact(EnginePower.class));
		artifacts.add(v);
		artifacts.add(o);
		artifacts.add(new ValueArtifact(10));
		artifacts.add(new ValueArtifact(15));
		artifacts.add(new AttributeArtifact(Inertia.class));
		artifacts.add(new AttributeArtifact(ScanRadius.class));
		artifacts.add(new AttributeArtifact(ShieldCapacity.class));
		artifacts.add(new AttributeArtifact(FuelCapacity.class));
		artifacts.add(new AttributeArtifact(Luck.class));
		artifacts.add(new AttributeArtifact(Landslide.class));

		Alien.createAlien(v, o, new AttributeArtifact(EnginePower.class));

		artifacts.add(new Divide());
		artifacts.add(new Increase());
		artifacts.add(new Multiply());
	}

	/**
	 * For GameOver
	 */
	public void reset() {
		collectedArtifactIds.clear();
		artifacts.clear();
		aliens.clear();

		resetAttributes();

		currentInternalFuel = (int) fuelCapacity.getCurrentValue();
		currentInternalShield = (int) shieldCapacity.getCurrentValue();
		currentSolarPosition = new Vector2(SolarScreen.INITIAL_ARK_POSITION_X, SolarScreen.INITIAL_ARK_POSITION_Y);
	}

	public void onArtifactCollected(Artifact artifact, String inGameId) {
		registerArtifactCollected(inGameId);
		getArtifacts().add(artifact);
	}

	public void registerArtifactCollected(String id) {
		collectedArtifactIds.add(id);
	}

	public boolean isArtifactCollected(String id) {
		return collectedArtifactIds.contains(id);
	}

	public boolean unregisterCollectedArtifact(String id) {
		return collectedArtifactIds.remove(id);
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

	public void computeProperties() {
		resetAttributes();

		for (Attribute attribute : getAttributes()) {
			for (Alien alien : aliens) {
				alien.modifyAttribute(attribute);
			}
		}
	}

	private void resetAttributes() {
		enginePower = new EnginePower(INITIAL_ENGINE_POWER);
		fuelCapacity = new FuelCapacity(INITIAL_FUEL_CAPACITY);
		luck = new Luck(INITIAL_LUCK);
		shieldCapacity = new ShieldCapacity(INITIAL_SHIELD_CAPACITY);
		landslide = new Landslide(INITIAL_LANDSLIDE);
		scanRadius = new ScanRadius(INITIAL_SCAN_RADIUS);
		inertia = new Inertia(INITIAL_INERTIA);
	}

	/**
	 * Calculates the 'internal value' by the 'user value'.
	 *
	 * @param userValue   The 'user value' between the min and max.
	 * @param internalMin The minimum 'internal value' of the attribute.
	 * @param internalMax The maximum 'internal value' of the attribute.
	 * @return The 'internal value'.
	 */
	public static float toInternalValue(float userValue, float internalMin, float internalMax) {
		float userValueScale = (float) (userValue) / (float) (USER_VALUE_MAX);
		return ((internalMax - internalMin) * userValueScale) + internalMin;
	}

	public static int toUserValue(float internalValue, float internalMin, float internalMax) {
		float internalValueScale = (internalValue - internalMin) / (internalMax - internalMin);
		return (int) ((USER_VALUE_MAX) * internalValueScale);
	}


	public float getEnginePower() {
		// balanced = better
		return toInternalValue(enginePower.getCurrentValue(), 25, 90);
	}

	public float getInertia() {
		// balanced = better
		return toInternalValue(9999 - inertia.getCurrentValue(), 0.75f, 3f);
	}

	public float getLandslide() {
		// lower = better
		return toInternalValue(landslide.getCurrentValue(), 0.2f, 3f);
	}

	public int getFuelCapacity() {
		// higher = better
		return (int) toInternalValue(fuelCapacity.getCurrentValue(), 0f, 9999f);
	}

	public int getShieldCapacity() {
		// higher = better
		return (int) toInternalValue(shieldCapacity.getCurrentValue(), 0f, 9999f);
	}

	public float getScanRadius() {
		// higher = better
		return toInternalValue(scanRadius.getCurrentValue(), 5, 70);
	}

	public float getLuck() {
		// lower = better
		return toInternalValue(luck.getCurrentValue(), 0, 1);
	}



	/*

	we should rename 'speed' to 'engine power' (more abstract)
	we should rename 'scanradius' to 'scan accuracy' (more abstract)

	Speed				// internal min  20.00f 	internal max  100.00f
	Maneuverability 	// internal min   0.75f 	internal max    3.00f
	Landslide 			// internal min   0.20f 	internal max    3.00f
	FuelCapacity 		// internal min 200.00f 	internal max 9999.00f
	ShieldCapacity = 	// internal min 200.00f 	internal max 9999.00f
	ScanRadius			// internal min   5.00f 	internal max   50.00f
	Misfortune 			// internal min   0.00f 	internal max    1.00f

	Ship attributes must be scaled.
	The 'internal value' (float) that is used physic etc. calculations
	The 'user value' (int) is used to show in the UI and to combine artifacts

	The 'internal value' min/max range depends on the attribute
	The 'user value' is always between 0 and USER_VALUE_MAX

	shield's and fuel's	internal should match 1 : 1 to the user value
	since this is the only value the player see the direct result (in the progress bars)
	 */

}
