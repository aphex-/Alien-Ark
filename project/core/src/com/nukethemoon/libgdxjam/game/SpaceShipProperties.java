package com.nukethemoon.libgdxjam.game;

import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.OperatorArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Decrease;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.attributes.Speed;

import java.util.ArrayList;
import java.util.List;

public class SpaceShipProperties {

	public static final SpaceShipProperties properties = new SpaceShipProperties(); //TODO!

	private Vector2 position;
	private Vector2 movementVector;
	private float fuelPerUnit = 1f;
	private int maxFuel = 100;
	private int currentFuel;

	private List<Artifact> artifacts = new ArrayList<Artifact>();

	private List<Alien> aliens = new ArrayList<Alien>();

	public void testInit(){
		AttributeArtifact a = new AttributeArtifact(Speed.class);
		ValueArtifact v = new ValueArtifact(10);
		OperatorArtifact o = new Increase();

		artifacts.add(a);
		artifacts.add(v);
		artifacts.add(o);

		Alien alien = Alien.createAlien(a, o, v);

		aliens.add(alien);

		 a = new AttributeArtifact(Speed.class);
		 v = new ValueArtifact(0.5f);
		 o = new Decrease();

		 alien = Alien.createAlien(a, o, v);
		aliens.add(alien);
	}


	public float computeSpeedPerUnit() {

		Speed speed = new Speed();
		for (Alien a:aliens) {
			a.modifySpeed(speed);
		}
		return speed.getCurrentValue();
	}

	public List<Alien> getAliens() {
		return aliens;
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}
}
