package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
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

	private final ModelInstance modelInstance;

	private static MaterialInterpreter interpreter;
	private final MeshBuilder meshBuilder;
	private final ModelBuilder modelBuilder;
	private Chunk chunk;


	public ChunkGraphic(Chunk chunk, float tileSize) {
		this.chunk = chunk;


		interpreter = new MaterialInterpreter();
		meshBuilder = new MeshBuilder();
		modelBuilder = new ModelBuilder();

		modelBuilder.begin();
		createLandscapePart(tileSize, chunk);
		createWaterPart(tileSize, chunk);

		modelInstance = new ModelInstance(modelBuilder.end());
		modelInstance.transform.translate(
				chunk.getChunkX() * chunk.getWidth() * tileSize,
				chunk.getChunkY() * chunk.getHeight() * tileSize,
				0);

	}





	private void createLandscapePart(float tileSize, Chunk chunk) {

		Vector3 corner0 = new Vector3();
		Vector3 corner1 = new Vector3();
		Vector3 corner2 = new Vector3();
		Vector3 corner3 = new Vector3();

		Vector3 tmpCorner = new Vector3();

		MeshPartBuilder.VertexInfo vertexInfo1 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo vertexInfo2 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo vertexInfo3 = new MeshPartBuilder.VertexInfo();

		Vector3 normal;

		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);


		for (int y = 0; y < Math.abs(chunk.getHeight()); y++) {
			for (int x = 0; x < Math.abs(chunk.getWidth()); x++) {

				float offsetX = tileSize * x;
				float offsetY = tileSize * y;
				float h0 = chunk.getRelative(x, y, 0);
				float h1 = h0;
				float h2 = h0;
				float h3 = h0;

				// neighbour heights
				if (y + 1 < chunk.getHeight()) {
					h1 = chunk.getRelative(x, y + 1, 0);
					if (x + 1 < chunk.getWidth()) {
						h2 = chunk.getRelative(x + 1, y + 1, 0);
					}
				}
				if (x + 1 < chunk.getWidth()) {
					h3 = chunk.getRelative(x + 1, y, 0);
				}

				corner0.set(offsetX, offsetY, h0 * LANDSCAPE_MAX_HEIGHT);
				corner1.set(offsetX, offsetY + tileSize, h1 * LANDSCAPE_MAX_HEIGHT);
				corner2.set(offsetX + tileSize, offsetY + tileSize, h2 * LANDSCAPE_MAX_HEIGHT);
				corner3.set(offsetX + tileSize, offsetY, h3 * LANDSCAPE_MAX_HEIGHT);

				int topologyIndex = getTopologyIndex(h0, h1, h2, h3);

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

					float averageHeightRect0 = (h0 + h1 + h2) / 3f;
					float averageHeightRect1 = (h2 + h3 + h0) / 3f;

					Color color = interpreter.getColor(averageHeightRect0);

					normal = calcNormal(corner0, corner1, corner2);
					vertexInfo1.set(corner0, normal, color, null);
					vertexInfo2.set(corner1, normal, color, null);
					vertexInfo3.set(corner2, normal, color, null);
					meshBuilder.triangle(vertexInfo3, vertexInfo2, vertexInfo1);


					color = interpreter.getColor(averageHeightRect1);

					normal = calcNormal(corner2, corner3, corner0);
					vertexInfo1.set(corner2, normal, color, null);
					vertexInfo2.set(corner3, normal, color, null);
					vertexInfo3.set(corner0, normal, color, null);
					meshBuilder.triangle(vertexInfo3, vertexInfo2, vertexInfo1);
				} else {

					float averageHeightRect0 = (h0 + h1 + h3) / 3f;
					float averageHeightRect1 = (h2 + h3 + h1) / 3f;

					Color color = interpreter.getColor(averageHeightRect0);

					normal = calcNormal(corner0, corner1, corner3);
					vertexInfo1.set(corner0, normal, color, null);
					vertexInfo2.set(corner1, normal, color, null);
					vertexInfo3.set(corner3, normal, color, null);
					meshBuilder.triangle(vertexInfo3, vertexInfo2, vertexInfo1);


					color = interpreter.getColor(averageHeightRect1);

					normal = calcNormal(corner2, corner3, corner1);
					vertexInfo1.set(corner2, normal, color, null);
					vertexInfo2.set(corner3, normal, color, null);
					vertexInfo3.set(corner1, normal, color, null);
					meshBuilder.triangle(vertexInfo3, vertexInfo2, vertexInfo1);
				}

			}
		}

		Material baseMaterial = new Material(
				new ColorAttribute(ColorAttribute.Diffuse, Color.LIGHT_GRAY));
		//Material baseMaterial = new Material();


		modelBuilder.part("BASE", meshBuilder.end(), GL20.GL_TRIANGLES, baseMaterial);
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

	private void createWaterPart(float tileSize, Chunk chunk) {
		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);

		float z = 0.009f * LANDSCAPE_MAX_HEIGHT;
		float width = chunk.getWidth() * tileSize;
		float height = chunk.getHeight() * tileSize;

		Vector3 corner01 = new Vector3(0f, 0f, z);
		Vector3 corner02 = new Vector3(width, 0f, z);
		Vector3 corner03 = new Vector3(width, height, z);
		Vector3 corner04 = new Vector3(0f, height, z);

		meshBuilder.setColor(0, 0, 1, 0.5f);
		meshBuilder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));

		Material waterMaterial = new Material(
				ColorAttribute.createReflection(Color.WHITE),
				new ColorAttribute(ColorAttribute.Specular, 1, 1, 1, 1));

		modelBuilder.part("WATER", meshBuilder.end(), GL20.GL_TRIANGLES, waterMaterial);
	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}


	public Chunk getChunk() {
		return chunk;
	}


}
