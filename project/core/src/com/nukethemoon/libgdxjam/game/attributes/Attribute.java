package com.nukethemoon.libgdxjam.game.attributes;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;

public abstract class Attribute {

	public static final float INTERNAL_MIN = -1;
	public static final float INTERNAL_MAX = -1;

	private float currentValue;

	public float getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
	}

	public static TextureRegion getSlotTexture(Class<? extends Attribute> clazz){
		String slot = "slot00";
		if(clazz == Speed.class) {
			slot = "slot03";
		} else if(clazz == FuelCapacity.class){
			slot = "slot01";
		} else if(clazz == Luck.class){
			slot = "slot06";
		} else if(clazz == ShieldCapacity.class){
			slot = "slot02";
		} else if(clazz == LandingDistance.class){
			slot = "slot04";
		} else if(clazz == ItemCollectRadius.class){
			slot = "slot09";
		} else if(clazz == Inertia.class){
			slot = "slot05";
		}
		return App.TEXTURES.findRegion(slot);
	}

	public String name(){
		return getName(getClass());
	}

	public static TextureRegion getPropertiesTexture(Class<? extends Attribute> clazz){
		String row = "row00";
		if(clazz == Speed.class) {
			row = "row03";
		} else if(clazz == FuelCapacity.class){
			row = "row01";
		} else if(clazz == Luck.class){
			row = "row06";
		} else if(clazz == ShieldCapacity.class){
			row = "row02";
		} else if(clazz == LandingDistance.class){
			row = "row04";
		} else if(clazz == ItemCollectRadius.class){
			row = "row09";
		} else if(clazz == Inertia.class){
			row = "row05";
		}
		return App.TEXTURES.findRegion(row);
	}

	public String getDescription() {
		return getDescription(getClass());
	}

	public static String getDescription(Class<? extends Attribute> clazz){

		return "Description of: " + clazz.getSimpleName().toUpperCase();
	}


	public static String getName(Class<? extends Attribute> clazz) {
		String name = "?";
		if(clazz == Speed.class) {
			name = "SPEED";
		} else if(clazz == FuelCapacity.class){
			name = "MAXIMUM FUEL";
		} else if(clazz == Luck.class){
			name = "LUCK";
		} else if(clazz == ShieldCapacity.class){
			name = "MAXIMUM SHIELD";
		} else if(clazz == LandingDistance.class){
			name = "LANDING DISTANCE";
		} else if(clazz == ItemCollectRadius.class){
			name = "ITEM COLLECT RADIUS";
		} else if(clazz == Inertia.class){
			name = "INERTIA";
		}

		return name;
	}
}
