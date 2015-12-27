package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.region.Chunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WorldController implements ChunkListener {

	private static final String WORLD_NAME = "entities/worlds/world0%d.json";


	private float tileGraphicSize = 2f;

	private int requestRadiusInTiles = 200;
	private int lastRequestCenterTileX = 0;
	private int lastRequestCenterTileY = 0;

	private Opus opus;

	private List<Point> tmpRequestList = new ArrayList<Point>();
	private List<Point> tmpRemoveList = new ArrayList<Point>();

	private List<Point> currentVisibleChunkPositions = new ArrayList<Point>();
	private Map<Point, ChunkGraphic> chunkBuffer = new HashMap<Point, ChunkGraphic>();

	private Vector2 tmpVector1 = new Vector2();
	private Vector2 tmpVector2 = new Vector2();

	private int chunkBufferSize;

	public WorldController(int worldIndex) {
		String worldName = String.format(WORLD_NAME, worldIndex);

		OpusLoaderJson loader = new OpusLoaderJson();
		try {
			// load opus by a json file
			opus = loader.load(worldName);

			chunkBufferSize = (requestRadiusInTiles / opus.getConfig().chunkSize) * 2;

			// add a callback to receive chunks
			opus.addChunkListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void requestChunks(List<Point> chunkCoordinates) {
		Log.l(WorldController.class, "Requesting chunks. Buffer size: " + chunkBuffer.size());

		List<Point> requestList = new ArrayList<Point>();
		for (Point coordinate : chunkCoordinates) {
			if (chunkBuffer.get(coordinate) == null) {
				requestList.add(coordinate);
			}
		}

		if (requestList.size() > 0) {
			int[] xCoordinates = new int[requestList.size()];
			int[] yCoordinates = new int[requestList.size()];
			for (int i = 0; i < requestList.size(); i++) {
				xCoordinates[i] = requestList.get(i).x;
				yCoordinates[i] = requestList.get(i).y;
			}

			try {
				opus.requestChunks(xCoordinates, yCoordinates);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}
	}



	public void updateRequestCenter(float graphicX, float graphicY) {
		int requestCenterTileX = (int) (Math.floor(graphicX) / tileGraphicSize);
		int requestCenterTileY = (int) (Math.floor(graphicY) / tileGraphicSize);

		if (lastRequestCenterTileX == requestCenterTileX && lastRequestCenterTileY == requestCenterTileY) {
			// return if requested the same tile again
			return;
		}
		lastRequestCenterTileX = requestCenterTileX;
		lastRequestCenterTileY = requestCenterTileY;

		tmpVector2.set(requestCenterTileX, requestCenterTileY);

		int chunkSize = opus.getConfig().chunkSize;
		int chunkBufferCenterX = (int) Math.floor(requestCenterTileX / chunkSize);
		int chunkBufferCenterY = (int) Math.floor(requestCenterTileY / chunkSize);


		currentVisibleChunkPositions.clear();

		for (int chunkIndexX = 0; chunkIndexX < chunkBufferSize; chunkIndexX++) {
			for (int chunkIndexY = 0; chunkIndexY < chunkBufferSize; chunkIndexY++) {
				boolean isInRadius = false;
				int currentChunkX = chunkBufferCenterX + chunkIndexX - (chunkBufferSize / 2);
				int currentChunkY = chunkBufferCenterY + chunkIndexY - (chunkBufferSize / 2);

				// chunk tile corner 1
				tmpVector1.set(currentChunkX * chunkSize, currentChunkY * chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 2
				tmpVector1.set(currentChunkX * chunkSize, currentChunkY * chunkSize + chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 3
				tmpVector1.set(currentChunkX * chunkSize + chunkSize, currentChunkY * chunkSize + chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 4
				tmpVector1.set(currentChunkX * chunkSize + chunkSize, currentChunkY * chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < requestRadiusInTiles) {
					isInRadius = true;
				}

				if (isInRadius) {
					currentVisibleChunkPositions.add(new Point(currentChunkX, currentChunkY));
				}
			}
		}

		tmpRequestList.clear();
		tmpRemoveList.clear();


		// remove non visible chunks
		for (Map.Entry<Point, ChunkGraphic> entry : chunkBuffer.entrySet()) {
			if (!currentVisibleChunkPositions.contains(entry.getKey())) {
				tmpRemoveList.add(entry.getKey());
			}
		}
		for (Point p : tmpRemoveList) {
			chunkBuffer.remove(p);
		}

		// request visible chunk
		for (Point p : currentVisibleChunkPositions) {
			if (chunkBuffer.get(p) == null) {
				tmpRequestList.add(p);
			}
		}

		if (tmpRequestList.size() > 0) {
			requestChunks(tmpRequestList);
		}
	}

	@Override
	public void onChunkCreated(int x, int y, Chunk chunk) {
		Point point = new Point(x, y);
		if (chunkBuffer.get(point) == null) {
			ChunkGraphic chunkMesh = new ChunkGraphic(chunk, tileGraphicSize);
			chunkBuffer.put(point, chunkMesh);
		} else {
			Log.d(getClass(), "Created a chunk that already exists. x " + x + " y " + y);
		}
	}


	public void render(ModelBatch batch, Environment environment) {
		for (Map.Entry<Point, ChunkGraphic> entry : chunkBuffer.entrySet()) {
			ChunkGraphic mesh = entry.getValue();
			batch.render(mesh.getModelInstance(), environment);
		}
	}


}
