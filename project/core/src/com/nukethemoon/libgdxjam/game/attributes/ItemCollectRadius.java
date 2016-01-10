package com.nukethemoon.libgdxjam.game.attributes;


public class ItemCollectRadius extends Attribute{
	public ItemCollectRadius(int radius){
		setCurrentValue(radius);
	}

	@Override
	public String name() {
		return "ITEM COLLECT RADIUS";
	}
}
