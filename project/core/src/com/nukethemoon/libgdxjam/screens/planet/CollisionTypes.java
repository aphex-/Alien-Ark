package com.nukethemoon.libgdxjam.screens.planet;


import java.util.HashMap;
import java.util.Map;

public enum CollisionTypes {

	GROUND((short) (1<<8)),
	ROCKET((short) (1<<9)),
	WATER((short) (1<<10)),
	NOTHING((short) 0);

	public static Map<CollisionTypes, CollisionTypes[]> TYPE_TO_COLLISIONS = new HashMap<CollisionTypes, CollisionTypes[]>();

	static {
		TYPE_TO_COLLISIONS.put(ROCKET, new CollisionTypes[] {GROUND, WATER});
		TYPE_TO_COLLISIONS.put(GROUND, new CollisionTypes[] {ROCKET});
		TYPE_TO_COLLISIONS.put(WATER, new CollisionTypes[] {ROCKET});
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

