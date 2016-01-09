package com.nukethemoon.libgdxjam.screens.ark;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.game.Alien;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;

import java.util.List;

//TODO Add properties list (am besten nicht-scrollend)
//TODO merge von Artifatcs
//TODO cancel von Artifatcs merges
//TODO Korrekte Attributartifact texturen
//TODO Korrekte Valueartifact zahl
//TODO Korrekte Alien texturen
//TODO Alien hint text
//TODO Artifact hint text, wenn Attr. noch unbekannt ("???")
//TODO Artifact hint text, wenn Attr. bekannt ("Speed")
//TODO close button
//TODO anzahl aliens/artifacts
//TODO Scroll shadow
//TODO (optional) anzahl aliens beschränken
//TODO (optional) delete crew memeber

public class ArkScreen implements Screen {

	public static final int INVENTORY_WIDTH = 245;
	public static final int INVENTORY_OFFSET_X = 75;
	public static final int INVENTORY_HEIGHT = 355;
	public static final int INVENTORY_Y = 200;
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

	public ArkScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		skin = uiSkin;
		this.multiplexer = multiplexer;

		spriteBatch = new SpriteBatch();

		stage = new Stage();
		dragAndDrop = new DragAndDrop();
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


		Table propertiesTable = new Table();
		Label label = new Label("SPEED", skin);

		label.getStyle().background = new TextureRegionDrawable(App.TEXTURES.findRegion("row03"));

		propertiesTable.add(label).width(540);
		propertiesTable.setX(400);
		propertiesTable.setY(250);


		propertiesTable.setWidth(540);
		stage.addActor(propertiesTable);

				multiplexer.addProcessor(stage);

	}

	private void createArtifactsInventory() {
		artifactsTable = new Table();
		artifactsTable.setWidth(INVENTORY_WIDTH);
		artifactsTable.pad(3);
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
		alienTable.pad(3);
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
			final Image img = new Image(artifact.getTexture());

			artifactsTable.add(img).space(5).width(75).height(75);

			dragAndDrop.addSource(new DragAndDrop.Source(img) {

				@Override
				public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {

					img.setVisible(false);

					DragAndDrop.Payload payload = new DragAndDrop.Payload();

					dragAndDrop.setDragActorPosition(-x, -y + 75);
					payload.setObject(artifact);

					payload.setDragActor(new Image(img.getDrawable()));

					payload.setValidDragActor(new Image(img.getDrawable()));

					payload.setInvalidDragActor(new Image(img.getDrawable()));

					return payload;
				}

				@Override
				public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
					if (target != null) {
						artifactsTable.removeActor(getActor());
						//artifacts.remove(payload.getObject());
					} else {
						img.setVisible(true);
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

			alienTable.add(img).space(5).width(75).height(75);

			if ((i + 1) % 3 == 0)
				alienTable.row();
		}
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
				slot.setDrawable(((Image) source.getActor()).getDrawable());
				dragAndDrop.removeTarget(this);

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
		stage.act(delta);
		stage.draw();
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

	}

	@Override
	public void dispose() {

	}

	private static float invertYAxis(float yValue) {
		return Gdx.graphics.getHeight() - yValue;
	}

	private class WorkbenchSlot extends Image {
		private Artifact insertedArtifact;

		public WorkbenchSlot() {
			super(App.TEXTURES.findRegion("slotDrop"));
		}

		public void insertArtifact(Artifact artifact) {
			this.insertedArtifact = artifact;
			setDrawable(new TextureRegionDrawable(App.TEXTURES.findRegion("slot01")));
		}

		public boolean isOccupied() {
			return insertedArtifact != null;
		}


	}

}
