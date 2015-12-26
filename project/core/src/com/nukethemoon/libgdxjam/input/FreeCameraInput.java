package com.nukethemoon.libgdxjam.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class FreeCameraInput implements InputProcessor {

	private PerspectiveCamera camera;
	private Vector3 tmpVector = new Vector3();

	private boolean forwardPressed = false;
	private boolean backwardPressed = false;
	private boolean leftPressed = false;
	private boolean rightPressed = false;

	private boolean upPressed = false;
	private boolean downPressed = false;

	private boolean enabled = false;

	private Vector2 lastDragPosition = new Vector2();

	public FreeCameraInput(PerspectiveCamera camera) {
		this.camera = camera;
	}



	@Override
	public boolean keyDown(int keycode) {
		if (!enabled) {
			return false;
		}
		if (keycode == 51) {
			// W
			forwardPressed = true;
		}

		if (keycode == 29) {
			// A
			leftPressed = true;

		}

		if (keycode == 47) {
			// S
			backwardPressed = true;
		}

		if (keycode == 32) {
			// D
			rightPressed = true;
		}

		if (keycode == 46) {
			// R
			upPressed = true;
		}

		if (keycode == 34) {
			// F
			downPressed = true;
		}

		//private boolean upPressed = false;
		//private boolean downPressed = false;

		/*if (keycode == 45) {
			// Q
			camera.position.z = camera.position.z + 1;
		}


		if (keycode == 33) {
			// E
			camera.position.z = camera.position.z - 1;
		}*/

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (!enabled) {
			return false;
		}
		if (keycode == 51) {
			// W
			forwardPressed = false;
		}

		if (keycode == 29) {
			// A
			leftPressed = false;
		}

		if (keycode == 47) {
			// S
			backwardPressed = false;
		}

		if (keycode == 32) {
			// D
			rightPressed = false;
		}

		if (keycode == 46) {
			// R
			upPressed = false;
		}

		if (keycode == 34) {
			// F
			downPressed = false;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (!enabled) {
			return false;
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!enabled) {
			return false;
		}
		lastDragPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!enabled) {
			return false;
		}
		lastDragPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!enabled) {
			return false;
		}
		float diffX = (screenX - lastDragPosition.x) * -1;
		float diffY = (screenY - lastDragPosition.y);
		camera.rotate(diffX / 5, 0, 0, 1);
		camera.rotate(diffY / 5, 1, 0, 0);

		lastDragPosition.set(screenX, screenY);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!enabled) {
			return false;
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (!enabled) {
			return false;
		}

		if (amount > 0) {
			camera.position.set(camera.position.x, camera.position.y, camera.position.z + 1);
		} else {
			camera.position.set(camera.position.x, camera.position.y, camera.position.z - 1);
		}


		return false;
	}


	public void update(float delta) {
		if (!enabled) {
			return;
		}

		if (forwardPressed) {
			tmpVector.set(camera.direction);
			tmpVector.nor();
			camera.position.add(tmpVector);
		}
		if (backwardPressed) {
			tmpVector.set(camera.direction);
			tmpVector.nor().scl(-1);
			camera.position.add(tmpVector);
		}


		if (upPressed) {
			camera.position.set(camera.position.x, camera.position.y, camera.position.z + 1);
		}

		if (downPressed) {
			camera.position.set(camera.position.x, camera.position.y, camera.position.z - 1);
		}

		camera.up.set(0, 0, 1);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
