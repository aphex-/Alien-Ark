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


	private static final float LANDSCAPE_MAX_HEIGHT = 3;
	private final int VERTEX_ATTRIBUTES = VertexAttributes.Usage.Position
			| VertexAttributes.Usage.ColorUnpacked
			| VertexAttributes.Usage.Normal;

	private final ModelInstance modelInstance;

	private static MaterialInterpreter interpreter;
	private final MeshBuilder meshBuilder;
	private final ModelBuilder modelBuilder;


	public ChunkGraphic(Chunk chunk, float tileSize) {


		interpreter = new MaterialInterpreter();
		meshBuilder = new MeshBuilder();
		modelBuilder = new ModelBuilder();

		modelBuilder.begin();
		createLandscapePart(tileSize, chunk);
		//createWaterPart(tileSize, chunk);

		modelInstance = new ModelInstance(modelBuilder.end());
		modelInstance.transform.translate(
				chunk.getChunkX() * chunk.getWidth() * tileSize,
				chunk.getChunkY() * chunk.getHeight() * tileSize,
				0);

	}



	/*
	         c1______c2
	          |    /|
	          |  /  |
	          |/____|
	         c0		c3

	 */

	private void createLandscapePart(float tileSize, Chunk chunk) {


		Vector3 corner00 = new Vector3();
		Vector3 corner01 = new Vector3();
		Vector3 corner02 = new Vector3();
		Vector3 corner03 = new Vector3();

		MeshPartBuilder.VertexInfo vertexInfo01 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo vertexInfo02 = new MeshPartBuilder.VertexInfo();
		MeshPartBuilder.VertexInfo vertexInfo03 = new MeshPartBuilder.VertexInfo();

		Vector3 normal;

		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);


		for (int y = 0; y < Math.abs(chunk.getHeight()); y++) {
			for (int x = 0; x < Math.abs(chunk.getWidth()); x++) {

				float h0 = chunk.getRelative(x, y, 0);
				float h1 = 0;
				float h2 = 0;
				float h3 = 0;

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



				float offsetX = tileSize * x;
				float offsetY = tileSize * y;

				corner00.set(offsetX, offsetY,
						h0 * LANDSCAPE_MAX_HEIGHT);
				corner01.set(offsetX, offsetY + tileSize,
						h1 * LANDSCAPE_MAX_HEIGHT);
				corner02.set(offsetX + tileSize, offsetY + tileSize,
						h2 * LANDSCAPE_MAX_HEIGHT);
				corner03.set(offsetX + tileSize, offsetY,
						h3 * LANDSCAPE_MAX_HEIGHT);

				Color color = interpreter.getColor(h0);

				normal = calcNormal(corner00, corner01, corner02);
				vertexInfo01.set(corner00, normal, color, null);
				vertexInfo02.set(corner01, normal, color, null);
				vertexInfo03.set(corner02, normal, color, null);
				meshBuilder.triangle(vertexInfo03, vertexInfo02, vertexInfo01);

				normal = calcNormal(corner02, corner03, corner00);
				vertexInfo01.set(corner02, normal, color, null);
				vertexInfo02.set(corner03, normal, color, null);
				vertexInfo03.set(corner00, normal, color, null);
				meshBuilder.triangle(vertexInfo03, vertexInfo02, vertexInfo01);

			}
		}

		Material baseMaterial = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.GRAY));
		//Material baseMaterial = new Material();


		modelBuilder.part("BASE", meshBuilder.end(), GL20.GL_TRIANGLES, baseMaterial);
	}

	private Vector3 calcNormal(Vector3 v01, Vector3 v02, Vector3 v03) {
		Vector3 seg1 = v01.cpy(); seg1.sub(v02);
		Vector3 seg2 = v03.cpy(); seg2.sub(v02);
		return seg1.crs(seg2).nor();
	}

	private void createWaterPart(float tileSize, Chunk chunk) {
		meshBuilder.begin(VERTEX_ATTRIBUTES, GL20.GL_TRIANGLES);
		Vector3 corner01 = new Vector3(
				0f,
				0f,
				0.1f * LANDSCAPE_MAX_HEIGHT);

		Vector3 corner02 = new Vector3(
				0f,
				chunk.getHeight() * tileSize,
				0.1f * LANDSCAPE_MAX_HEIGHT);

		Vector3 corner03 = new Vector3(
				chunk.getWidth() * tileSize,
				chunk.getHeight() * tileSize,
				0.1f * LANDSCAPE_MAX_HEIGHT);

		Vector3 corner04 = new Vector3(
				chunk.getWidth() * tileSize,
				0F,
				0.1f * LANDSCAPE_MAX_HEIGHT);

		meshBuilder.setColor(0, 0, 1, 0.5f);
		meshBuilder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));

		//ColorAttribute reflection = ColorAttribute.createReflection(Color.WHITE);
		Material waterMaterial = new Material();
		modelBuilder.part("WATER", meshBuilder.end(), GL20.GL_TRIANGLES, waterMaterial);
	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}


}
