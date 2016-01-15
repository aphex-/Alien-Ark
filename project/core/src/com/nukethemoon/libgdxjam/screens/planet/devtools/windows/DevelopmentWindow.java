package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.ControllerPlanet;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.PlanetScreen;
import com.nukethemoon.libgdxjam.screens.planet.devtools.DevelopmentPlacementRenderer;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;

public class DevelopmentWindow extends ClosableWindow {

	public static NinePatchDrawable INNER_BACKGROUND;

	public DevelopmentWindow(final Skin skin, Stage stage, final PlanetConfig planetConfig,
							 final PlanetScreen planetScreen, final ReloadSceneListener reloadSceneListener,
							 DevelopmentPlacementRenderer placementRenderer, ControllerPlanet controllerPlanet,
							 final String[] knownPlanets) {
		super("Development", skin);
		NinePatch patch = new NinePatch(new Texture(Gdx.files.internal("skin/background.png")),
				1, 1, 1, 1);

		INNER_BACKGROUND = new NinePatchDrawable(patch);
		defaults().pad(4);

		final DirectionalLightsWindow directionalLightsWindow = new DirectionalLightsWindow(skin, reloadSceneListener);
		directionalLightsWindow.setVisible(false);
		stage.addActor(directionalLightsWindow);

		final MaterialsWindow materialsWindow = new MaterialsWindow(stage, skin, reloadSceneListener);
		materialsWindow.setVisible(false);
		stage.addActor(materialsWindow);

		final PlacementWindow placementWindow = new PlacementWindow(skin, placementRenderer, controllerPlanet, planetScreen);
		placementWindow.setVisible(false);
		stage.addActor(placementWindow);

		Table openPlanetTable = new Table();

		final SelectBox<String> openPlanetSelect = new SelectBox<String>(Styles.UI_SKIN);
		openPlanetSelect.setItems(knownPlanets);
		openPlanetTable.add(openPlanetSelect);
		TextButton button = new TextButton("open", Styles.UI_SKIN);

		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				planetScreen.dispose();
				String selected = openPlanetSelect.getSelected();
				for (int i = 0; i < knownPlanets.length; i++) {
					if (knownPlanets[i].equals(selected)) {
						App.openPlanetScreen(i);
					}
				}
			}
		});
		openPlanetTable.add(button);

		add(openPlanetTable);
		row();


		/*final ColorAttribute colorAttribute = planetConfig.environmentColorAttributes.get(0);
		ColorAttributeForm colorAttributeForm = new ColorAttributeForm(skin, colorAttribute, new ColorAttributeForm.ColorAttributeChangeListener() {
			@Override
			public void onColorAttributeChange(ColorAttribute attribute) {
				planetConfig.environmentColorAttributes.remove(colorAttribute);
				planetConfig.environmentColorAttributes.add(attribute);
				reloadSceneListener.onReloadScene(planetConfig);
			}
		});
		add(colorAttributeForm);
		row();*/

		TextButton placementButton = new TextButton("Placement", skin);
		placementButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				placementWindow.setVisible(true);
				placementWindow.toFront();
			}
		});
		add(placementButton);
		row();

		TextButton lightsButton = new TextButton("Directional Lights", skin);
		lightsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				directionalLightsWindow.load(planetConfig);
				directionalLightsWindow.setVisible(true);
				directionalLightsWindow.toFront();
			}
		});
		add(lightsButton);
		row();

		TextButton materialsButton = new TextButton("Materials", skin);
		materialsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				materialsWindow.load(planetConfig);
				materialsWindow.setVisible(true);
				materialsWindow.toFront();
			}
		});
		add(materialsButton);

		row();
		add(new Label("'Tab' toggle camera", skin)).left().fill();
		row();
		add(new Label("'p' toggle pause", skin)).left().fill();

		row();

		final TextButton leaveButton = new TextButton("Leave Planet", skin);
		leaveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				planetScreen.leavePlanet();
			}
		});
		leaveButton.setPosition(10, 10);
		add(leaveButton);

		pack();
	}


}
