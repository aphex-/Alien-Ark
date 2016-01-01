package com.nukethemoon.libgdxjam.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;

import java.util.ArrayList;
import java.util.List;


public class SpriteDragAndDropProcessor extends InputAdapter {
	private Vector2 touchPoint = new Vector2(0, 0);

	private Sprite selectedSprite;
	private List<Sprite> allMovableSprites;
	private List<Sprite> lockedSprites = new ArrayList<Sprite>();
	private final OnDroppedListener listener;

	public SpriteDragAndDropProcessor(OnDroppedListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchPoint.set(screenX, Gdx.graphics.getHeight() - screenY);
		selectedSprite = getHitSprite(touchPoint);

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (selectedSprite != null)
			listener.OnDrop(selectedSprite, screenX, screenY);

		selectedSprite = null;

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		touchPoint.set(screenX, Gdx.graphics.getHeight() - screenY);

		if (selectedSprite != null) {
			selectedSprite.setCenter(touchPoint.x, touchPoint.y);
		}

		return true;
	}

	private Sprite getHitSprite(Vector2 touchPoint) {

		for (Sprite s : allMovableSprites) {
			if (s.getBoundingRectangle().contains(touchPoint) && !isLocked(s)) {
				return s;
			}
		}

		return null;
	}

	public boolean isLocked(Sprite sprite) {
		return lockedSprites.contains(sprite);
	}

	public void setAllMovableSprites(List<Sprite> allMovableSprites) {
		this.allMovableSprites = allMovableSprites;
	}

	public void lock(Sprite sprite) {
		lockedSprites.add(sprite);
	}

	public interface OnDroppedListener {
		void OnDrop(Sprite droppedSprite, int x, int y);
	}
}
