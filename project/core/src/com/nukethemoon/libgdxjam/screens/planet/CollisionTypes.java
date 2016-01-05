package com.nukethemoon.libgdxjam.screens.planet;



	public enum CollisionTypes {

		GROUND((short) (1<<8)),
		ROCKET((short) (1<<9)),
		WATER((short) (1<<10)),
		ALL((short)-1);

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
	}

