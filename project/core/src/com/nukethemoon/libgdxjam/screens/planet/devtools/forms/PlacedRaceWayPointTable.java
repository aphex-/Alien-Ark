package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.RaceWayPoint;

public class PlacedRaceWayPointTable extends Table {

	private final TextButton addButton;
	private final Table content;
	private final ScrollPane scrollPane;
	private WayPointPlacementListener listener;

	public PlacedRaceWayPointTable() {
		setBackground(Styles.NINE_PATCH_POPUP_BG_01);

		addButton = new TextButton("add", Styles.UI_SKIN);
		add(addButton);
		row();

		content = new Table();
		scrollPane = new ScrollPane(content);
		scrollPane.setFadeScrollBars(false);
		add(scrollPane);

		pack();
	}

	public void update(ControllerPlanet planet) {
		content.clear();
		if (planet.getPlanetConfig().planetRace != null) {
			for (RaceWayPoint p : planet.getPlanetConfig().planetRace.wayPoints) {
				addToList(p);
			}
		}
		pack();
	}

	private void addToList(final RaceWayPoint p) {
		Table artifactEntry = new Table();

		artifactEntry.add(new Label("X: " + p.x, Styles.LABEL_DEV)).left().width(100).fill();
		artifactEntry.add(new Label("Y: " + p.y, Styles.LABEL_DEV)).left().width(100).fill();
		TextButton removeButton = new TextButton("X", Styles.UI_SKIN);
		artifactEntry.add(removeButton).left();
		removeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onWayPointRemove(p);
			}
		});

		TextButton jumpToButton = new TextButton("view", Styles.UI_SKIN);
		artifactEntry.add(jumpToButton).left();
		jumpToButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onWayPointView(p.x, p.y);
			}
		});
		artifactEntry.add(removeButton);
		content.add(artifactEntry);
		content.row();
		pack();
	}

	public void setWayPointPlacementListener(final WayPointPlacementListener listener) {
		this.listener = listener;
		addButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onWayPointAdd();
			}
		});
	}


	public interface WayPointPlacementListener {
		void onWayPointAdd();
		void onWayPointRemove(RaceWayPoint wayPoint);
		void onWayPointView(float graphicX, float graphicY);
	}
}
