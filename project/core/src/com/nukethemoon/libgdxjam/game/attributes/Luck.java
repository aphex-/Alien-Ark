package com.nukethemoon.libgdxjam.game.attributes;

/**
 * Increases the chance to find rare artifacts
 */
public class Luck extends Attribute{
	public Luck(float val){
		setCurrentValue(val);
	}

	@Override
	public String name() {
		return "SENSOR ACCURACY";
	}
}
