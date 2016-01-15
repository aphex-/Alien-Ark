package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.ArtifactDefinitions;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.ArtifactObject;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Collectible;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.PlanetPart;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.PlanetPortal;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.RaceWayPoint;
import com.nukethemoon.libgdxjam.screens.planet.helper.SphereTextureProvider;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;
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

	public static float TILE_GRAPHIC_SIZE = 5f;
	public static float TRACTOR_BEAM_TOLERANCE_RADIUS = 5f;
	public static int INFINITE_WATER_SIZE = 10000;
	private static float infiniteWaterHeight;

	private final PlanetPortal planetPortal;

	private final Model waterModel;
	private final ModelInstance waterModelInstance;

	private Opus opus;
	private PlanetConfig planetConfig;
	private ControllerPhysic controllerPhysic;
	private Ani ani;

	private PlanetRaceListener raceListener;

	private List<Point> currentVisiblePlanetParts = new ArrayList<Point>();
	private List<Collectible> currentVisibleCollectibles = new ArrayList<Collectible>();
	private List<ArtifactObject> currentVisibleArtifacts = new ArrayList<ArtifactObject>();
	private List<RaceWayPoint> currentlyVisibleRaceWayPoints = new ArrayList<RaceWayPoint>();

	private List<RaceWayPoint> alreadyReachedWayPoints = new ArrayList<RaceWayPoint>();

	private List<ObjectPlacementInfo> allArtifactsOnPlanet = new ArrayList<ObjectPlacementInfo>();

	private Map<Point, PlanetPart> planetPartBuffer = new HashMap<Point, PlanetPart>();
	private final TypeInterpreter typeInterpreter;

	private CollectedItemCache collectedItemCache = new CollectedItemCache();

	private List<Point> tmpRequestList = new ArrayList<Point>();

	private int lastRequestCenterTileX = 0;
	private int lastRequestCenterTileY = 0;
	private long requestCount = 0;
	private int chunkBufferSize;

	private ModelInstance environmentSphere;
	private Model sphereModel;

	private List<Point> tmpRemoveList = new ArrayList<Point>();
	private List<Collectible> tmpRemoveList2 = new ArrayList<Collectible>();
	private List<Point> tmpRemoveList3 = new ArrayList<Point>();


	private Vector2 nearestArtifactPosition = new Vector2();

	private Vector2 tmpVec1 = new Vector2();
	private Vector2 tmpVector1 = new Vector2();
	private Vector2 tmpVector2 = new Vector2();
	private Vector3 tmpVec3 = new Vector3();
	private Vector3 tmpVec4 = new Vector3();
	private Vector2 tmpVec5 = new Vector2();
	private Vector2 tmpVec6 = new Vector2();
	private Vector3 tmpVec7 = new Vector3();
	private Vector3 tmpVec8 = new Vector3();


	public ControllerPlanet(String planetName, PlanetConfig pPlanetConfig, ControllerPhysic controllerPhysic, Ani ani,
							PlanetRaceListener raceListener) {

		this.planetConfig = pPlanetConfig;
		this.controllerPhysic = controllerPhysic;
		this.ani = ani;
		this.raceListener = raceListener;


		OpusLoaderJson loader = new OpusLoaderJson();
		try {
			// load opus by a json file
			opus = loader.load("entities/planets/" + planetName + "/opusConfig.json");
			;
			com.nukethemoon.tools.opusproto.log.Log.logLevel = com.nukethemoon.tools.opusproto.log.Log.LogType.Error;

			chunkBufferSize = (App.config.requestRadiusInTiles / opus.getConfig().chunkSize) * 2;

			// add a callback to receive chunks
			opus.addChunkListener(this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		typeInterpreter = toTypeInterpreter((ColorInterpreter) opus.getLayers().get(0).getInterpreter());

		// == init artifacts ==
		for (ObjectPlacementInfo p : pPlanetConfig.artifacts) {
			if (!SpaceShipProperties.properties.isArtifactCollected(p.id)) {
				allArtifactsOnPlanet.add(p);
			}
		}
		updateNearestArtifact(0, 0);

		// == init water ==
		waterModel = createInfiniteWaterPart(typeInterpreter, 0, pPlanetConfig);
		waterModelInstance = new ModelInstance(waterModel);

		// == init planet portal ==
		planetPortal = new PlanetPortal();
		for (btRigidBody body : planetPortal.rigidBodyList) {
			controllerPhysic.addRigidBody(body, CollisionTypes.GROUND);
		}
		for (btCollisionObject object : planetPortal.collisionObjectList) {
			controllerPhysic.addCollisionObject(object);
		}

		loadSphere(planetConfig.id);

	}

	private void loadSphere(String planetId) {
		ModelLoader loader = new ObjLoader();
		sphereModel = loader.loadModel(Gdx.files.internal("models/sphere01.obj"),
				new SphereTextureProvider(planetId));
		environmentSphere = new ModelInstance(sphereModel);
		Attribute attribute = environmentSphere.materials.get(0).get(
				ColorAttribute.getAttributeType(ColorAttribute.DiffuseAlias));

		((ColorAttribute) attribute).color.r = 1;
		((ColorAttribute) attribute).color.g = 1;
		((ColorAttribute) attribute).color.b = 1;

	}

	public static Model createInfiniteWaterPart(TypeInterpreter interpreter, int landscapeIndex, PlanetConfig planetConfig) {
		ModelBuilder modelBuilder = new ModelBuilder();
		MeshBuilder builder = new MeshBuilder();
		float waterHeight = interpreter.it.get(landscapeIndex).endValue;
		builder.begin(PlanetPart.VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);
		infiniteWaterHeight = waterHeight * planetConfig.landscapeHeight - 1;
		Vector3 corner01 = new Vector3(-INFINITE_WATER_SIZE, -INFINITE_WATER_SIZE, infiniteWaterHeight);
		Vector3 corner02 = new Vector3(INFINITE_WATER_SIZE, -INFINITE_WATER_SIZE, infiniteWaterHeight);
		Vector3 corner03 = new Vector3(INFINITE_WATER_SIZE, INFINITE_WATER_SIZE, infiniteWaterHeight);
		Vector3 corner04 = new Vector3(-INFINITE_WATER_SIZE, INFINITE_WATER_SIZE, infiniteWaterHeight);
		builder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));
		Material waterMaterial = planetConfig.layerConfigs.get(landscapeIndex).material;
		Mesh mesh = builder.end();
		modelBuilder.begin();
		modelBuilder.part(PlanetPart.LANDSCAPE_PART_NAME + landscapeIndex, mesh, GL20.GL_TRIANGLES, waterMaterial);
		return modelBuilder.end();
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

	public void updateNearestArtifact(float rocketX, float rocketY) {
		float shortestDistance = -1;
		nearestArtifactPosition = null;
		for(ObjectPlacementInfo p : allArtifactsOnPlanet) {
			float artifactX = p.x;
			float artifactY = p.y;
			float distance = tmpVec6.set(artifactX - rocketX, artifactY - rocketY).len();
			if (distance < shortestDistance || shortestDistance == -1) {
				shortestDistance = distance;
				nearestArtifactPosition = tmpVec5.set(artifactX, artifactY);
			}
		}
	}

	public Vector2 getNearestArtifactPosition() {
		return nearestArtifactPosition;
	}

	public void updateRequestCenter(Vector3 position) {
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
				if (Math.abs(tmpVector1.dst(tmpVector2)) < App.config.requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 2
				tmpVector1.set(currentChunkX * chunkSize, currentChunkY * chunkSize + chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < App.config.requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 3
				tmpVector1.set(currentChunkX * chunkSize + chunkSize, currentChunkY * chunkSize + chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < App.config.requestRadiusInTiles) {
					isInRadius = true;
				}
				// chunk tile corner 4
				tmpVector1.set(currentChunkX * chunkSize + chunkSize, currentChunkY * chunkSize);
				if (Math.abs(tmpVector1.dst(tmpVector2)) < App.config.requestRadiusInTiles) {
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

			for (RaceWayPoint r : planetPart.getRaceWayPoints()) {
				addRaceWayPoint(r);
			}

			planetPartBuffer.put(point, planetPart);
		} else {
			Log.d(getClass(), "Created a chunk that already exists. x " + x + " y " + y);
		}
	}


	public void render(ModelBatch batch, Environment environment, boolean planetOnly, Vector3 rocketPosition) {
		for (Map.Entry<Point, PlanetPart> entry : planetPartBuffer.entrySet()) {
			PlanetPart planetPart = entry.getValue();
			renderEnv(planetPart.getModelInstance(), batch, environment);
		}

		waterModelInstance.transform.setToTranslation(rocketPosition.x, rocketPosition.y, infiniteWaterHeight);
		renderEnv(waterModelInstance, batch, environment);

		if (planetOnly) {
			return;
		}

		planetPortal.render(batch, environment);

		environmentSphere.transform.idt();
		environmentSphere.transform.setToTranslation(rocketPosition.x, rocketPosition.y, 0);
		environmentSphere.transform.scl(1000);
		batch.render(environmentSphere);

		for (Collectible c : currentVisibleCollectibles) {
			renderEnv(c.getModelInstance(), batch, environment);
		}

		for (ArtifactObject o : currentVisibleArtifacts) {
			renderEnv(o.getModelInstance(), batch, environment);
		}

		for (RaceWayPoint r : currentlyVisibleRaceWayPoints) {
			renderEnv(r.getModelInstance(), batch, environment);
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

	public void removeCollectible(Collectible c) {
		currentVisibleCollectibles.remove(c);
		controllerPhysic.removeCollisionObject(c.getCollisionObject());
		c.dispose(ani);
		planetPartBuffer.get(c.getPlanetPartPosition()).getCollectibles().remove(c);
	}

	public void addArtifact(ArtifactObject o) {
		tmpVec3.set(o.getDefinition().x, o.getDefinition().y, 10000);
		controllerPhysic.calculateVerticalIntersection(tmpVec3, tmpVec4);
		o.adjust(tmpVec4.z);

		if (!SpaceShipProperties.properties.isArtifactCollected(o.getDefinition().id)) {
			currentVisibleArtifacts.add(o);
		}
	}

	public void removeArtifactModel(ArtifactObject o) {
		currentVisibleArtifacts.remove(o);
		o.dispose();
	}

	public ArtifactObject getCurrentVisibleArtifact(String id) {
		for (ArtifactObject o : currentVisibleArtifacts) {
			if (o.getDefinition().id.equals(id)) {
				return o;
			}
		}
		return null;
	}

	public ArtifactObject tryCollect(Vector3 position, float tractorBeamRadius) {
		for (ArtifactObject o : currentVisibleArtifacts) {
			o.getModelInstance().transform.getTranslation(tmpVec7);
			float distance = tmpVec7.sub(position).len();
			if (distance < (tractorBeamRadius + TRACTOR_BEAM_TOLERANCE_RADIUS)) {
				return o;
			}
		}
		return null;
	}

	public void collectArtifact(ArtifactObject o) {
		if (!SpaceShipProperties.properties.isArtifactCollected(o.getDefinition().id)) {
			removeArtifactModel(o);
			allArtifactsOnPlanet.remove(o.getDefinition());
			ArtifactDefinitions.ConcreteArtifactType concreteArtifactType
					= ArtifactDefinitions.ConcreteArtifactType.byName(o.getDefinition().type);
			SpaceShipProperties.properties.onArtifactCollected(concreteArtifactType.createArtifact(), o.getDefinition().id);
		}
	}

	public void addRaceWayPoint(RaceWayPoint r) {
		if (!alreadyReachedWayPoints.contains(r)) {
			tmpVec3.set(r.x, r.y, 10000);
			controllerPhysic.calculateVerticalIntersection(tmpVec3, tmpVec4);
			controllerPhysic.addCollisionObject(r.getTrigger());
			r.adjust(tmpVec4.z);
			currentlyVisibleRaceWayPoints.add(r);
		}
	}

	public void removeRaceWayPoint(RaceWayPoint r) {
		currentlyVisibleRaceWayPoints.remove(r);
		controllerPhysic.removeCollisionObject(r.getTrigger());
		r.dispose();
	}

	public void reachWayPoint(RaceWayPoint r) {
		if (r == planetConfig.planetRace.wayPoints.get(0)) {
			raceListener.onRaceStart();
		} else if (r == planetConfig.planetRace.wayPoints.get(planetConfig.planetRace.wayPoints.size() - 1)) {
			raceListener.onRaceSuccess();
		} else {
			raceListener.onRaceProgress();
		}
		alreadyReachedWayPoints.add(r);
		removeRaceWayPoint(r);
	}

	public RaceWayPoint getRaceWayPoint(btCollisionObject collision) {
		for (RaceWayPoint r : currentlyVisibleRaceWayPoints) {
			if (r.getTrigger() == collision) {
				return r;
			}
		}
		return null;
	}

	public void resetRace() {
		alreadyReachedWayPoints.clear();
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

	public List<ObjectPlacementInfo> getAllArtifactsOnPlanet() {
		return allArtifactsOnPlanet;
	}

	public interface PlanetRaceListener {
		void onRaceStart();
		void onRaceProgress();
		void onRaceTimeOut();
		void onRaceSuccess();
	}
}
