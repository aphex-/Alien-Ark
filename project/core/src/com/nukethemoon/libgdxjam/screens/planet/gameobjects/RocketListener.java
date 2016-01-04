package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

public interface RocketListener {

	void onRocketLanded();
	void onRocketLaunched();
	void onRocketDisabledThrust();
	void onRocketEnabledThrust();
	void onRocketDamage();
	void onRocketFuelConsumed();
	void onRocketExploded();

}