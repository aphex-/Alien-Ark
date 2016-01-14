package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.ArtifactDefinitions;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.ObjectPlacementInfo;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.planet.devtools.DevelopmentPlacementRenderer;
import com.nukethemoon.libgdxjam.screens.planet.devtools.forms.PlacedArtifactTable;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;

import java.util.List;

public class PlacementWindow extends ClosableWindow implements DevelopmentPlacementRenderer.CursorChangeListener, PlacedArtifactTable.ArtifactPlacementListener {

	private final TextButton saveButton;
	private Label cursorPositionLabel;
	private final Skin skin;
	private DevelopmentPlacementRenderer renderer;

	private PlacedArtifactTable artifactTable;
	private ControllerPlanet controllerPlanet;
	private final PlanetScreen planetScreen;


	public PlacementWindow(Skin skin, DevelopmentPlacementRenderer renderer, final ControllerPlanet controllerPlanet, final PlanetScreen planetScreen) {
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

		saveButton = new TextButton("save", Styles.UI_SKIN);
		saveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PlanetConfig.savePlanetConfig(controllerPlanet.getPlanetConfig());
			}
		});
		add(saveButton);
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
	public void onArtifactAdd(ArtifactDefinitions.ConcreteArtifactType artifact) {
		Vector3 cp = renderer.getCursorPosition();
		String newArtifactId = controllerPlanet.getPlanetConfig().consumeArtifactId();
		ObjectPlacementInfo point = new ObjectPlacementInfo(cp.x, cp.y, newArtifactId);
		point.type = artifact.name();
		ArtifactObject artifactObject = new ArtifactObject(point);
		// live add
		controllerPlanet.addArtifact(artifactObject);
		controllerPlanet.getAllArtifactsOnPlanet().add(point);
		// planet config add
		controllerPlanet.getPlanetConfig().artifacts.add(point);
		update();
	}

	@Override
	public void onArtifactRemove(String id) {
		ArtifactObject a = controllerPlanet.getCurrentVisibleArtifact(id);
		if (a != null) {
			// live remove
			controllerPlanet.removeArtifactModel(a);
			removeArtifactFromList(controllerPlanet.getAllArtifactsOnPlanet(), a.getDefinition().id);
			// planet config remove
			removeArtifactFromList(controllerPlanet.getPlanetConfig().artifacts, a.getDefinition().id);
			// save game remove
			SpaceShipProperties.properties.unregisterCollectedArtifact(a.getDefinition().id);
		}
		update();
	}

	private void removeArtifactFromList(List<ObjectPlacementInfo> list, String id) {
		ObjectPlacementInfo pointToRemove = null;
		for (ObjectPlacementInfo p : list) {
			if (p.id.equals(id)) {
				pointToRemove = p;
				break;
			}
		}
		if (pointToRemove != null) {
			list.remove(pointToRemove);
		}
	}

	@Override
	public void onArtifactView(float graphicX, float graphicY) {
		planetScreen.devJumpTo(graphicX, graphicY);
	}
}
