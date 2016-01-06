package com.nukethemoon.libgdxjam.screens.planet.helper;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class StandardMotionState extends btMotionState {
	private Matrix4 transform;

	public StandardMotionState(Matrix4 transform) {
		this.transform = transform;
	}
	@Override
	public void getWorldTransform (Matrix4 worldTrans) {
		worldTrans.set(transform);
	}
	@Override
	public void setWorldTransform (Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
}
