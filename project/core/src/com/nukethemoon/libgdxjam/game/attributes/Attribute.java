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
			slot = "slot03";
		} else if(clazz == MaxFuel.class){
			slot = "slot01";
		} else if(clazz == Luck.class){
			slot = "slot06";
		} else if(clazz == Shield.class){
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
}
