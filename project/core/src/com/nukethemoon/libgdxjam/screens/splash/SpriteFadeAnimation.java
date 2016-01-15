package com.nukethemoon.libgdxjam.screens.splash;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class SpriteFadeAnimation extends BaseAnimation {

	private Sprite sprite;

	public SpriteFadeAnimation(Sprite sprite, AnimationFinishedListener listener) {
		super(4000, listener);
		this.sprite = sprite;
	}

	@Override
	protected void onProgress(float v) {
		float v1 = computeIntervalProgress(v, 0f, 0.3f);
		if (v1 >= 0) {
			sprite.setAlpha(v1);
		}

	}

	@Override
	protected void onFinish() {
		sprite.setAlpha(1);
	}
}
