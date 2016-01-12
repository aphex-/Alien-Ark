package com.nukethemoon.libgdxjam.game.attributes;


public class FuelCapacity extends Attribute {

	public static float INTERNAL_MIN = 0;
	public static float INTERNAL_MAX = 9999;
	public static float INTERNAL_INITIAL = 200;

	public FuelCapacity(int maxFuel) {
		setCurrentValue(maxFuel);
	}

}
