package com.nukethemoon.libgdxjam.screens.planet.devtools.forms;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
		Table entry = new Table();

		entry.add(new Label("X: " + Math.floor(p.x), Styles.LABEL_DEV)).left().width(100).fill();
		entry.add(new Label("Y: " + Math.floor(p.y), Styles.LABEL_DEV)).left().width(100).fill();
		TextButton removeButton = new TextButton("X", Styles.UI_SKIN);
		entry.add(removeButton).left();
		removeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onWayPointRemove(p);
			}
		});


		entry.add(new Label("rotX: ", Styles.LABEL_DEV));
		final TextField textFieldRotationX = new TextField(p.rotationX + "", Styles.UI_SKIN);
		textFieldRotationX.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				textFieldRotationX.setStyle(Styles.TEXT_FIELD_STYLE);
				if (isFloat(textFieldRotationX.getText())) {
					p.rotationX = getFloat(textFieldRotationX.getText());
					listener.onWayPointChange(p);
				} else {
					textFieldRotationX.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
				}
			}
		});
		entry.add(textFieldRotationX).width(50);

		entry.add(new Label("rotZ: ", Styles.LABEL_DEV));
		final TextField textFieldRotationZ = new TextField(p.rotationZ + "", Styles.UI_SKIN);
		textFieldRotationZ.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				textFieldRotationZ.setStyle(Styles.TEXT_FIELD_STYLE);
				if (isFloat(textFieldRotationZ.getText())) {
					p.rotationZ = getFloat(textFieldRotationZ.getText());
					listener.onWayPointChange(p);
				} else {
					textFieldRotationZ.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
				}
			}
		});
		entry.add(textFieldRotationZ).width(50);

		entry.add(new Label("offsetZ: ", Styles.LABEL_DEV));
		final TextField textFieldOffsetZ = new TextField(Math.floor(p.zOffset) + "", Styles.UI_SKIN);
		textFieldOffsetZ.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				textFieldOffsetZ.setStyle(Styles.TEXT_FIELD_STYLE);
				if (isFloat(textFieldOffsetZ.getText())) {
					p.zOffset = getFloat(textFieldOffsetZ.getText());
					listener.onWayPointChange(p);
				} else {
					textFieldOffsetZ.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
				}
			}
		});
		entry.add(textFieldOffsetZ).width(50);

		entry.add(new Label("time: ", Styles.LABEL_DEV));
		final TextField textFieldSecondsToReach = new TextField(p.secondsToReach + "", Styles.UI_SKIN);
		textFieldSecondsToReach.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (isInteger(textFieldSecondsToReach.getText())) {
					textFieldSecondsToReach.setStyle(Styles.TEXT_FIELD_STYLE);
					p.secondsToReach = getInteger(textFieldSecondsToReach.getText());
					listener.onWayPointChange(p);
				} else {
					textFieldSecondsToReach.setStyle(Styles.TEXT_FIELD_STYLE_FAIL);
				}
			}
		});
		entry.add(textFieldSecondsToReach).width(50);

		TextButton jumpToButton = new TextButton("view", Styles.UI_SKIN);
		entry.add(jumpToButton).left();
		jumpToButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				listener.onWayPointView(p.x, p.y);
			}
		});
		entry.add(removeButton);
		content.add(entry);
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

	private static boolean isFloat(String text) {
		try {
			Float.parseFloat(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static float getFloat(String text) {
		try {
			return Float.parseFloat(text);
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	private static int getInteger(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public interface WayPointPlacementListener {
		void onWayPointAdd();
		void onWayPointRemove(RaceWayPoint wayPoint);
		void onWayPointView(float graphicX, float graphicY);
		void onWayPointChange(RaceWayPoint wayPoint);
	}
}
