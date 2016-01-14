package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.planet.PointWithId;
import com.nukethemoon.libgdxjam.screens.planet.devtools.DevelopmentPlacementRenderer;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.PlacedArtifactTable;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;

public class PlacementWindow extends ClosableWindow implements DevelopmentPlacementRenderer.CursorChangeListener, PlacedArtifactTable.ArtifactPlacementListener {

	private Label cursorPositionLabel;
	private final Skin skin;
	private DevelopmentPlacementRenderer renderer;

	private PlacedArtifactTable artifactTable;
	private ControllerPlanet controllerPlanet;
	private final PlanetScreen planetScreen;


	public PlacementWindow(Skin skin, DevelopmentPlacementRenderer renderer, ControllerPlanet controllerPlanet, PlanetScreen planetScreen) {
		super("Placement", skin);
		this.skin = skin;
		this.renderer = renderer;
		this.controllerPlanet = controllerPlanet;
		this.planetScreen = planetScreen;
		renderer.setCursorChangeListener(this);

		cursorPositionLabel = new Label("", Styles.LABEL_DEV);
		add(cursorPositionLabel);
		row();
		artifactTable = new PlacedArtifactTable();
		artifactTable.setArtifactPlacementListener(this);


		add(artifactTable);
		row();

		update();
	}


	public void update() {
		artifactTable.update(controllerPlanet);
		updateCursorLabel();
		pack();
	}

	private void updateCursorLabel() {
		Vector3 cp = renderer.getCursorPosition();
		this.cursorPositionLabel.setText(
				"CURSOR: x: " + (Math.floor(cp.x * 10f) / 10f)
						+ " y:" + (Math.floor(cp.y * 10f) / 10f));
		pack();
	}

	@Override
	public void onCursorPositionChange(Vector3 position) {
		updateCursorLabel();
	}

	@Override
	public void onArtifactAdd() {
		Vector3 cp = renderer.getCursorPosition();

	}

	@Override
	public void onArtifactRemove(String id) {
		ArtifactObject a = controllerPlanet.getCurrentVisibleArtifact(id);
		if (a != null) {
			controllerPlanet.removeArtifactModel(a);
		}
		PointWithId pointToRemove = null;
		for (PointWithId p : controllerPlanet.getAllArtifactsOnPlanet()) {
			if (p.id.equals(id)) {
				pointToRemove = p;
				break;
			}
		}
		if (pointToRemove != null) {
			controllerPlanet.getAllArtifactsOnPlanet().remove(pointToRemove);
		}
		update();
	}

	@Override
	public void onArtifactView(int tileX, int tileY) {
		planetScreen.devJumpTo(tileX, tileY);
	}
}
