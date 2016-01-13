package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;
import com.nukethemoon.libgdxjam.screens.solar.SolarScreen;
import com.nukethemoon.libgdxjam.ui.DialogTable;
import com.nukethemoon.tools.ani.Ani;

import java.util.HashMap;
import java.util.Map;

public class TutorialController {

	private Stage stage;
	private Ani ani;
	private DialogTable dialogTable;

	public enum TutorialStep {
		NULL,
		REACH_PLANET,
		SHIP_CONTROLS,
		REACH_THE_ARTIFACT,
		COLLECT_THE_ARTIFACT,
		LEAVE_THE_PLANET,
		OPEN_COMMAND_CENTER,
		CRAFT_AN_ALIEN,
		END_TUTORIAL
	}

	private Map<TutorialStep, DialogTable> tutorialDialogs = new HashMap<TutorialStep, DialogTable>();

	private TutorialStep currentStep = TutorialStep.NULL;

	private long totalKeyEvents = 0;

	private int solarScreenStep = 0;
	private int planetScreenStep = 0;
	private int arkScreenStep = 0;

	private boolean pressedLeft = false;
	private boolean pressedTop = false;
	private boolean pressedRight = false;
	private boolean pressedDown = false;

	private Vector2 tmpVector = new Vector2();

	public TutorialController(Skin skin) {


		tutorialDialogs.put(TutorialStep.REACH_PLANET, new DialogTable(skin, new String[]{
				"Hello. This is Tutorianto",
				"speaking... Let me help you",
				"getting into this LIFE IN SPACE.",
				"Navigate your ship using the",
				"CURSOR KEYS or W, A, S and D.",
				"Travel to a planet and avoid",
				"to fall into the Star."
		}, "TUTORIAL 01"));

		tutorialDialogs.put(TutorialStep.SHIP_CONTROLS, new DialogTable(skin, new String[]{
				"This is your first flight?",
				"Who gave a rocket to a beginner?",
				"Anyway.. lets make the best out",
				"of it. " +
						"I will show you the basics of",
				"the modern space flight controls.",
				"Simply use your CURSOR KEYS or",
				"W, A, S and D to change",
				"the direction."
		}, "TUTORIAL 02"));

		tutorialDialogs.put(TutorialStep.REACH_THE_ARTIFACT, new DialogTable(skin, new String[]{
				"Take a look at the mini map.",
				"It shows you the PLANET PORTAL to",
				"leave the planet and the nearest",
				"ARTIFACT from your current",
				"position.",
				"Fly to this ARTIFACT using your",
				"new flight skills."
		}, "TUTORIAL 03"));

		tutorialDialogs.put(TutorialStep.COLLECT_THE_ARTIFACT, new DialogTable(skin, new String[]{
				"Respect! I underestimated your",
				"skills. Now try to land your",
				"ship close to the ARTIFACT to",
				"collect it.",
				"Press SPACE to stop the engines",
				"and start the landing manoeuvre.",
				"Let the rocket come to a halt",
				"on solid ground.",
				"Press SPACE again start the",
				"engines if you miss the right spot."
		}, "TUTORIAL 04"));

		tutorialDialogs.put(TutorialStep.COLLECT_THE_ARTIFACT, new DialogTable(skin, new String[]{
				"Respect! I underestimated your",
				"skills. Now try to land your",
				"ship close to the ARTIFACT to",
				"collect it.",
				"Press SPACE to stop the engines",
				"and start the landing manoeuvre.",
				"Let the rocket come to a halt",
				"on solid ground.",
				"Press SPACE again to start the",
				"engines if you miss the right spot."
		}, "TUTORIAL 05"));

		tutorialDialogs.put(TutorialStep.LEAVE_THE_PLANET, new DialogTable(skin, new String[]{
				"Perfect! You found an ARTIFACT.",
				"You can use it to craft new crew",
				"members and improve your ship.",
				"Lets leave the planet through",
				"the PLANET PORTAL.",
				"Press SPACE to start your engines."
		}, "TUTORIAL 06"));


		tutorialDialogs.put(TutorialStep.OPEN_COMMAND_CENTER, new DialogTable(skin, new String[]{
				"Alright. Your progress is saved",
				"and the fuel is filled up now.",
				"Open the COMMAND CENTER by",
				"pressing the button called",
				"'command center button'.",
		}, "TUTORIAL 07"));


		tutorialDialogs.put(TutorialStep.CRAFT_AN_ALIEN, new DialogTable(skin, new String[]{
				"The ARTIFACTS you collected are",
				"organic life forms.",
				"With the lab that is on board you can",
				"easily hybridize them into fully-fletched ALIENS!",
				"Drag & Drop three ARTIFACTS into the LAB."
		}, "TUTORIAL 08"));

		tutorialDialogs.put(TutorialStep.END_TUTORIAL, new DialogTable(skin, new String[]{
				"The end of the tutorial",
		}, "TUTORIAL 09"));
	}

	public void register(Stage stage, Ani ani) {
		this.stage = stage;
		this.ani = ani;
	}

	private void next() {
		if (App.config.tutorialEnabled) {
			closeCurrent();
			if (currentStep.ordinal() + 1 < currentStep.values().length) {
				currentStep = TutorialStep.values()[currentStep.ordinal() + 1];
				dialogTable = tutorialDialogs.get(currentStep);

				stage.addActor(dialogTable);
				ani.add(dialogTable.createAnimation());
			}
			if (currentStep == TutorialStep.END_TUTORIAL) {
				App.config.tutorialEnabled = false;
				App.saveConfig();
			}
		}
	}

	public void toFront() {
		if (dialogTable != null) {
			dialogTable.toFront();
		}
	}

	public void nextStepFor(Class<? extends Screen> screenClass) {
		if (!App.config.tutorialEnabled) {
			return;
		}

		if (screenClass == PlanetScreen.class) {
			if (planetScreenStep == 0) {
				currentStep = TutorialStep.values()[TutorialStep.SHIP_CONTROLS.ordinal() - 1];
				next();
			}
		}

		if (screenClass == SolarScreen.class) {
			if (solarScreenStep == 0) {
				currentStep = TutorialStep.values()[TutorialStep.REACH_PLANET.ordinal() - 1];
				next();
			}
			if (solarScreenStep == 1) {
				currentStep = TutorialStep.values()[TutorialStep.OPEN_COMMAND_CENTER.ordinal() - 1];
				next();
			}
		}

		if (screenClass == ArkScreen.class) {
			if (arkScreenStep == 0) {
				currentStep = TutorialStep.values()[TutorialStep.CRAFT_AN_ALIEN.ordinal() - 1];
				next();
			}
		}
	}

	private void closeCurrent() {
		if (currentStep != TutorialStep.NULL) {
			DialogTable dialogTable = tutorialDialogs.get(currentStep);
			if (dialogTable != null) {
				dialogTable.remove();
			}
		}
	}

	public void onKeyTyped(int keyCode) {
		if (App.config.tutorialEnabled) {
			totalKeyEvents++;
			if (currentStep == TutorialStep.SHIP_CONTROLS) {
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
					if (totalKeyEvents > 8) {
						next();
					}
				}
			}
		}

	}

	public void applyNearestArtifact(Vector2 nearestArtifactPosition, Vector3 position) {
		if (App.config.tutorialEnabled) {
			if (currentStep == TutorialStep.REACH_THE_ARTIFACT && nearestArtifactPosition != null) {
				float len = tmpVector.set(nearestArtifactPosition).sub(position.x, position.y).len();
				if (len < 170) {
					next();
				}
			}
		}
	}

	public void onArkEntered() {
		if (App.config.tutorialEnabled) {
			if (currentStep == TutorialStep.CRAFT_AN_ALIEN) {
				next();
			}
		}
	}

	public void onArtifactCollected() {
		if (App.config.tutorialEnabled) {
			if (currentStep == TutorialStep.COLLECT_THE_ARTIFACT) {
				next();
			}
		}
	}

	public void onLeavePlanet() {
		if (App.config.tutorialEnabled) {
			if (currentStep.ordinal() < TutorialStep.LEAVE_THE_PLANET.ordinal()) {
				currentStep = TutorialStep.LEAVE_THE_PLANET;
				planetScreenStep = 1;
			}
		}
	}
}
