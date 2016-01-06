package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.PlanetPart;
import com.nukethemoon.tools.opusproto.generator.ChunkListener;
import com.nukethemoon.tools.opusproto.generator.Opus;
import com.nukethemoon.tools.opusproto.interpreter.ColorInterpreter;
import com.nukethemoon.tools.opusproto.interpreter.TypeInterpreter;
import com.nukethemoon.tools.opusproto.loader.json.OpusLoaderJson;
import com.nukethemoon.tools.opusproto.region.Chunk;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ControllerPlanet implements ChunkListener, Disposable {

	private static final String WORLD_NAME = "entities/planets/planet01/opusConfig.json";


	private float tileGraphicSize = 3f;

	private int requestRadiusInTiles = 150;
	private int lastRequestCenterTileX = 0;
	private int lastRequestCenterTileY = 0;
	private long requestCount = 0;

	private Opus opus;

	private List<Point> tmpRequestList = new ArrayList<Point>();
	private List<Point> tmpRemoveList = new ArrayList<Point>();

	private List<Point> currentVisibleChunkPositions = new ArrayList<Point>();

	private Map<Point, PlanetPart> chunkGraphicBuffer = new HashMap<Point, PlanetPart>();


	private Vector2 tmpVector1 = new Vector2();
	private Vector2 tmpVector2 = new Vector2();

	private int chunkBufferSize;
	private PlanetConfig planetConfig;
	private ControllerPhysic controllerPhysic;

	public ControllerPlanet(int worldIndex, PlanetConfig pPlanetConfig, ControllerPhysic controllerPhysic) {
		this.planetConfig = pPlanetConfig;
		this.controllerPhysic = controllerPhysic;
		String worldName = String.format(WORLD_NAME, worldIndex);

		OpusLoaderJson loader = new OpusLoaderJson();
		try {
			// load opus by a json file
			opus = loader.load(worldName);
			;
			com.nukethemoon.tools.opusproto.log.Log.logLevel = com.nukethemoon.tools.opusproto.log.Log.LogType.Error;

			chunkBufferSize = (requestRadiusInTiles / opus.getConfig().chunkSize) * 2;

			// add a callback to receive chunks
			opus.addChunkListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void requestChunks(List<Point> chunkCoordinates) {

		List<Point> requestList = new ArrayList<Point>();
		for (Point coordinate : chunkCoordinates) {
			if (chunkGraphicBuffer.get(coordinate) == null) {
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

	Vector2 tmpVec1 = new Vector2();
	Vector2 tmpVec2 = new Vector2();

	public void updateRequestCenter(Vector3 position, Vector3 direction) {
		tmpVec1.set((float) (Math.floor(position.x) / tileGraphicSize), (float) (Math.floor(position.y) / tileGraphicSize));
		tmpVec2.set(direction.x, direction.y).nor().scl(-1).scl(requestRadiusInTiles * 0.7f);
		tmpVec1.sub(tmpVec2);

		int requestCenterTileX = (int) tmpVec1.x;
		int requestCenterTileY = (int) tmpVec1.y;

		if (lastRequestCenterTileX == requestCenterTileX && lastRequestCenterTileY == requestCenterTileY && requestCount < 0) {
			// return if requested the same tile again
			return;
		}

		requestCount++;
		// buffer the last request position
		lastRequestCenterTileX = requestCenterTileX;
		lastRequestCenterTileY = requestCenterTileY;

		tmpVector2.set(requestCenterTileX, requestCenterTileY);

		int chunkSize = opus.getConfig().chunkSize - 1;
		int chunkBufferCenterX = (int) Math.floor(requestCenterTileX / chunkSize);
		int chunkBufferCenterY = (int) Math.floor(requestCenterTileY / chunkSize);


		currentVisibleChunkPositions.clear();

		// find chunks that are in the view radius
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
		for (Map.Entry<Point, PlanetPart> entry : chunkGraphicBuffer.entrySet()) {
			if (!currentVisibleChunkPositions.contains(entry.getKey())) {
				PlanetPart c = entry.getValue();
				for (btRigidBody body : c.rigidBodyList) {
					controllerPhysic.removeRigidBody(body);
				}
				for (btCollisionObject object : c.collisionList) {
					controllerPhysic.removeCollisionObject(object);
				}
				c.dispose();
				tmpRemoveList.add(entry.getKey());
			}
		}
		for (Point p : tmpRemoveList) {
			chunkGraphicBuffer.remove(p);
		}

		// add chunks that are not already loaded
		for (Point p : currentVisibleChunkPositions) {
			if (chunkGraphicBuffer.get(p) == null) {
				tmpRequestList.add(p);
			}
		}

		// request visible chunks
		if (tmpRequestList.size() > 0) {
			requestChunks(tmpRequestList);
		}
	}

	@Override
	public void onChunkCreated(int x, int y, Chunk chunk) {
		Point point = new Point(x, y);
		if (chunkGraphicBuffer.get(point) == null) {
			PlanetPart chunkMesh = new PlanetPart(chunk, tileGraphicSize, planetConfig,
					toTypeInterpreter((ColorInterpreter) opus.getLayers().get(0).getInterpreter()));
			for (btRigidBody body : chunkMesh.rigidBodyList) {
				controllerPhysic.addRigidBody(body,
						CollisionTypes.byMask((short) body.getUserValue()));
			}
			for (btCollisionObject object : chunkMesh.collisionList) {
				controllerPhysic.addCollisionObject(object);
			}

			chunkGraphicBuffer.put(point, chunkMesh);
		} else {
			Log.d(getClass(), "Created a chunk that already exists. x " + x + " y " + y);
		}
	}


	public void render(ModelBatch batch, Environment environment) {
		for (Map.Entry<Point, PlanetPart> entry : chunkGraphicBuffer.entrySet()) {
			PlanetPart mesh = entry.getValue();
			batch.render(mesh.getModelInstance(), environment);
		}
	}


	public void dispose() {
		for (PlanetPart g : chunkGraphicBuffer.values()) {
			g.dispose();
		}
	}

	/**
	 * This is a hack because of an issue in opus.
	 */
	private TypeInterpreter toTypeInterpreter(ColorInterpreter colorInterpreter) {
		TypeInterpreter typeInterpreter = new TypeInterpreter("someId");
		for (ColorInterpreter.InterpreterItem item : colorInterpreter.items) {
			TypeInterpreter.InterpreterItem interpreterItem = new TypeInterpreter.InterpreterItem();
			interpreterItem.startValue = item.startValue;
			interpreterItem.endValue = item.endValue;
			typeInterpreter.it.add(interpreterItem);
		}
		return typeInterpreter;
	}

}
