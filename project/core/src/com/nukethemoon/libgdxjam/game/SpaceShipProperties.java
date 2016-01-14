package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;
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
	public static final int INITIAL_SPEED = 30;
	public static final float INITIAL_LUCK = .1f;
	public static final int INITIAL_LANDING_DISTANCE = 10;
	public static final int INITIAL_COLLECT_RADIUS = 12;
	public static final float INITIAL_INERTIA = .75f;

	private List<String> collectedArtifactIds = new ArrayList<String>(); // just to save the already collected artifacts

	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Alien> aliens = new ArrayList<Alien>();

	private int currentInternalFuel;
	private int currentInternalShield;
	public Vector2 currentSolarPosition;

	private Speed speed = new Speed(INITIAL_SPEED);

	private FuelCapacity fuelCapacity = new FuelCapacity((int) FuelCapacity.INTERNAL_INITIAL);
	private Luck luck = new Luck(INITIAL_LUCK);
	private ShieldCapacity shieldCapacity = new ShieldCapacity((int) ShieldCapacity.INTERNAL_INITIAL);
	private LandingDistance landingDistance = new LandingDistance(INITIAL_LANDING_DISTANCE);
	private ItemCollectRadius radius = new ItemCollectRadius(INITIAL_COLLECT_RADIUS);
	private Inertia inertia = new Inertia(INITIAL_INERTIA);

	public Attribute[] getAttributes() {
		return new Attribute[]{fuelCapacity, shieldCapacity, speed, luck, landingDistance, radius, inertia};
	}

	private SpaceShipProperties() {
		currentInternalFuel = (int) fuelCapacity.getCurrentValue();
		currentInternalShield = (int) shieldCapacity.getCurrentValue();
		currentSolarPosition = new Vector2(SolarScreen.INITIAL_ARK_POSITION_X, SolarScreen.INITIAL_ARK_POSITION_Y);
	}

	public void testInit() {

		/*ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(new AttributeArtifact(Speed.class));
		artifacts.add(new AttributeArtifact(Speed.class));
		artifacts.add(v);
		artifacts.add(o);
		artifacts.add(new ValueArtifact(10));
		artifacts.add(new ValueArtifact(15));
		artifacts.add(new AttributeArtifact(Inertia.class));
		artifacts.add(new AttributeArtifact(ItemCollectRadius.class));
		artifacts.add(new AttributeArtifact(ShieldCapacity.class));
		artifacts.add(new AttributeArtifact(FuelCapacity.class));
		artifacts.add(new AttributeArtifact(Luck.class));
		artifacts.add(new AttributeArtifact(LandingDistance.class));

		Alien.createAlien(v, o, new AttributeArtifact(Speed.class));

		artifacts.add(new Divide());
		artifacts.add(new Increase());
		artifacts.add(new Multiply());*/
	}

	public void onArtifactCollected(Artifact artifact, String inGameId) {
		registerArtifactCollected(inGameId);
		getArtifacts().add(artifact);
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
		speed = new Speed(INITIAL_SPEED);
		fuelCapacity = new FuelCapacity((int) FuelCapacity.INTERNAL_INITIAL);
		luck = new Luck(INITIAL_LUCK);
		shieldCapacity = new ShieldCapacity((int) ShieldCapacity.INTERNAL_INITIAL);
		landingDistance = new LandingDistance(INITIAL_LANDING_DISTANCE);
		radius = new ItemCollectRadius(INITIAL_COLLECT_RADIUS);
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

	public float getSpeed() {
		return speed.getCurrentValue();
	}

	public float getManeuverability() {
		return toInternalValue(inertia.getCurrentValue(), 0.75f, 20f);
	}

	public float getLandslide() {
		return toInternalValue(landingDistance.getCurrentValue(), 1f, 90f);
	}

	public int getFuelCapacity() {
		return (int) fuelCapacity.getCurrentValue();
	}

	public int getShieldCapacity() {
		return (int) shieldCapacity.getCurrentValue();
	}

	public float getScanRadius() {
		return radius.getCurrentValue();
	}

	public float misLuck() {
		return luck.getCurrentValue();
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


	/*

	we should rename 'speed' to 'engine power' (more abstract)
	we should rename 'scanradius' to 'scan accuracy' (more abstract)

	Speed				// internal min  20.00f 	internal max  100.00f		// balanced = better
	Maneuverability 	// internal min   0.75f 	internal max    3.00f		// higher = better
	Landslide 			// internal min   0.20f 	internal max    3.00f		// lower = better
	FuelCapacity 		// internal min 200.00f 	internal max 9999.00f		// higher = better
	ShieldCapacity = 	// internal min 200.00f 	internal max 9999.00f		// higher = better
	ScanRadius			// internal min   5.00f 	internal max   50.00f		// higher = better
	Misfortune 			// internal min   0.00f 	internal max    1.00f		// lower = better

	Ship attributes must be scaled.
	The 'internal value' (float) that is used physic etc. calculations
	The 'user value' (int) is used to show in the UI and to combine artifacts

	The 'internal value' min/max range depends on the attribute
	The 'user value' is always between 0 and USER_VALUE_MAX

	shield's and fuel's	internal should match 1 : 1 to the user value
	since this is the only value the player see the direct result (in the progress bars)
	 */

}
