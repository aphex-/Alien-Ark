package com.nukethemoon.libgdxjam.screens.ark;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.Alien;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.game.attributes.Attribute;
import com.nukethemoon.libgdxjam.ui.hud.ShipProgressBar;
import com.nukethemoon.tools.ani.Ani;

import java.util.List;

//TODO merge von Artifatcs
//TODO cancel von Artifatcs merges
//TODO Korrekte Attributartifact texturen
//TODO Korrekte Alien texturen
//TODO Alien hint text

//TODO Artifact hint text, wenn Attr. bekannt ("Speed")
//TODO anzahl aliens/artifacts
//TODO Scroll shadow

//TODO (opional) Artifact hint text, wenn Attr. noch unbekannt ("???")
//TODO (optional) anzahl aliens beschränken
//TODO (optional) delete crew memeber

public class ArkScreen implements Screen {

	public static final int INVENTORY_WIDTH = 245;
	public static final int INVENTORY_OFFSET_X = 75;
	public static final int INVENTORY_HEIGHT = 355;
	public static final int INVENTORY_Y = 200;
	private final Ani ani;
	private WorkbenchSlot workbenchSlot1;
	private WorkbenchSlot workbenchSlot2;
	private WorkbenchSlot workbenchSlot3;

	private Actor resultArea;

	private SpriteBatch spriteBatch;

	private Skin skin;
	private InputMultiplexer multiplexer;
	private Texture background;

	private Stage stage;
	private DragAndDrop dragAndDrop;
	private Table alienTable;
	private Table artifactsTable;
	private Label leftHintText;
	private Label rightHintText;

	private Actor selectedArtifact;
	private Actor selectedAlien;

	private ShipProgressBar fuelProgressBar;
	private ShipProgressBar shieldProgressBar;

	public ArkScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		ani = new Ani();
		skin = uiSkin;
		this.multiplexer = multiplexer;

		spriteBatch = new SpriteBatch();

		stage = new Stage();
		dragAndDrop = new DragAndDrop();

		App.TUTORIAL_CONTROLLER.register(stage, ani);
		App.TUTORIAL_CONTROLLER.nextStepFor(this.getClass());

		fuelProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.FUEL);
		fuelProgressBar.updateFromShipProperties();
		shieldProgressBar = new ShipProgressBar(ShipProgressBar.ProgressType.SHIELD);
		shieldProgressBar.updateFromShipProperties();

		stage.addActor(fuelProgressBar);
		stage.addActor(shieldProgressBar);
	}

	@Override
	public void show() {
		workbenchSlot1 = new WorkbenchSlot();
		int dimension = 75;
		workbenchSlot1.setBounds(400, 435, dimension, dimension);

		workbenchSlot2 = new WorkbenchSlot();
		workbenchSlot2.setBounds(527, 435, dimension, dimension);

		workbenchSlot3 = new WorkbenchSlot();
		workbenchSlot3.setBounds(652, 435, dimension, dimension);

		resultArea = new Actor();
		resultArea.setBounds(777, 435, dimension, dimension);

		background = new Texture("textures/background02.png");

		createArtifactsInventory();
		createAlienInventory();

		updateArtifactsInventory();
		updateAlienInventory();

		addDropTarget(workbenchSlot1);
		addDropTarget(workbenchSlot2);
		addDropTarget(workbenchSlot3);

		createPropertiesList();

		createCloseButton();

		leftHintText = new Label("", skin);
		leftHintText.setX(70);
		leftHintText.setY(90);
		leftHintText.setWidth(255);
		leftHintText.setHeight(95);
		leftHintText.setWrap(true);
		stage.addActor(leftHintText);

		rightHintText = new Label("", skin);
		rightHintText.setX(Gdx.graphics.getWidth() - 255 - 70);
		rightHintText.setY(90);
		rightHintText.setWidth(255);
		rightHintText.setHeight(95);
		rightHintText.setWrap(true);

		stage.addActor(rightHintText);

		multiplexer.addProcessor(stage);

	}

	private void createCloseButton() {
		Actor closeButton = new Actor();
		closeButton.setX(493);
		closeButton.setY(32);
		closeButton.setWidth(290);
		closeButton.setHeight(44);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				App.openSolarScreen();
			}
		});

		stage.addActor(closeButton);
	}

	private void createPropertiesList() {
		Table propertiesTable = new Table();

		for (Attribute a : SpaceShipProperties.properties.getAttributes()) {
			Table singleProperty = new Table();
			singleProperty.align(Align.bottomLeft);
			singleProperty.padLeft(5);
			singleProperty.setBackground(new TextureRegionDrawable(Attribute.getPropertiesTexture(a.getClass())));
			Label label = new Label(a.name(), skin);
			singleProperty.add(label).width(395);
			Label value = new Label(String.valueOf(a.getCurrentValue()), skin);
			singleProperty.add(value).width(100);

			propertiesTable.add(singleProperty).width(500).height(39);
			propertiesTable.row().space(3);
		}

		propertiesTable.setWidth(520);
		ScrollPane pane = new ScrollPane(propertiesTable);
		pane.setX(390);
		pane.setY(100);
		pane.setWidth(500);
		pane.setHeight(210);
		stage.addActor(pane);
	}

	private void createArtifactsInventory() {
		artifactsTable = new Table();
		artifactsTable.setWidth(INVENTORY_WIDTH);

		artifactsTable.padTop(6);
		artifactsTable.padRight(2);
		artifactsTable.padLeft(2);
		artifactsTable.padBottom(6);
		artifactsTable.top();
		ScrollPane artifactsScrollPane = new ScrollPane(artifactsTable);

		artifactsScrollPane.setX(INVENTORY_OFFSET_X);
		artifactsScrollPane.setY(INVENTORY_Y);
		artifactsScrollPane.setWidth(INVENTORY_WIDTH);
		artifactsScrollPane.setHeight(INVENTORY_HEIGHT);
		artifactsScrollPane.setScrollingDisabled(true, false);

		stage.addActor(artifactsScrollPane);
	}

	private void createAlienInventory() {
		alienTable = new Table();
		alienTable.setWidth(INVENTORY_WIDTH);
		alienTable.padTop(6);
		alienTable.padRight(2);
		alienTable.padLeft(2);
		alienTable.padBottom(6);
		alienTable.top();

		ScrollPane alienScrollPane = new ScrollPane(alienTable);
		alienScrollPane.setX(Gdx.graphics.getWidth() - INVENTORY_OFFSET_X - INVENTORY_WIDTH);
		alienScrollPane.setY(INVENTORY_Y);
		alienScrollPane.setWidth(INVENTORY_WIDTH);
		alienScrollPane.setHeight(INVENTORY_HEIGHT);
		alienScrollPane.setScrollingDisabled(true, false);

		stage.addActor(alienScrollPane);
	}

	private void updateArtifactsInventory() {
		artifactsTable.clear();
		final List<Artifact> artifacts = SpaceShipProperties.properties.getArtifacts();

		for (int i = 0; i < artifacts.size(); ++i) {
			final Artifact artifact = artifacts.get(i);
			final Actor actor = artifact.createActor();
			actor.setUserObject(artifact);
			artifactsTable.add(actor).space(5).width(75).height(75);

			actor.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedArtifact = actor;
					selectedAlien = null;
					updateHintTexts();
				}
			});

			dragAndDrop.addSource(new DragAndDrop.Source(actor) {

				@Override
				public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

					actor.setVisible(false);

					DragAndDrop.Payload payload = new DragAndDrop.Payload();

					dragAndDrop.setDragActorPosition(-x, -y);
					payload.setObject(artifact);

					payload.setDragActor(artifact.createActor());

					return payload;
				}

				@Override
				public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
					if (target != null) {
						artifactsTable.removeActor(getActor());
					} else {
						actor.setVisible(true);
					}
				}
			});
			if ((i + 1) % 3 == 0)
				artifactsTable.row();
		}
	}

	private void updateAlienInventory() {
		alienTable.clear();
		final List<Alien> aliens = SpaceShipProperties.properties.getAliens();

		for (int i = 0; i < aliens.size(); ++i) {
			final Alien alien = aliens.get(i);
			final Image img = new Image(alien.getTexture());
			img.setUserObject(alien);
			img.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectedAlien = img;
					selectedArtifact = null;
					updateHintTexts();
				}
			});
			alienTable.add(img).space(5).width(75).height(75);

			if ((i + 1) % 3 == 0)
				alienTable.row();
		}
	}

	private void updateHintTexts() {
		leftHintText.setText(selectedArtifact == null ? "" : ((Artifact) selectedArtifact.getUserObject()).getDescription());
		rightHintText.setText(selectedAlien == null ? "" : ((Alien)selectedAlien.getUserObject()).getDescription());
	}

	private void addDropTarget(final WorkbenchSlot slot) {
		dragAndDrop.addTarget(new DragAndDrop.Target(slot) {
			@Override
			public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				//TODO
				return true;
			}

			@Override
			public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {

			}

			@Override
			public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
				slot.insertArtifact((Artifact) payload.getObject());
				dragAndDrop.removeTarget(this);
				App.TUTORIAL_CONTROLLER.onAlienCrafted();
				SpaceShipProperties.properties.updateCache();
				fuelProgressBar.updateFromShipProperties();
				shieldProgressBar.updateFromShipProperties();
			}
		});
		stage.addActor(slot);
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(background, 0, 0);
		spriteBatch.end();

		//x=75 y=height-145 scroll pane left
		App.TUTORIAL_CONTROLLER.toFront();
		stage.act(delta);
		stage.draw();
		ani.update();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		multiplexer.removeProcessor(stage);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	private static float invertYAxis(float yValue) {
		return Gdx.graphics.getHeight() - yValue;
	}

	private class WorkbenchSlot extends Actor {
		private Artifact insertedArtifact;
		private Actor actor = new Group();

		public WorkbenchSlot() {

		}

		public void insertArtifact(Artifact artifact) {
			this.insertedArtifact = artifact;
			actor = artifact.createActor();
			actor.setX(getX());
			actor.setY(getY());
			actor.setWidth(getWidth());
			actor.setHeight(getHeight());
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			actor.draw(batch, parentAlpha);
		}

		public boolean isOccupied() {
			return insertedArtifact != null;
		}


	}

}
