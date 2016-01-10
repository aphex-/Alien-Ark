package com.nukethemoon.libgdxjam.game.attributes;

/*
 * Formally known as "friction" but now inverted
 */
public class LandingDistance extends Attribute{
	public LandingDistance(int meter){
		setCurrentValue(meter);
	}

	@Override
	public String name() {
		return "LANDING DISTANCE";
	}
}
