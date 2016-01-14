package com.nukethemoon.libgdxjam.screens.planet;

public class ObjectPlacementInfo {

	public float x;
	public float y;

	public float groundOffsetZ = 0;

	public float rotationX = 0;
	public float rotationY = 0;
	public float rotationZ = 0;

	public String id;
	public String type;

	public ObjectPlacementInfo(float x, float y, String id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
}
