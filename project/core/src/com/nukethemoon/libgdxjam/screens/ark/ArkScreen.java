package com.nukethemoon.libgdxjam.screens.ark;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.Artifact;
import com.nukethemoon.libgdxjam.game.artifacts.AttributeArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.ValueArtifact;
import com.nukethemoon.libgdxjam.game.artifacts.operators.Increase;
import com.nukethemoon.libgdxjam.game.attributes.Speed;
import com.nukethemoon.libgdxjam.input.SpriteDragAndDropProcessor;

import java.util.ArrayList;
import java.util.List;

public class ArkScreen implements Screen, SpriteDragAndDropProcessor.OnDroppedListener {

	private Rectangle workbenchArea;
	private WorkbenchSlot workbenchSpot1;
	private WorkbenchSlot workbenchSpot2;
	private WorkbenchSlot workbenchSpot3;

	private SpriteBatch spriteBatch;
	private ShapeRenderer workBenchRender;
	private SpriteDragAndDropProcessor dragAndDropProcessor;
	private InputMultiplexer multiplexer;

	private List<Sprite> alienSprites = new ArrayList<Sprite>();

	public ArkScreen(Skin uiSkin, InputMultiplexer multiplexer) {
		this.multiplexer = multiplexer;

		spriteBatch = new SpriteBatch();
		dragAndDropProcessor = new SpriteDragAndDropProcessor(this);
		dragAndDropProcessor.setAllMovableSprites(alienSprites);
	}

	@Override
	public void show() {
		Rectangle rect1 = new Rectangle(0, 0, 135, 135);
		rect1.setX(Gdx.graphics.getWidth() / 2 - 232);
		rect1.setY(Gdx.graphics.getHeight() / 3);

		workbenchSpot1 = new WorkbenchSlot(rect1);

		Rectangle rect2 = new Rectangle(0, 0, 135, 135);
		rect2.setX((rect1.x + rect1.width) + 10);
		rect2.setY(Gdx.graphics.getHeight() / 3);
		workbenchSpot2 = new WorkbenchSlot(rect2);

		Rectangle rect3 = new Rectangle(0, 0, 135, 135);
		rect3.setX((rect2.x + rect2.width) + 10);
		rect3.setY(Gdx.graphics.getHeight() / 3);
		workbenchSpot3 = new WorkbenchSlot(rect3);

		workbenchArea = new Rectangle(rect1.x - 20, rect1.y - 20, (rect3.x + rect3.width) - rect1.x + 40, rect1.height + 40);

		workBenchRender = new ShapeRenderer();

		setUpArtifacts();
		multiplexer.addProcessor(dragAndDropProcessor);
	}

	private void setUpArtifacts() {
		Sprite triangle = new ArtifactSprite(new AttributeArtifact(Speed.class));
		Sprite square = new ArtifactSprite(new ValueArtifact(42));
		Sprite circle = new ArtifactSprite(new Increase());

		alienSprites.add(triangle);
		alienSprites.add(square);
		alienSprites.add(circle);


		triangle.setX(100);
		triangle.setY(invertYAxis(triangle.getHeight() * 0.5f) - 100);
		square.setX(100);
		square.setY(triangle.getY() - square.getHeight() * 0.5f - 20);
		circle.setX(100);
		circle.setY(square.getY() - circle.getHeight() * 0.5f - 20);

		triangle.scale(-0.5f);
		square.scale(-0.5f);
		circle.scale(-0.5f);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.8f, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		{
			for (Sprite s : alienSprites) {
				s.draw(spriteBatch);
			}
		}
		spriteBatch.end();

		workBenchRender.begin(ShapeRenderer.ShapeType.Line);
		{
			workBenchRender.setColor(Color.BLACK);
			workBenchRender.rect(workbenchArea.x, workbenchArea.y, workbenchArea.width, workbenchArea.height);
		}
		workBenchRender.end();

		workbenchSpot1.draw(workBenchRender);
		workbenchSpot2.draw(workBenchRender);
		workbenchSpot3.draw(workBenchRender);
	}

	@Override
	public void OnDrop(Sprite droppedSprite, int x, int y) {
		WorkbenchSlot slot = null;
		if (workbenchSpot1.contains(x, invertYAxis(y))) {
			slot = workbenchSpot1;
		} else if (workbenchSpot2.contains(x, invertYAxis(y))) {
			slot = workbenchSpot2;
		} else if (workbenchSpot3.contains(x, invertYAxis(y))) {
			slot = workbenchSpot3;
		}

		if(slot != null) {
			if(slot.isOccupied()){
				Log.l(ArkScreen.class, "Slot is already occupied");
			} else {
				slot.insertArtifact(((ArtifactSprite) droppedSprite).getArtifact());
				dragAndDropProcessor.lock(droppedSprite);
				Log.l(ArkScreen.class, "inserting artifact into slot");
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		multiplexer.removeProcessor(dragAndDropProcessor);
	}

	@Override
	public void dispose() {

	}

	private static float invertYAxis(float yValue) {
		return Gdx.graphics.getHeight() - yValue;
	}

	private class WorkbenchSlot {
		private final Rectangle bounds;
		private Artifact insertedArtifact;


		public WorkbenchSlot(Rectangle bounds) {
			this.bounds = bounds;
		}

		public boolean contains(float positionX, float positionY) {
			return bounds.contains(positionX, positionY);
		}

		public void insertArtifact(Artifact artifact) {
			this.insertedArtifact = artifact;
		}

		public boolean isOccupied() {
			return insertedArtifact != null;
		}

		public void draw(ShapeRenderer renderer) {
			workBenchRender.begin(isOccupied() ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
			workBenchRender.setColor(Color.GOLDENROD);
			workBenchRender.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			workBenchRender.end();
		}
	}

}
