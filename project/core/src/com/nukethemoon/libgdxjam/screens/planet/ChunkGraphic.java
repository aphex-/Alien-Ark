package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class ChunkGraphic {


	private static final float LANDSCAPE_MAX_HEIGHT = 12;
	private final int VERTEX_ATTRIBUTES = VertexAttributes.Usage.Position
			| VertexAttributes.Usage.ColorUnpacked
			| VertexAttributes.Usage.Normal;

	private final static float WATER_HEIGHT = 0.1f;

	private final ModelInstance modelInstance;

	private static MaterialInterpreter interpreter;
	private final MeshBuilder meshBuilder;
	private final ModelBuilder modelBuilder;
	private final Model model;

	private Chunk chunk;
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

	public ChunkGraphic(Chunk chunk, float tileSize, PlanetConfig pPlanetConfig) {
		this.chunk = chunk;
		this.tileSize = tileSize;
		this.planetConfig = pPlanetConfig;


		interpreter = new MaterialInterpreter();
		meshBuilder = new MeshBuilder();
		modelBuilder = new ModelBuilder();

		modelBuilder.begin();
		createLandscapePart(chunk);
		createWaterPart(chunk);

		model = modelBuilder.end();
		modelInstance = new ModelInstance(model);
		modelInstance.transform.translate(
				chunk.getChunkX() * (chunk.getWidth() - 1) * tileSize,
				chunk.getChunkY() * (chunk.getHeight() - 1) * tileSize,
				0);
	}


	private void createLandscapePart(Chunk chunk) {

		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);

 		int chunkSize = chunk.getWidth() - 1; // overlap 1

		for (int y = 0; y < chunkSize; y++) {
			for (int x = 0; x < chunkSize; x++) {

				float offsetX = tileSize * x;
				float offsetY = tileSize * y;
				float height0 = chunk.getRelative(x, y, 0);
				float height1 = chunk.getRelative(x, y + 1, 0);
				float height2 = chunk.getRelative(x + 1, y + 1, 0);
				float height3 = chunk.getRelative(x + 1, y, 0);

				boolean tileIsUnderWater = (height0 < WATER_HEIGHT && height1 < WATER_HEIGHT && height2 < WATER_HEIGHT && height3 < WATER_HEIGHT);

				if (!tileIsUnderWater) {
					createTile(offsetX, offsetY, height0, height1, height2, height3);
				}
			}
		}

		Material landscapeMaterial = planetConfig.materials.get("Landscape01");
		modelBuilder.part("Landscape01", meshBuilder.end(), GL20.GL_TRIANGLES, landscapeMaterial);
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


		if (topologyIndex == 0) {

			float averageHeightRect0 = (height0 + height1 + height2) / 3f;
			float averageHeightRect1 = (height2 + height3 + height0) / 3f;

			Color color = interpreter.getColor(averageHeightRect0);

			tmpNormal = calcNormal(tmpCorner0, tmpCorner1, tmpCorner2);
			tmpVertexInfo1.set(tmpCorner0, tmpNormal, color, null);
			tmpVertexInfo2.set(tmpCorner1, tmpNormal, color, null);
			tmpVertexInfo3.set(tmpCorner2, tmpNormal, color, null);
			meshBuilder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);


			color = interpreter.getColor(averageHeightRect1);

			tmpNormal = calcNormal(tmpCorner2, tmpCorner3, tmpCorner0);
			tmpVertexInfo1.set(tmpCorner2, tmpNormal, color, null);
			tmpVertexInfo2.set(tmpCorner3, tmpNormal, color, null);
			tmpVertexInfo3.set(tmpCorner0, tmpNormal, color, null);
			meshBuilder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);
		} else {

			float averageHeightRect0 = (height0 + height1 + height3) / 3f;
			float averageHeightRect1 = (height2 + height3 + height1) / 3f;

			Color color = interpreter.getColor(averageHeightRect0);

			tmpNormal = calcNormal(tmpCorner0, tmpCorner1, tmpCorner3);
			tmpVertexInfo1.set(tmpCorner0, tmpNormal, color, null);
			tmpVertexInfo2.set(tmpCorner1, tmpNormal, color, null);
			tmpVertexInfo3.set(tmpCorner3, tmpNormal, color, null);
			meshBuilder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);


			color = interpreter.getColor(averageHeightRect1);

			tmpNormal = calcNormal(tmpCorner2, tmpCorner3, tmpCorner1);
			tmpVertexInfo1.set(tmpCorner2, tmpNormal, color, null);
			tmpVertexInfo2.set(tmpCorner3, tmpNormal, color, null);
			tmpVertexInfo3.set(tmpCorner1, tmpNormal, color, null);
			meshBuilder.triangle(tmpVertexInfo3, tmpVertexInfo2, tmpVertexInfo1);
		}
	}


		/*
		Tile:
	         c1______c2			   c1______c2
	          |    /|				|\    |
	          |  /  |				|  \  |
	          |/____|				|____\|
	         c0		c3			  c0	   c3

	         Topology0				Topology1
	 */

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

	private void createWaterPart(Chunk chunk) {
		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);

		float z = WATER_HEIGHT * LANDSCAPE_MAX_HEIGHT;
		float width = chunk.getWidth() * tileSize;
		float height = chunk.getHeight() * tileSize;

		Vector3 corner01 = new Vector3(0f, 0f, z);
		Vector3 corner02 = new Vector3(width, 0f, z);
		Vector3 corner03 = new Vector3(width, height, z);
		Vector3 corner04 = new Vector3(0f, height, z);

		meshBuilder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));

		Material waterMaterial = planetConfig.materials.get("Water01");

		modelBuilder.part("WATER", meshBuilder.end(), GL20.GL_TRIANGLES, waterMaterial);
	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}


	public void dispose() {
		model.dispose();
	}
}
