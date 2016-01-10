package com.nukethemoon.libgdxjam.game.attributes;


public class Shield extends Attribute{
	public Shield(int shield){
		setCurrentValue(shield);
	}

	@Override
	public String name() {
		return "SHIELD";
	}
}
