package com.nukethemoon.libgdxjam.screens.planet.devtools;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class DevelopmentPlacementRenderer implements Disposable {


	private final Model cursorModel;
	private final ModelInstance cursorModelInstance;
	private Vector3 tmpVector = new Vector3();
	private CursorChangeListener listener;

	public DevelopmentPlacementRenderer() {
		ModelBuilder modelBuilder = new ModelBuilder();
		cursorModel = modelBuilder.createSphere(1f, 1f, 1f, 8, 8, new Material(), VertexAttributes.Usage.Position
				| VertexAttributes.Usage.ColorUnpacked
				| VertexAttributes.Usage.Normal);

		cursorModelInstance = new ModelInstance(cursorModel);
	}

	public void render(ModelBatch batch) {
		batch.render(cursorModelInstance);
	}

	public void setCursorPosition(Vector3 position) {
		cursorModelInstance.transform.setToTranslation(position);
		if (listener != null) {
			listener.onCursorPositionChange(position);
		}
	}

	public Vector3 getCursorPosition() {
		return cursorModelInstance.transform.getTranslation(tmpVector);
	}

	public void setCursorChangeListener(CursorChangeListener cursorChangeListener) {
		this.listener = cursorChangeListener;
	}

	@Override
	public void dispose() {
	}

	public interface CursorChangeListener {
		void onCursorPositionChange(Vector3 position);
	}
}
