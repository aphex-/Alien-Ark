package com.nukethemoon.libgdxjam.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;


public class DebugCameraInput implements InputProcessor {

	private PerspectiveCamera camera;

	public DebugCameraInput(PerspectiveCamera camera) {
		this.camera = camera;
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == 51) {
			// W
			camera.position.y = camera.position.y + 1;
		}

		if (keycode == 29) {
			// A
			camera.position.x = camera.position.x - 1;
		}

		if (keycode == 47) {
			// S
			camera.position.y = camera.position.y - 1;
		}

		if (keycode == 32) {
			// D
			camera.position.x = camera.position.x + 1;
		}

		if (keycode == 45) {
			// Q
			camera.position.z = camera.position.z + 1;
		}


		if (keycode == 33) {
			// E
			camera.position.z = camera.position.z - 1;
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.position.z = camera.position.z + amount;
		return false;
	}
}
