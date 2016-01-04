package com.nukethemoon.libgdxjam.ui.animation;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.ani.BaseAnimation;

public class FadeTableAnimation extends BaseAnimation {

	private Table table;

	public FadeTableAnimation(Table table) {
		super(1000);
		this.table = table;
	}

	@Override
	protected void onProgress(float v) {
		table.setColor(1, 1, 1, v);
	}
}
