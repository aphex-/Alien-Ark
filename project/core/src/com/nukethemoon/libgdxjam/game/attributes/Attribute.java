package com.nukethemoon.libgdxjam.game.attributes;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nukethemoon.libgdxjam.App;

public abstract class Attribute {

	private float currentValue;

	public float getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
	}

	public static TextureRegion getSlotTexture(Class<? extends Attribute> clazz) {
		String slot = "slot00";
		if (clazz == EnginePower.class) {
			slot = "attributeSpeed";
		} else if (clazz == FuelCapacity.class) {
			slot = "attributeFuelCapacity";
		} else if (clazz == Luck.class) {
			slot = "attributeLuck";
		} else if (clazz == ShieldCapacity.class) {
			slot = "attributeShieldCapacity";
		} else if (clazz == Landslide.class) {
			slot = "attributeLandingDistance";
		} else if (clazz == ScanRadius.class) {
			slot = "attributeItemCollectRadius";
		} else if (clazz == Inertia.class) {
			slot = "attributeTraegheit";
		}
		return App.TEXTURES.findRegion(slot);
	}

	public String name() {
		return getName(getClass());
	}

	public static TextureRegion getPropertiesTexture(Class<? extends Attribute> clazz) {
		String row = "row00";
		if (clazz == EnginePower.class) {
			row = "row03";
		} else if (clazz == FuelCapacity.class) {
			row = "row01";
		} else if (clazz == Luck.class) {
			row = "row02";
		} else if (clazz == ShieldCapacity.class) {
			row = "row04";
		} else if (clazz == Landslide.class) {
			row = "row05";
		} else if (clazz == ScanRadius.class) {
			row = "row07";
		} else if (clazz == Inertia.class) {
			row = "row06";
		}
		return App.TEXTURES.findRegion(row);
	}

	public String getDescription() {
		return getDescription(getClass());
	}

	public static String getDescription(Class<? extends Attribute> clazz) {

		return "Description of: " + clazz.getSimpleName().toUpperCase();
	}


	public static String getName(Class<? extends Attribute> clazz) {
		String name = "?";
		if (clazz == EnginePower.class) {
			name = "ENGINE POWER";
		} else if (clazz == FuelCapacity.class) {
			name = "FUEL CAPACITY";
		} else if (clazz == Luck.class) {
			name = "LUCK";
		} else if (clazz == ShieldCapacity.class) {
			name = "SHIELD CAPACITY";
		} else if (clazz == Landslide.class) {
			name = "LANDSLIDE";
		} else if (clazz == ScanRadius.class) {
			name = "SCAN RADIUS";
		} else if (clazz == Inertia.class) {
			name = "INERTIA";
		}

		return name;
	}

	public static TextureRegion getAlienTexture(Class<? extends Attribute> clazz) {
		String row = "slot00";
		if (clazz == EnginePower.class) {
			row = "alienSpeed";
		} else if (clazz == FuelCapacity.class) {
			row = "alienFuelCapacity";
		} else if (clazz == Luck.class) {
			row = "alienLuck";
		} else if (clazz == ShieldCapacity.class) {
			row = "alienShieldCapacity";
		} else if (clazz == Landslide.class) {
			row = "alienLandingDistance";
		} else if (clazz == ScanRadius.class) {
			row = "alienItemCollectRadius";
		} else if (clazz == Inertia.class) {
			row = "alienTraegheit";
		}
		return App.TEXTURES.findRegion(row);
	}
}
