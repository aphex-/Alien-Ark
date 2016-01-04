package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class Toast extends Table {

	private Label label;

	public Toast(Skin uiSkin) {
		setSkin(uiSkin);
		label = new Label("", uiSkin);
		add(label);
		pack();
	}

	public void setText(String text) {
		label.setText(text);
		pack();
	}

	@Override
	public void pack() {
		super.pack();
		setPosition(
				(Gdx.graphics.getWidth() / 2) - (getWidth() / 2),
				(Gdx.graphics.getHeight() / 2) - (getHeight() / 2));

	}

	public static class ShowToastAnimation extends BaseAnimation {

		private Toast toast;

		public ShowToastAnimation(Toast toast, AnimationFinishedListener listener) {
			super(1500, listener);
			this.toast = toast;
		}

		@Override
		protected void onProgress(float v) {
			toast.setPosition(toast.getX(), toast.getY() + 1);
		}
	}
}
