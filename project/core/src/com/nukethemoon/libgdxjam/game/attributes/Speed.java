package com.nukethemoon.libgdxjam.game.attributes;


public class Speed extends Attribute {
	public Speed(int speed) {
		setCurrentValue(speed);
	}

	@Override
	public String name() {
		return "SPEED";
	}
}
