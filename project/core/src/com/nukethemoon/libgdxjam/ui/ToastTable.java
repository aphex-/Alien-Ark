package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.tools.ani.AnimationFinishedListener;
import com.nukethemoon.tools.ani.BaseAnimation;

public class ToastTable extends CenteredTable {

	private Label label;

	public ToastTable(Skin uiSkin) {
		setSkin(uiSkin);
		label = new Label("", uiSkin);
		add(label);
		pack();
	}

	public void setText(String text) {
		label.setText(text);
		pack();
	}

	public static class ShowToastAnimation extends BaseAnimation {

		private ToastTable toast;

		public ShowToastAnimation(ToastTable toast, AnimationFinishedListener listener) {
			super(1500, listener);
			this.toast = toast;
		}

		@Override
		protected void onProgress(float v) {
			toast.setPosition(toast.getX(), toast.getY() + 1);
		}
	}
}
