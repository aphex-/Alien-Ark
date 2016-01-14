package com.nukethemoon.libgdxjam;

public class ArtifactDefinitions {

	public enum ConcreteArtifactType {
		ATTRIBUTE_SPEED,
		ATTRIBUTE_INERTIA,
		ATTRIBUTE_LANDSLIDE,
		ATTRIBUTE_FUEL_CAPACITY,
		ATTRIBUTE_SHIELD_CAPACITY,
		ATTRIBUTE_SCAN_RADIUS,
		ATTRIBUTE_LUCK,
		OPERATOR_PLUS,
		OPERATOR_MINUS,
		OPERATOR_MULTIPLY,
		OPERATOR_DIVIDE,
		VALUE_100,
		VALUE_200,
		VALUE_500,
		VALUE_1000,
		VALUE_2000;


		public static ConcreteArtifactType byName(String name) {
			for (ConcreteArtifactType t : ConcreteArtifactType.values()) {
				if (t.name().toLowerCase().equals(name.toLowerCase())) {
					return t;
				}
			}
			return null;
		}
	}
}
