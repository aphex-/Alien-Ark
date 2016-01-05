package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.nukethemoon.libgdxjam.screens.planet.CollisionTypes;
import com.nukethemoon.libgdxjam.screens.planet.PlanetConfig;
import com.nukethemoon.tools.opusproto.interpreter.TypeInterpreter;
import com.nukethemoon.tools.opusproto.region.Chunk;

import java.util.ArrayList;
import java.util.List;

public class PlanetPart extends GameObject {


	private static final float LANDSCAPE_MAX_HEIGHT = 20;

	private final int VERTEX_ATTRIBUTES = VertexAttributes.Usage.Position
			| VertexAttributes.Usage.ColorUnpacked
			| VertexAttributes.Usage.Normal;


	private static final String LANDSCAPE_NODE_NAME = "LANDSCAPE_NODE";
	private static final String LANDSCAPE_PART_NAME = "LANDSCAPE_PART";

	private final ModelInstance modelInstance;


	private final ModelBuilder modelBuilder;
	private final Model model;

	private float tileSize;

	private Vector3 tmpCorner0 = new Vector3();
	private Vector3 tmpCorner1 = new Vector3();
	private Vector3 tmpCorner2 = new Vector3();
	private Vector3 tmpCorner3 = new Vector3();

	private MeshPartBuilder.VertexInfo tmpVertexInfo1 = new MeshPartBuilder.VertexInfo();
	private MeshPartBuilder.VertexInfo tmpVertexInfo2 = new MeshPartBuilder.VertexInfo();
	private MeshPartBuilder.VertexInfo tmpVertexInfo3 = new MeshPartBuilder.VertexInfo();

	private Vector3 tmpNormal;
	private PlanetConfig planetConfig;
	private TypeInterpreter interpreter;

	private List<MeshBuilder> meshBuilders = new ArrayList<MeshBuilder>();

	public PlanetPart(Chunk chunk, float tileSize, PlanetConfig pPlanetConfig, TypeInterpreter interpreter) {
		this.tileSize = tileSize;
		this.planetConfig = pPlanetConfig;
		this.interpreter = interpreter;

		// create MeshBuilder for every landscape layer
		for (int i = 0; i < interpreter.it.size(); i++) {
			meshBuilders.add(new MeshBuilder());
		}
		modelBuilder = new ModelBuilder();

		modelBuilder.begin();
		createLandscapePart(chunk);
		for (int i = 0; i < pPlanetConfig.layerConfigs.size(); i++) {
			PlanetConfig.LandscapeLayerConfig layerConfig = pPlanetConfig.layerConfigs.get(i);
			if (CollisionTypes.byName(layerConfig.collisionType) == CollisionTypes.WATER) {
				createWaterPart(chunk, i);
			}
		}
		model = modelBuilder.end();


		modelInstance = new ModelInstance(model);
		modelInstance.transform.translate(
				chunk.getChunkX() * (chunk.getWidth() - 1) * tileSize,
				chunk.getChunkY() * (chunk.getHeight() - 1) * tileSize,
				0);

		for (int landscapeLayerIndex = 0; landscapeLayerIndex < planetConfig.layerConfigs.size(); landscapeLayerIndex++) {
			String partName = LANDSCAPE_NODE_NAME + landscapeLayerIndex;
			Node landscapeNode = model.getNode(partName);
			if (areAllPartsValid(landscapeNode)) {
				btCollisionShape collisionShape = Bullet.obtainStaticNodeShape(landscapeNode, false);
				float mass = 0;
				float friction = 1;
				PlanetConfig.LandscapeLayerConfig layerConfig = planetConfig.layerConfigs.get(landscapeLayerIndex);
				int userValue = CollisionTypes.byName(layerConfig.collisionType).mask;
				addRigidBody(collisionShape, mass, friction, userValue, modelInstance.transform);
			}
		}
	}

	private boolean areAllPartsValid(Node node) {
		for (NodePart nodePart : node.parts) {
			boolean usesTriangles = (nodePart.meshPart.primitiveType == GL20.GL_TRIANGLES);
			if ((nodePart.meshPart.size <= 0) || !usesTriangles) {
				return false;
			}
		}
		return true;
	}

	private void createLandscapePart(Chunk chunk) {

		// begin with all mesh builders for the landscape (except the water layer)
		for (int landscapeLayerIndex = 0; landscapeLayerIndex < meshBuilders.size(); landscapeLayerIndex++) {
			if (CollisionTypes.byName(planetConfig.layerConfigs.get(landscapeLayerIndex).collisionType) != CollisionTypes.WATER)  {
				MeshBuilder meshBuilder = meshBuilders.get(landscapeLayerIndex);
				meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);
			}
		}

 		int chunkSize = chunk.getWidth() - 1; // overlap 1

		// iterate over all tiles of the chunk
		for (int y = 0; y < chunkSize; y++) {
			for (int x = 0; x < chunkSize; x++) {
				float offsetX = tileSize * x;
				float offsetY = tileSize * y;
				float height0 = chunk.getRelative(x, y, 0);
				float height1 = chunk.getRelative(x, y + 1, 0);
				float height2 = chunk.getRelative(x + 1, y + 1, 0);
				float height3 = chunk.getRelative(x + 1, y, 0);

				if (!areAllHeightsWater(height0, height1, height2, height3)) {
					createTile(offsetX, offsetY, height0, height1, height2, height3);
				}
			}
		}


		for (int landscapeLayerIndex = 0; landscapeLayerIndex < meshBuilders.size(); landscapeLayerIndex++) {
			String collisionType = planetConfig.layerConfigs.get(landscapeLayerIndex).collisionType;
			if (CollisionTypes.byName(collisionType) != CollisionTypes.WATER) {

				modelBuilder.node().id = LANDSCAPE_NODE_NAME + landscapeLayerIndex;

				Mesh mesh = meshBuilders.get(landscapeLayerIndex).end();
				Material landscapeMaterial = planetConfig.layerConfigs.get(landscapeLayerIndex).material;
				// id, mesh, primitiveType, offset, size, material
				modelBuilder.part(LANDSCAPE_PART_NAME + landscapeLayerIndex, mesh, GL20.GL_TRIANGLES, 0, mesh.getNumIndices(), landscapeMaterial);

			}
		}
	}

	private boolean areAllHeightsWater(float height0, float height1, float height2, float height3) {
		return CollisionTypes.byName(planetConfig.layerConfigs.get(interpreter.getType(height0)).collisionType) == CollisionTypes.WATER
				&& CollisionTypes.byName(planetConfig.layerConfigs.get(interpreter.getType(height1)).collisionType) == CollisionTypes.WATER
					&& CollisionTypes.byName(planetConfig.layerConfigs.get(interpreter.getType(height2)).collisionType) == CollisionTypes.WATER
						&& CollisionTypes.byName(planetConfig.layerConfigs.get(interpreter.getType(height3)).collisionType) == CollisionTypes.WATER;

	}


	private void createTile(float offsetX, float offsetY, float height0, float height1, float height2, float height3) {

		tmpCorner0.set(offsetX, offsetY, height0 * LANDSCAPE_MAX_HEIGHT);
		tmpCorner1.set(offsetX, offsetY + tileSize, height1 * LANDSCAPE_MAX_HEIGHT);
		tmpCorner2.set(offsetX + tileSize, offsetY + tileSize, height2 * LANDSCAPE_MAX_HEIGHT);
		tmpCorner3.set(offsetX + tileSize, offsetY, height3 * LANDSCAPE_MAX_HEIGHT);

		int topologyIndex = getTopologyIndex(height0, height1, height2, height3);

					/*
						Tile:
						   c1______c2		   c1______c2
							|    /|				|\    |
							|  /  |				|  \  |
							|/____|				|____\|
						  c0		c3		   c0	   c3

							Topology1				Topology2
					*/

		MeshBuilder builder;

		if (topologyIndex == 0) {

			float averageHeightRect0 = (height0 + height1 + height2) / 3f;
			float averageHeightRect1 = (height2 + height3 + height0) / 3f;


			builder = meshBuilders.get(getNonWaterLayerIndex(height0));
			tmpNormal = calcNormal(tmpCorner0, tmpCorner1, tmpCorner2);
			tmpVertexInfo1.set(tmpCorner0, tmpNormal, null, null);
			tmpVertexInfo2.set(tmpCorner1, tmpNormal, null, null);
			tmpVertexInfo3.set(tmpCorner2, tmpNormal, null, null);
			builder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);

			builder = meshBuilders.get(getNonWaterLayerIndex(height0));
			tmpNormal = calcNormal(tmpCorner2, tmpCorner3, tmpCorner0);
			tmpVertexInfo1.set(tmpCorner2, tmpNormal, null, null);
			tmpVertexInfo2.set(tmpCorner3, tmpNormal, null, null);
			tmpVertexInfo3.set(tmpCorner0, tmpNormal, null, null);
			builder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);
		} else {

			float averageHeightRect0 = (height0 + height1 + height3) / 3f;
			float averageHeightRect1 = (height2 + height3 + height1) / 3f;

			builder = meshBuilders.get(getNonWaterLayerIndex(height0));
			tmpNormal = calcNormal(tmpCorner0, tmpCorner1, tmpCorner3);
			tmpVertexInfo1.set(tmpCorner0, tmpNormal, null, null);
			tmpVertexInfo2.set(tmpCorner1, tmpNormal, null, null);
			tmpVertexInfo3.set(tmpCorner3, tmpNormal, null, null);
			builder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);


			builder = meshBuilders.get(getNonWaterLayerIndex(height0));
			tmpNormal = calcNormal(tmpCorner2, tmpCorner3, tmpCorner1);
			tmpVertexInfo1.set(tmpCorner2, tmpNormal, null, null);
			tmpVertexInfo2.set(tmpCorner3, tmpNormal, null, null);
			tmpVertexInfo3.set(tmpCorner1, tmpNormal, null, null);
			builder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);
		}
	}

	private int getNonWaterLayerIndex(float heightValue) {
		int layerIndex = interpreter.getType(heightValue);
		String collisionType = planetConfig.layerConfigs.get(layerIndex).collisionType;
		if (CollisionTypes.byName(collisionType) == CollisionTypes.WATER) {
			return layerIndex + 1;
		}
		return layerIndex;
	}

	private int getTopologyIndex(float height00, float height01, float height02, float height03) {
		float topology01Distance = getDistance(height00, height01, height02)
				+ getDistance(height02, height03, height00);

		float topology02Distance = getDistance(height00, height01, height03)
				+ getDistance(height01, height02, height03);

		if (topology01Distance < topology02Distance) {
			return 0;
		} else {
			return 1;
		}
	}

	private float getDistance(float value00, float value01, float value02) {
		return Math.abs(value00 - value01) + Math.abs(value01 - value02);
	}

	private Vector3 calcNormal(Vector3 v01, Vector3 v02, Vector3 v03) {
		Vector3 seg1 = v01.cpy(); seg1.sub(v02);
		Vector3 seg2 = v03.cpy(); seg2.sub(v02);
		return seg1.crs(seg2).nor();
	}

	/**
	 * Creates a water plane.
	 * @param chunk The chunk.
	 */
	private void createWaterPart(Chunk chunk, int landscapeIndex) {
		MeshBuilder builder = meshBuilders.get(landscapeIndex);
		float WATER_HEIGHT = interpreter.it.get(landscapeIndex).endValue;
		builder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);
		float z = WATER_HEIGHT * LANDSCAPE_MAX_HEIGHT;
		float width = chunk.getWidth() * tileSize;
		float height = chunk.getHeight() * tileSize;
		Vector3 corner01 = new Vector3(0f, 0f, z);
		Vector3 corner02 = new Vector3(width, 0f, z);
		Vector3 corner03 = new Vector3(width, height, z);
		Vector3 corner04 = new Vector3(0f, height, z);
		builder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));
		Material waterMaterial = planetConfig.layerConfigs.get(landscapeIndex).material;
		Mesh mesh = builder.end();
		modelBuilder.node().id = LANDSCAPE_NODE_NAME + landscapeIndex;
		modelBuilder.part(LANDSCAPE_PART_NAME + landscapeIndex, mesh, GL20.GL_TRIANGLES, waterMaterial);
	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void dispose() {
		//model.dispose();
		for (btRigidBody b : rigidBodyList) {
			b.dispose();
		}
	}
}
