package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class OverlayRenderer {

	private ShapeRenderer shapeRenderer;

	private boolean enabled = false;
	private Color drawColor;

	public OverlayRenderer() {
		shapeRenderer = new ShapeRenderer();
		drawColor = new Color(0, 0, 0, 0);
	}

	public void render() {
		if (enabled && drawColor.a > 0) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.setColor(drawColor.r, drawColor.g, drawColor.b, drawColor.a);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.box(0, 0, 1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setColor(float r, float g, float b, float a) {
		drawColor.set(r, g, b, a);
	}
}
