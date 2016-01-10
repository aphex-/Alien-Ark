package com.nukethemoon.libgdxjam.game.attributes;


import com.nukethemoon.libgdxjam.game.SpaceShipProperties;

public class MaxFuel extends Attribute {

	public MaxFuel (int maxFuel) {
		setCurrentValue(maxFuel);
	}

	@Override
	public String name() {
		return "MAX FUEL";
	}
}
