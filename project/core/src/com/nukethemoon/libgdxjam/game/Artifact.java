package com.nukethemoon.libgdxjam.game;


public abstract class Artifact {
	private static int nextID;
	public final int ID = ++nextID;

	@Override
	public int hashCode() {
		return ID;
	}
}
