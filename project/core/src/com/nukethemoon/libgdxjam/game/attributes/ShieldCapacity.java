package com.nukethemoon.libgdxjam.game.attributes;


public class ShieldCapacity extends Attribute{

	public static float INTERNAL_MIN = 0;
	public static float INTERNAL_MAX = 9999;
	public static float INTERNAL_INITIAL = 100;

	public ShieldCapacity(int shield){
		setCurrentValue(shield);
	}


}
