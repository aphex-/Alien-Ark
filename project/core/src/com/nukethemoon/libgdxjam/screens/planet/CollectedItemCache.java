package com.nukethemoon.libgdxjam.screens.planet;

import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

import java.awt.*;
import java.util.*;

public class CollectedItemCache {

	public static final int CACHE_EXPIRE_DISTANCE = 100;

	private java.util.List<Point> collectedFuelPositions = new ArrayList<Point>();
	private java.util.List<Point> collectedShieldPositions = new ArrayList<Point>();

	private java.util.List<Point> tmpRemoveList = new ArrayList<Point>();
	private Point tmpPoint = new Point();

	public void registerCollected(int chunkX, int chunkY, CollisionTypes type) {
		Point point = new Point(chunkX, chunkY);
		if (type == CollisionTypes.FUEL) {
			if (!collectedFuelPositions.contains(point)) {
				collectedFuelPositions.add(point);
			}
		}
		if (type == CollisionTypes.SHIELD) {
			if (!collectedShieldPositions.contains(point)) {
				collectedShieldPositions.add(point);
			}
		}
	}


	public void update(int currentChunkX, int currentChunkY) {
		tmpPoint.setLocation(currentChunkX, currentChunkY);

		for (Point p : collectedFuelPositions) {
			if (p.distance(tmpPoint) > CACHE_EXPIRE_DISTANCE) {
				tmpRemoveList.add(p);
			}
		}

		for (Point p : tmpRemoveList) {
			collectedFuelPositions.remove(p);
		}

		for (Point p : collectedShieldPositions) {
			if (p.distance(tmpPoint) > CACHE_EXPIRE_DISTANCE) {
				tmpRemoveList.add(p);
			}
		}

		for (Point p : tmpRemoveList) {
			collectedShieldPositions.remove(p);
		}
	}

	public boolean isFuelCollected(int chunkX, int chunkY) {
		tmpPoint.setLocation(chunkX, chunkY);
		return collectedFuelPositions.contains(tmpPoint);
	}

	public boolean isShieldCollected(int chunkX, int chunkY) {
		tmpPoint.setLocation(chunkX, chunkY);
		return collectedShieldPositions.contains(tmpPoint);
	}
}
