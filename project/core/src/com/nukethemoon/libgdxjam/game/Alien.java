package com.nukethemoon.libgdxjam.game;

import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

public class Alien {

	private final AttributeArtifact attribute;
	private final OperatorArtifact operator;
	private final ValueArtifact value;

	public Alien(AttributeArtifact attribute, OperatorArtifact operator, ValueArtifact value) {
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}


	public void modifySpeed(Speed speed) {
		Log.l(SpaceShipProperties.class, "Combining speed " + speed.getCurrentValue() + " with Operator " + operator.getClass().getSimpleName() + " and Value " + value.getValue());
		attribute.apply(speed,operator, value);
	}
}
