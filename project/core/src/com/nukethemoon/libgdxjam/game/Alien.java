package com.nukethemoon.libgdxjam.game;

import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

public class Alien {

	private final AttributeArtifact attribute;
	private final OperatorArtifact operator;
	private final ValueArtifact value;

	private Alien(AttributeArtifact attribute, OperatorArtifact operator, ValueArtifact value) {
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}

	//delete this?
	public void modifySpeed(Speed speed) {
		Log.l(SpaceShipProperties.class, "Combining speed " + speed.getCurrentValue() + " with Operator " + operator.getClass().getSimpleName() + " and Value " + value.getValue());
		attribute.apply(speed,operator, value);
	}

	public void modifyAttribute(Attribute attributeToModify) {
		attribute.apply(attributeToModify, operator, value);
	}

	public static Alien createAlien(Artifact... artifacts) {
		//TODO check valid conditions and return "Useless fool" if invalid

		return new Alien(findAttribute(artifacts), findOperator(artifacts), findValue(artifacts));
	}

	private static <T extends Artifact> T find(Artifact[] artifacts, Class<T> clazz){
		for (Artifact a: artifacts) {
			if(clazz.isInstance(a))
				return (T) a;
		}

		return null;
	}

	private static AttributeArtifact findAttribute(Artifact[] artifacts){
		return find(artifacts, AttributeArtifact.class);
	}

	private static OperatorArtifact findOperator(Artifact[] artifacts){
		return find(artifacts, OperatorArtifact.class);
	}

	private static ValueArtifact findValue(Artifact[] artifacts){
		return find(artifacts, ValueArtifact.class);
	}

}
