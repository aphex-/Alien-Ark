package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.ui.DialogTable;
import com.nukethemoon.tools.ani.Ani;

import java.util.ArrayList;
import java.util.List;

public class TutorialHelper {

	private Stage stage;
	private final Ani ani;
	private boolean enabled;

	private List<DialogTable> tutorialDialogs = new ArrayList<DialogTable>();

	private int currentStep = -1;

	private long totalKeyEvents = 0;

	private boolean pressedLeft = false;
	private boolean pressedTop = false;
	private boolean pressedRight = false;
	private boolean pressedDown = false;

	public TutorialHelper(Stage stage, Skin skin, Ani ani) {
		this.stage = stage;
		this.ani = ani;
		enabled = App.save.showTutorial;

		tutorialDialogs.add(new DialogTable(skin, new String[] {
			"This is your first flight?",
			"Who gave a rocket to a beginner?",
			"Anyway.. lets make the best out",
			"of it. " +
			"I will show you the basics of",
			"the modern space flight controls.",
			"Simply use your CURSOR KEYS or",
			"W, A, S and D to change",
			"the direction."
		}));

		next();
	}

	public void next() {
		if (enabled) {
			closeCurrent();
			currentStep++;
			if (currentStep >= 0 && currentStep < tutorialDialogs.size()) {
				DialogTable dialogTable = tutorialDialogs.get(currentStep);
				stage.addActor(dialogTable);
				ani.add(dialogTable.createAnimation());
			}
		}
	}

	private void closeCurrent() {
		if (currentStep >= 0 && currentStep < tutorialDialogs.size()) {
			DialogTable dialogTable = tutorialDialogs.get(currentStep);
			if (dialogTable != null) {
				dialogTable.remove();
			}
		}
	}


	public void onKeyTyped(int keyCode) {
		if (enabled) {
			totalKeyEvents++;

			if (currentStep == 0) {
				if (keyCode == Input.Keys.LEFT || keyCode == Input.Keys.A) {
					pressedLeft = true;
				}
				if (keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.D) {
					pressedRight = true;
				}
				if (keyCode == Input.Keys.UP || keyCode == Input.Keys.W) {
					pressedTop = true;
				}
				if (keyCode == Input.Keys.DOWN || keyCode == Input.Keys.S) {
					pressedDown = true;
				}
				if (pressedDown && pressedRight && pressedTop && pressedLeft) {
					closeCurrent();
					if (totalKeyEvents > 10) {
						next();
					}
				}
			}
		}

	}
}
