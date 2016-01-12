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



	private Speed speed = new Speed(30);
	private FuelCapacity fuelCapacity = new FuelCapacity((int) FuelCapacity.INTERNAL_INITIAL);
	private Luck luck = new Luck(.1f);
	private ShieldCapacity shieldCapacity = new ShieldCapacity((int) ShieldCapacity.INTERNAL_INITIAL);
	private LandingDistance landingDistance = new LandingDistance(10);
	private ItemCollectRadius radius = new ItemCollectRadius(12);
	private Inertia inertia = new Inertia(.75f);

	public Attribute[] getAttributes() {
		return new Attribute[]{fuelCapacity, shieldCapacity, speed, luck, landingDistance, radius, inertia};
	}

	private SpaceShipProperties() {
		currentInternalFuel = (int) fuelCapacity.getCurrentValue();
		currentInternalShield = (int) shieldCapacity.getCurrentValue();
		currentSolarPosition = new Vector2(SolarScreen.INITIAL_ARK_POSITION_X, SolarScreen.INITIAL_ARK_POSITION_Y);
	}

	public void testInit() {
		AttributeArtifact a = new AttributeArtifact(Speed.class);
		ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(a);
		artifacts.add(v);
		artifacts.add(o);

		artifacts.add(new AttributeArtifact(Inertia.class));
		artifacts.add(new AttributeArtifact(ItemCollectRadius.class));
		artifacts.add(new AttributeArtifact(ShieldCapacity.class));
		artifacts.add(new AttributeArtifact(FuelCapacity.class));
		artifacts.add(new AttributeArtifact(Luck.class));
		artifacts.add(new AttributeArtifact(LandingDistance.class));

		artifacts.add(new Divide());
		artifacts.add(new Multiply());
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

	public void computeProperties() {
		for (Attribute attribute : getAttributes()) {
			for (Alien alien : aliens) {
				alien.modifyAttribute(attribute);
			}
		}
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

	public float misFortune() {
		return 1 - luck.getCurrentValue(); //HÄ? wieso umgedreht?
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
