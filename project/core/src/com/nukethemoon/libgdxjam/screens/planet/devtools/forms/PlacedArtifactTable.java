package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.ArtifactDefinitions;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.ObjectPlacementInfo;

public class PlacedArtifactTable extends Table {

	private final TextButton addButton;
	private final SelectBox<ArtifactDefinitions.ConcreteArtifactType> selectBox;
	private ArtifactPlacementListener listener;

	private Table artifactContent;
	private ScrollPane scrollPane;


	public PlacedArtifactTable() {
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);

		selectBox = new SelectBox<ArtifactDefinitions.ConcreteArtifactType>(Styles.UI_SKIN);
		selectBox.setItems(ArtifactDefinitions.ConcreteArtifactType.values());
		add(selectBox);

		addButton = new TextButton("add artifact", Styles.UI_SKIN);
		add(addButton);
		row();

		artifactContent = new Table();
		scrollPane = new ScrollPane(artifactContent);
		scrollPane.setFadeScrollBars(false);
		add(scrollPane).colspan(2);

		pack();
	}

	public void update(ControllerPlanet planet) {
		artifactContent.clear();
		for (ObjectPlacementInfo p : planet.getAllArtifactsOnPlanet()) {
			addToList(p);
		}
		pack();
	}

	private void addToList(final ObjectPlacementInfo p) {
		Table artifactEntry = new Table();

		artifactEntry.add(new Label(p.type, Styles.LABEL_DEV)).left().width(200).fill();
		artifactEntry.add(new Label("X: " + Math.floor(p.x), Styles.LABEL_DEV)).left().width(100).fill().width(80).padLeft(4);
		artifactEntry.add(new Label("Y: " + Math.floor(p.y), Styles.LABEL_DEV)).left().width(100).fill().width(80).padLeft(4);
		artifactEntry.add(new Label("ID: " + p.id, Styles.LABEL_DEV)).left().width(200).fill().padRight(2);
		TextButton removeButton = new TextButton("X", Styles.UI_SKIN);
		artifactEntry.add(removeButton).left();
		removeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onArtifactRemove(p.id);
			}
		});

		TextButton jumpToButton = new TextButton("view", Styles.UI_SKIN);
		artifactEntry.add(jumpToButton).left();
		jumpToButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onArtifactView(p.x, p.y);
			}
		});
		artifactEntry.add(removeButton);
		artifactContent.add(artifactEntry).colspan(2);
		artifactContent.row();
		pack();
	}

	public void setArtifactPlacementListener(final ArtifactPlacementListener listener) {
		this.listener = listener;
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onArtifactAdd(selectBox.getSelected());
			}
		});
	}


	public interface ArtifactPlacementListener {
		void onArtifactAdd(ArtifactDefinitions.ConcreteArtifactType concreteType);
		void onArtifactRemove(String id);
		void onArtifactView(float graphicX, float graphicY);
	}

}
