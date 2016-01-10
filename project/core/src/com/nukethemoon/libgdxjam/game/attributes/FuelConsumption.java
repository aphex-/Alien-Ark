package com.nukethemoon.libgdxjam.game.attributes;


import com.nukethemoon.libgdxjam.game.SpaceShipProperties;

@Deprecated
public class FuelConsumption extends Attribute {

	public FuelConsumption() {
		setCurrentValue(SpaceShipProperties.INITAL_FUEL_CONSUMPTION);
	}

	@Override
	public String name() {
		return "FUEL CONSUMPTION";
	}
}
