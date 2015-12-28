package com.nukethemoon.libgdxjam.screens.planet.devtools.windows;


import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.libgdxjam.screens.planet.devtools.ReloadSceneListener;

public class DevelopmentWindow extends ClosableWindow {

	public DevelopmentWindow(final Skin skin, Stage stage, final PlanetConfig planetConfig, ReloadSceneListener reloadSceneListener) {
		super("Development", skin);

		final SceneWindow sceneWindow = new SceneWindow(skin, reloadSceneListener);
		sceneWindow.setVisible(false);
		stage.addActor(sceneWindow);


		ColorAttribute colorAttribute = planetConfig.environmentColorAttributes.get(0);
		com.nukethemoon.libgdxjam.screens.planet.devtools.forms.ColorForm cf = new com.nukethemoon.libgdxjam.screens.planet.devtools.forms.ColorForm(skin, colorAttribute.color, new com.nukethemoon.libgdxjam.screens.planet.devtools.forms.ColorForm.ColorChangeListener() {
			@Override
			public void onColorChange(float v, float v1, float v2) {
				pack();
			}
		});
		add(cf);
		row();


		TextButton sceneButton = new TextButton("Scene", skin);
		sceneButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sceneWindow.load(planetConfig);
				sceneWindow.setVisible(true);
				sceneWindow.toFront();
			}
		});
		add(sceneButton);


		pack();
	}
}
