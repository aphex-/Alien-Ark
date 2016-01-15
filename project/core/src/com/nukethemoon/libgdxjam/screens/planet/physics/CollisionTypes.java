package com.nukethemoon.libgdxjam.screens.planet.physics;


import java.util.HashMap;
import java.util.Map;

public enum CollisionTypes {

	GROUND(				(short) (1<< 8)),
	ROCKET(				(short) (1<< 9)),
	WATER(				(short) (1<<10)),
	FUEL(				(short) (1<<11)),
	SHIELD(				(short) (1<<12)),
	PORTAL_EXIT(		(short) (1<<13)),
	WAY_POINT_TRIGGER(	(short) (1<<14)),
	NOTHING(			(short) 0);

	public static Map<CollisionTypes, CollisionTypes[]> TYPE_TO_COLLISIONS = new HashMap<CollisionTypes, CollisionTypes[]>();

	// collision filtering
	static {
		TYPE_TO_COLLISIONS.put(ROCKET, 				new CollisionTypes[] {GROUND, WATER, FUEL, SHIELD, PORTAL_EXIT, WAY_POINT_TRIGGER});
		TYPE_TO_COLLISIONS.put(GROUND,	 			new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(WATER, 				new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(FUEL, 				new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(SHIELD, 				new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(PORTAL_EXIT,			new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(WAY_POINT_TRIGGER,	new CollisionTypes[] {ROCKET});
	}

	public short mask;

	CollisionTypes(short mask) {
		this.mask = mask;
	}

	public static CollisionTypes byName(String name) {
		for (CollisionTypes t : CollisionTypes.values()) {
			if (t.name().toLowerCase().equals(name.toLowerCase())) {
				return t;
			}
		}
		return null;
	}

	public static CollisionTypes byMask(short mask) {
		for (CollisionTypes t : CollisionTypes.values()) {
			if (t.mask == mask) {
				return t;
			}
		}
		return null;
	}

	public static CollisionTypes[] getColliders(CollisionTypes type) {
		return TYPE_TO_COLLISIONS.get(type);
	}

	public static short toMask(CollisionTypes... types) {
		short returnMask = NOTHING.mask;
		if (types == null) {
			return returnMask;
		}
		for (CollisionTypes t : types) {
			returnMask = (short) (returnMask | t.mask);
		}
		return returnMask;
	}
}

