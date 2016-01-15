package com.nukethemoon.libgdxjam.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Styles;

public class MenuTable extends Table {

	private final Stage stage;
	private Table content;

	public MenuTable(Stage stage, final CloseListener closeListener) {
		this.stage = stage;

		setBackground(new TextureRegionDrawable(App.TEXTURES.findRegion("popupMenu")));
		pad(0);
		content = new Table();
		content.pad(15);
		content.padTop(5);
		ImageButton imageButton = new ImageButton(
				new TextureRegionDrawable(App.TEXTURES.findRegion("exitImage2")));
		imageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MenuTable.this.remove();
				closeListener.onClose();
			}
		});
		add(imageButton).top().right();
		row();
		add(content).fill().expand();

		addContent();

		pack();
		setPosition((Gdx.graphics.getWidth() / 2) - this.getWidth() / 2,
				(Gdx.graphics.getHeight() / 2) - this.getHeight() / 2);

	}

	private void addContent() {
		content.left().top();

		TextButton creditsButton = new TextButton("CREDITS", Styles.STYLE_BUTTON_02);
		creditsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addActor(new CreditsDialog());
			}
		});
		content.add(creditsButton).width(250).top().colspan(2).padTop(20);
		content.row();

		TextButton helpButton = new TextButton("HELP", Styles.STYLE_BUTTON_02);
		helpButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addActor(new HelpDialog());
			}
		});
		content.add(helpButton).width(250).top().colspan(2);


		addLine();

		// tutorial
		Label tutorialEnabledLabel = new Label("Tutorial Enabled", Styles.LABEL_PROGRESS_TYPE);
		content.add(tutorialEnabledLabel).left().padLeft(5);
		final ImageButton tutorialEnabledButton = new ImageButton(Styles.IMAGE_BUTTON_STYLE);
		tutorialEnabledButton.setChecked(App.config.tutorialEnabled);
		tutorialEnabledButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				App.config.tutorialEnabled = tutorialEnabledButton.isChecked();
				App.saveConfig();
			}
		});
		content.add(tutorialEnabledButton).top().right();
		addLine();

		// controls
		Label invertLabel = new Label("Invert Controls", Styles.LABEL_PROGRESS_TYPE);
		content.add(invertLabel).left().padLeft(5);
		final ImageButton invertButton = new ImageButton(Styles.IMAGE_BUTTON_STYLE);
		invertButton.setChecked(App.config.invertUpDown);
		invertButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				App.config.invertUpDown = invertButton.isChecked();
				App.saveConfig();
			}
		});
		content.add(invertButton).top().right();
		addLine();

		// audio
		Label audioLabel = new Label("Audio Enabled", Styles.LABEL_PROGRESS_TYPE);
		content.add(audioLabel).left().padLeft(5);
		final ImageButton audioButton = new ImageButton(Styles.IMAGE_BUTTON_STYLE);
		audioButton.setChecked(App.config.playAudio);
		audioButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				App.config.playAudio = audioButton.isChecked();
				App.audioController.setMusicEnabled(App.config.playAudio);
				App.audioController.setSoundEnabled(App.config.playAudio);
				App.saveConfig();
			}
		});
		content.add(audioButton).top().right();
		addLine();


		Label versionLabel = new Label("Version", Styles.LABEL_PROGRESS_TYPE);
		content.add(versionLabel).top().left().padLeft(5);
		content.add(new Label(Config.VERSION, Styles.LABEL_PROGRESS_TYPE)).top().right().padRight(5);


	}

	private void addLine() {
		Image line = new Image(App.TEXTURES.findRegion("line"));
		content.row();
		content.add(line).width(250).colspan(2).pad(4);
		content.row();
	}

	public interface CloseListener {
		void onClose();
	}
}
