package com.nukethemoon.libgdxjam;

import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Decrease;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Divide;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Multiply;
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Inertia;
import com.nukethemoon.libgdxjam.game.attributes.ItemCollectRadius;
import com.nukethemoon.libgdxjam.game.attributes.LandingDistance;
import com.nukethemoon.libgdxjam.game.attributes.Luck;
import com.nukethemoon.libgdxjam.game.attributes.ShieldCapacity;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

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

		public Artifact createArtifact() {
			if (this == ATTRIBUTE_SPEED) {
				return new AttributeArtifact(Speed.class);
			}
			if (this == ATTRIBUTE_INERTIA) {
				return new AttributeArtifact(Inertia.class);
			}
			if (this == ATTRIBUTE_LANDSLIDE) {
				return new AttributeArtifact(LandingDistance.class);
			}
			if (this == ATTRIBUTE_FUEL_CAPACITY) {
				return new AttributeArtifact(FuelCapacity.class);
			}
			if (this == ATTRIBUTE_SHIELD_CAPACITY) {
				return new AttributeArtifact(ShieldCapacity.class);
			}
			if (this == ATTRIBUTE_SCAN_RADIUS) {
				return new AttributeArtifact(ItemCollectRadius.class);
			}
			if (this == ATTRIBUTE_LUCK) {
				return new AttributeArtifact(Luck.class);
			}
			if (this == OPERATOR_PLUS) {
				return new Increase();
			}
			if (this == OPERATOR_MINUS) {
				return new Decrease();
			}
			if (this == OPERATOR_MULTIPLY) {
				return new Multiply();
			}
			if (this == OPERATOR_DIVIDE) {
				return new Divide();
			}
			if (this == VALUE_100) {
				return new ValueArtifact(100);
			}
			if (this == VALUE_200) {
				return new ValueArtifact(200);
			}
			if (this == VALUE_500) {
				return new ValueArtifact(500);
			}
			if (this == VALUE_1000) {
				return new ValueArtifact(1000);
			}
			if (this == VALUE_2000) {
				return new ValueArtifact(2000);
			}
			return null;
		}
	}
}
