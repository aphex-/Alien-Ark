package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

	public void modifyAttribute(Attribute attributeToModify) {
		attribute.apply(attributeToModify, operator, value);
	}

	public static void createAlien(Artifact... artifacts) {

		AttributeArtifact attribute = findAttribute(artifacts);
		OperatorArtifact operator = findOperator(artifacts);
		ValueArtifact value = findValue(artifacts);
		Alien a = new Alien(attribute, operator, value);
		SpaceShipProperties.properties.getAliens().add(a);
		SpaceShipProperties.properties.getArtifacts().remove(attribute);
		SpaceShipProperties.properties.getArtifacts().remove(operator);
		SpaceShipProperties.properties.getArtifacts().remove(value);

		SpaceShipProperties.properties.computeProperties();
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

	public TextureRegion getTexture(){
		if(attribute == null)
			return null;

		return attribute.getBackgroundTexture();
	}

	public String getDescription(){
		return operator.getAlienVerb() + " your " + attribute.getName() + " by " + value.getValue();
	}

}
