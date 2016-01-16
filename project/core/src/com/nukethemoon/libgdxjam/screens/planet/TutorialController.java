package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
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
		CRAFT,
		END_TUTORIAL
	}

	private Map<TutorialStep, DialogTable> tutorialDialogs = new HashMap<TutorialStep, DialogTable>();

	private TutorialStep currentStep = TutorialStep.NULL;

	private long totalKeyEvents = 0;

	private int solarScreenCalls = 0;
	private int planetScreenCalls = 0;
	private int arkScreenCalls = 0;

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
				"Travel to the marked planet",
				"and enter it. Avoid falling",
				"into the star."
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


		tutorialDialogs.put(TutorialStep.LEAVE_THE_PLANET, new DialogTable(skin, new String[]{
				"Perfect! You found an ARTIFACT.",
				"You can use it to craft new crew",
				"members and improve your ship.",
				"Lets leave the planet through",
				"the PLANET PORTAL.",
				"Press SPACE to start your engines."
		}, "TUTORIAL 05"));


		tutorialDialogs.put(TutorialStep.OPEN_COMMAND_CENTER, new DialogTable(skin, new String[]{
				"Alright. Your fuel is filled up",
				"now.",
				"Open the COMMAND CENTER by",
				"pressing the button called",
				"'command center button'.",
		}, "TUTORIAL 06"));


		tutorialDialogs.put(TutorialStep.CRAFT, new DialogTable(skin, new String[]{
				"There is your collected ARTIFACT",
				"It can improve the FUEL CAPACITY",
				"of the rocket. Luckily you ",
				"have two additional ARTIFACTS",
				"on board. Drag and drop them",
				"into the LAB to craft an ALIEN."
		}, "TUTORIAL 07"));

		tutorialDialogs.put(TutorialStep.END_TUTORIAL, new DialogTable(skin, new String[]{
				"Perfect! Your ship has improved.",
				"If you find ARTIFACTS that",
				"improve the speed you can even",
				"win a race on a planet."
		}, "TUTORIAL END"));
	}

	public void register(Stage stage, Ani ani) {
		this.stage = stage;
		this.ani = ani;
	}

	public void reset() {
		currentStep = TutorialStep.NULL;
		totalKeyEvents = 0;
		solarScreenCalls = 0;
		planetScreenCalls = 0;
		arkScreenCalls = 0;
		pressedLeft = false;
		pressedTop = false;
		pressedRight = false;
		pressedDown = false;
	}

	private void next() {
		if (App.config.tutorialEnabled) {
			closeCurrent();
			if (currentStep.ordinal() + 1 < currentStep.values().length) {
				currentStep = TutorialStep.values()[currentStep.ordinal() + 1];
				dialogTable = tutorialDialogs.get(currentStep);

				if (currentStep == TutorialStep.CRAFT) {
					dialogTable.setPosition(10, dialogTable.getHeight() + 20);
				}

				if (dialogTable != null) {
					stage.addActor(dialogTable);
					ani.add(dialogTable.createAnimation());
				}

				if (currentStep == TutorialStep.SHIP_CONTROLS || currentStep == TutorialStep.REACH_THE_ARTIFACT
						|| currentStep == TutorialStep.COLLECT_THE_ARTIFACT || currentStep ==  TutorialStep.LEAVE_THE_PLANET) {
					SpaceShipProperties.properties.addCurrentInternalFuel(SpaceShipProperties.properties.getFuelCapacity());
				}

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
			if (planetScreenCalls == 0) {
				currentStep = TutorialStep.values()[TutorialStep.SHIP_CONTROLS.ordinal() - 1];
				next();
			}
			planetScreenCalls++;
		}

		if (screenClass == SolarScreen.class) {
			if (solarScreenCalls == 0) {
				currentStep = TutorialStep.values()[TutorialStep.REACH_PLANET.ordinal() - 1];
				next();
			}
			if (solarScreenCalls == 1) {
				currentStep = TutorialStep.values()[TutorialStep.OPEN_COMMAND_CENTER.ordinal() - 1];
				next();
			}
			solarScreenCalls++;
		}

		if (screenClass == ArkScreen.class) {
			if (arkScreenCalls == 0) {
				currentStep = TutorialStep.values()[TutorialStep.CRAFT.ordinal() - 1];
				next();
			}
			arkScreenCalls++;
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

	public void onAlienCrafted() {
		if (App.config.tutorialEnabled) {
			if (currentStep == TutorialStep.CRAFT) {
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
				planetScreenCalls = 1;
			}
		}
	}

	public boolean isForcedPlanet01() {
		return solarScreenCalls == 1 && App.config.tutorialEnabled;
	}
}
