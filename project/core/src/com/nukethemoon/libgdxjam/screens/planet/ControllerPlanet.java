package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Collectible;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.PlanetPart;
import com.nukethemoon.libgdxjam.screens.planet.physics.ControllerPhysic;
import com.nukethemoon.tools.ani.Ani;
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

	public static float TILE_GRAPHIC_SIZE = 3f;

	private Opus opus;
	private PlanetConfig planetConfig;
	private ControllerPhysic controllerPhysic;
	private Ani ani;

	private List<Point> currentVisiblePlanetParts = new ArrayList<Point>();

	private List<Collectible> currentVisibleCollectibles = new ArrayList<Collectible>();
	private List<ArtifactObject> currentVisibleArtifacts = new ArrayList<ArtifactObject>();

	private Map<Point, PlanetPart> planetPartBuffer = new HashMap<Point, PlanetPart>();
	private final TypeInterpreter typeInterpreter;

	private CollectedItemCache collectedItemCache = new CollectedItemCache();

	private List<Point> tmpRequestList = new ArrayList<Point>();

	private int requestRadiusInTiles = 100;
	private int lastRequestCenterTileX = 0;
	private int lastRequestCenterTileY = 0;
	private long requestCount = 0;
	private int chunkBufferSize;

	private List<Point> tmpRemoveList = new ArrayList<Point>();
	private List<Collectible> tmpRemoveList2 = new ArrayList<Collectible>();
	private List<Point> tmpRemoveList3 = new ArrayList<Point>();

	private Vector2 tmpVec1 = new Vector2();
	private Vector2 tmpVector1 = new Vector2();
	private Vector2 tmpVector2 = new Vector2();
	private Vector3 tmpVec3 = new Vector3();
	private Vector3 tmpVec4 = new Vector3();

	public ControllerPlanet(String planetName, PlanetConfig pPlanetConfig, ControllerPhysic controllerPhysic, Ani ani) {
		this.planetConfig = pPlanetConfig;
		this.controllerPhysic = controllerPhysic;
		this.ani = ani;


		OpusLoaderJson loader = new OpusLoaderJson();
		try {
			// load opus by a json file
			opus = loader.load("entities/planets/" + planetName + "/opusConfig.json");
			;
			com.nukethemoon.tools.opusproto.log.Log.logLevel = com.nukethemoon.tools.opusproto.log.Log.LogType.Error;

			chunkBufferSize = (requestRadiusInTiles / opus.getConfig().chunkSize) * 2;

			// add a callback to receive chunks
			opus.addChunkListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		typeInterpreter = toTypeInterpreter((ColorInterpreter) opus.getLayers().get(0).getInterpreter());
	}

	public void requestChunks(List<Point> chunkCoordinates) {

		List<Point> requestList = new ArrayList<Point>();
		for (Point coordinate : chunkCoordinates) {
			if (planetPartBuffer.get(coordinate) == null) {
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

	public void updateRequestCenter(Vector3 position, Vector3 direction) {
		tmpVec1.set((float) (Math.floor(position.x) / TILE_GRAPHIC_SIZE), (float) (Math.floor(position.y) / TILE_GRAPHIC_SIZE));
		//tmpVec2.set(direction.x, direction.y).nor().scl(-1).scl(requestRadiusInTiles * 0.7f);
		//tmpVec1.sub(tmpVec2);

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

		int chunkSize = opus.getConfig().chunkSize - 1; // overlapping chunks
		int chunkBufferCenterX = getChunkX(requestCenterTileX);
		int chunkBufferCenterY = getChunkY(requestCenterTileY);

		currentVisiblePlanetParts.clear();

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
					currentVisiblePlanetParts.add(new Point(currentChunkX, currentChunkY));
				}
			}
		}

		tmpRequestList.clear();
		tmpRemoveList.clear();


		// remove non visible chunks
		for (Map.Entry<Point, PlanetPart> entry : planetPartBuffer.entrySet()) {
			if (!currentVisiblePlanetParts.contains(entry.getKey())) {
				PlanetPart c = entry.getValue();
				for (btRigidBody body : c.rigidBodyList) {
					controllerPhysic.removeRigidBody(body);
				}
				for (btCollisionObject object : c.collisionObjectList) {
					controllerPhysic.removeCollisionObject(object);
				}
				tmpRemoveList.add(entry.getKey());
			}
		}
		for (Point p : tmpRemoveList) {
			disposePlanetPart(p);
		}

		// add chunks that are not already loaded
		for (Point p : currentVisiblePlanetParts) {
			if (planetPartBuffer.get(p) == null) {
				tmpRequestList.add(p);
			}
		}

		// request visible chunks
		if (tmpRequestList.size() > 0) {
			collectedItemCache.update(chunkBufferCenterX, chunkBufferCenterY);
			requestChunks(tmpRequestList);
		}
	}

	private int getChunkX(int tilePositionX) {
		int chunkSize = opus.getConfig().chunkSize - 1; // overlapping chunks
		return (int) Math.floor(tilePositionX / chunkSize);
	}

	private int getChunkY(int tilePositionY) {
		int chunkSize = opus.getConfig().chunkSize - 1; // overlapping chunks
		return (int) Math.floor(tilePositionY / chunkSize);
	}

	private void disposePlanetPart(Point position) {
		PlanetPart planetPart = planetPartBuffer.get(position);
		tmpRemoveList2.clear();
		for (Collectible c : planetPart.getCollectibles()) {
			tmpRemoveList2.add(c);
		}
		for (Collectible c : tmpRemoveList2) {
			removeCollectible(c);
		}
		planetPart.dispose();
		planetPartBuffer.remove(position);
	}

	@Override
	public void onChunkCreated(int x, int y, Chunk chunk) {
		Point point = new Point(x, y);
		if (planetPartBuffer.get(point) == null) {
			PlanetPart planetPart = new PlanetPart(chunk, TILE_GRAPHIC_SIZE, planetConfig,
					typeInterpreter, collectedItemCache, opus.getConfig().seed);

			for (btRigidBody body : planetPart.rigidBodyList) {
				controllerPhysic.addRigidBody(body,
						com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes.byMask((short) body.getUserValue()));
			}
			for (btCollisionObject object : planetPart.collisionObjectList) {
				controllerPhysic.addCollisionObject(object);
			}

			for (Collectible c : planetPart.getCollectibles()) {
				addCollectible(c);
			}

			for (ArtifactObject o : planetPart.getArtifactObjects()) {
				addArtifact(o);
			}

			planetPartBuffer.put(point, planetPart);
		} else {
			Log.d(getClass(), "Created a chunk that already exists. x " + x + " y " + y);
		}
	}

	public void render(ModelBatch batch, Environment environment, boolean planetOnly) {
		for (Map.Entry<Point, PlanetPart> entry : planetPartBuffer.entrySet()) {
			PlanetPart planetPart = entry.getValue();
			renderEnv(planetPart.getModelInstance(), batch, environment);
		}

		if (planetOnly) {
			return;
		}

		for (Collectible c : currentVisibleCollectibles) {
			renderEnv(c.getModelInstance(), batch, environment);
		}

		for (ArtifactObject o : currentVisibleArtifacts) {
			renderEnv(o.getModelInstance(), batch, environment);
			tmpVec3.set(o.getDefinition().x * TILE_GRAPHIC_SIZE,
					o.getDefinition().y * TILE_GRAPHIC_SIZE, 100);
			controllerPhysic.calculateGroundIntersection(tmpVec3, tmpVec4);
			o.adjust(tmpVec4.z);
		}
	}

	private void renderEnv(ModelInstance model, ModelBatch batch, Environment environment) {
		if (environment != null) {
			batch.render(model, environment);
		} else {
			batch.render(model);
		}
	}



	public Collectible getCollectible(btCollisionObject collisionObject) {
		for (Collectible c : currentVisibleCollectibles) {
			if (c.getCollisionObject() == collisionObject) {
				return c;
			}
		}
		return null;
	}

	public void addCollectible(Collectible c) {
		controllerPhysic.addCollisionObject(c.getCollisionObject());
		currentVisibleCollectibles.add(c);
		ani.add(c.createScaleAnimation());
	}

	private void addArtifact(ArtifactObject o) {
		currentVisibleArtifacts.add(o);
	}

	public void removeCollectible(Collectible c) {
		currentVisibleCollectibles.remove(c);
		controllerPhysic.removeCollisionObject(c.getCollisionObject());
		c.dispose(ani);
		planetPartBuffer.get(c.getPlanetPartPosition()).getCollectibles().remove(c);
	}

	public void removeArtifact(ArtifactObject o) {
		currentVisibleArtifacts.remove(o);
		o.dispose();
	}

	public void dispose() {
		tmpRemoveList3.clear();
		for (Point p : planetPartBuffer.keySet()) {
			tmpRemoveList3.add(p);
		}
		for (Point p : tmpRemoveList3) {
			disposePlanetPart(p);
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

	public CollectedItemCache getCollectedItemCache() {
		return collectedItemCache;
	}

	public PlanetConfig getPlanetConfig() {
		return planetConfig;
	}

	public List<Collectible> getCurrentVisibleCollectibles() {
		return currentVisibleCollectibles;
	}
}
