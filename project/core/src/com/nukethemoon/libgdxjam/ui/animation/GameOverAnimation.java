package com.nukethemoon.libgdxjam.ui.animation;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.libgdxjam.screens.planet.OverlayRenderer;
import com.nukethemoon.tools.ani.BaseAnimation;

public class GameOverAnimation extends BaseAnimation {

	private Table table;
	private OverlayRenderer overlayRenderer;

	public GameOverAnimation(Table table, OverlayRenderer overlayRenderer) {
		super(1000);
		this.overlayRenderer = overlayRenderer;
	}

	@Override
	protected void onProgress(float v) {
		overlayRenderer.setColor(0, 0, 0, v);
	}

	@Override
	protected void onFinish() {
	}

	@Override
	protected void onStart() {
		overlayRenderer.setEnabled(true);
	}
}
