package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.region.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WorldController implements ChunkListener {

	private Opus opus;
	private Map<Vector2, ChunkGraphic> chunkMeshes = new HashMap<Vector2, ChunkGraphic>();

	public WorldController() {

		OpusLoaderJson loader = new OpusLoaderJson();


		try {
			// load opus by a json file
			opus = loader.load("entities/worlds/world01.json");
			// add a callback to receive chunks
			opus.addChunkListener(WorldController.this);

			requestChunks(	new Vector2(0, 0),
							new Vector2(-1, 0),
							new Vector2(1, 0));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void requestChunks(Vector2... chunkCoordinates) {
		List<Vector2> requestList = new ArrayList<Vector2>();
		for (Vector2 coordinate : chunkCoordinates) {
			if (chunkMeshes.get(coordinate) == null) {
				requestList.add(coordinate);
			}
		}
		if (requestList.size() > 0) {


			int[] xCoordinates = new int[requestList.size()];
			int[] yCoordinates = new int[requestList.size()];
			for (int i = 0; i < requestList.size(); i++) {
				xCoordinates[i] = (int) requestList.get(i).x;
				yCoordinates[i] = (int) requestList.get(i).y;
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

	@Override
	public void onChunkCreated(int x, int y, Chunk chunk) {
		ChunkGraphic chunkMesh = new ChunkGraphic(chunk);
		Vector2 positionVector = new Vector2(x, y);
		if (chunkMeshes.get(positionVector) == null) {
			chunkMeshes.put(positionVector, chunkMesh);
		} else {
			Log.d(getClass(), "Created a chunk that already exists. x " + x + " y " + y);
		}
	}

	public void render(ModelBatch batch) {
		for (Map.Entry<Vector2, ChunkGraphic> entry : chunkMeshes.entrySet()) {
			ChunkGraphic mesh = entry.getValue();
			batch.render(mesh.getModelInstance());
		}
	}

	public int getChunkSize() {
		return opus.getConfig().chunkSize;
	}
}
