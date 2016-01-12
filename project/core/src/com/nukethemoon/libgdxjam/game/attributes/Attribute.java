package com.nukethemoon.libgdxjam.game.attributes;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;

public abstract class Attribute {

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
			slot = "attributeSpeed";
		} else if(clazz == MaxFuel.class){
			slot = "attributeFuelCapacity";
		} else if(clazz == Luck.class){
			slot = "attributeLuck";
		} else if(clazz == Shield.class){
			slot = "attributeShieldCapacity";
		} else if(clazz == LandingDistance.class){
			slot = "attributeLandingDistance";
		} else if(clazz == ItemCollectRadius.class){
			slot = "attributeItemCollectRadius";
		} else if(clazz == Inertia.class){
			slot = "attributeTraegheit";
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
		} else if(clazz == MaxFuel.class){
			row = "row01";
		} else if(clazz == Luck.class){
			row = "row02";
		} else if(clazz == Shield.class){
			row = "row04";
		} else if(clazz == LandingDistance.class){
			row = "row05";
		} else if(clazz == ItemCollectRadius.class){
			row = "row07";
		} else if(clazz == Inertia.class){
			row = "row06";
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
		} else if(clazz == MaxFuel.class){
			name = "MAXIMUM FUEL";
		} else if(clazz == Luck.class){
			name = "LUCK";
		} else if(clazz == Shield.class){
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
