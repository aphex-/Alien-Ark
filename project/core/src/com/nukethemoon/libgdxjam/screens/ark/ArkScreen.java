package com.nukethemoon.libgdxjam.screens.ark;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.ui.CenteredTable;

import java.util.List;

public class ArkScreen implements Screen {

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
		workbenchSlot1.setBounds(400, 435, 75, 75);


		workbenchSlot2 = new WorkbenchSlot();
		workbenchSlot2.setBounds(527, 435, 75, 75);

		workbenchSlot3 = new WorkbenchSlot();
		workbenchSlot3.setBounds(652, 435, 75, 75);

		resultArea = new Actor();
		resultArea.setBounds(777, 435, 75, 75);

		background = new Texture("textures/background02.png");

		final Table artifactsTable = new Table();
		artifactsTable.setWidth(245);
		artifactsTable.pad(3);
		artifactsTable.top();
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
					if(target != null) {
						artifactsTable.removeActor(getActor());
						artifacts.remove(payload.getObject());
					} else {
						img.setVisible(true);
					}
				}
			});
			if ((i + 1) % 3 == 0)
				artifactsTable.row();
		}

		addDropTarget(workbenchSlot1);
		addDropTarget(workbenchSlot2);
		addDropTarget(workbenchSlot3);

		ScrollPane scrollPane = new ScrollPane(artifactsTable);

		scrollPane.setX(75);
		scrollPane.setY(200);
		scrollPane.setWidth(245);
		scrollPane.setHeight(355);
		scrollPane.setScrollingDisabled(true, false);

		stage.addActor(scrollPane);
		multiplexer.addProcessor(stage);


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
				slot.setDrawable(((Image)source.getActor()).getDrawable());
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

		public WorkbenchSlot(){
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
