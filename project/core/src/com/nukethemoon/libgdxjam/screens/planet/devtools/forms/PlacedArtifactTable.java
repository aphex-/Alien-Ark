package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.PointWithId;

public class PlacedArtifactTable extends Table {

	private final TextButton addButton;
	private ArtifactPlacementListener listener;

	private Table artifactContent;
	private ScrollPane scrollPane;


	public PlacedArtifactTable() {
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);

		addButton = new TextButton("add artifact", Styles.UI_SKIN);
		add(addButton);
		row();

		artifactContent = new Table();
		scrollPane = new ScrollPane(artifactContent);
		scrollPane.setFadeScrollBars(false);
		add(scrollPane);

		pack();
	}

	public void update(ControllerPlanet planet) {
		artifactContent.clear();
		for (PointWithId p : planet.getAllArtifactsOnPlanet()) {
			addToList(p);
		}
		pack();
	}

	private void addToList(final PointWithId p) {
		Table artifactEntry = new Table();
		artifactEntry.add(new Label("Tile x " + p.x + " y" + p.y, Styles.LABEL_DEV)).left().width(200).fill();
		artifactEntry.add(new Label("Id " + p.id, Styles.LABEL_DEV)).left().width(200).fill();
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

		artifactContent.add(artifactEntry);
		pack();
	}

	public void setArtifactPlacementListener(final ArtifactPlacementListener listener) {
		this.listener = listener;
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onArtifactAdd();
			}
		});
	}


	public interface ArtifactPlacementListener {
		void onArtifactAdd();
		void onArtifactRemove(String id);
		void onArtifactView(int tileX, int tileY);
	}

}
