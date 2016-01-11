package com.nukethemoon.libgdxjam.ui.animation;

import com.badlogic.gdx.graphics.Color;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.ui.DialogTable;
import com.nukethemoon.tools.ani.BaseAnimation;

public class TableHighlightAnimation extends BaseAnimation {

	private DialogTable table;
	private Color color;

	public TableHighlightAnimation(DialogTable table) {
		super(500);
		this.table = table;
		color = new Color();
		setLoopLength(4);
		App.audioController.playSound("dialogHighlight.mp3");
	}

	@Override
	protected void onProgress(float v) {
		color.set(1 * v, 1 * v, 1 * v, 1);
		table.setColor(color);
	}

	@Override
	protected void onLoopStart(int pLoopIndex) {
		App.audioController.playSound("dialogHighlight.mp3");
	}

	@Override
	protected void onFinish() {
		table.setColor(Color.WHITE);
	}
}
