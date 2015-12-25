package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class ChunkGraphic {


	private static final float LANDSCAPE_MAX_HEIGHT = 3;
	private final int VERTEX_ATTRIBUTES = VertexAttributes.Usage.Position
			| VertexAttributes.Usage.ColorPacked
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


		//long now = System.currentTimeMillis();
		//Log.l(ChunkGraphic.class, "Created Chunk Graphic x " + chunk.getChunkX() + " y " + chunk.getChunkY() + " in " + (now - l) + " millis.");
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
		Vector3 normal = new Vector3(0, 0, 1);

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


				meshBuilder.setColor(interpreter.getColor(h0));
				meshBuilder.triangle(corner02, corner01, corner00);
				meshBuilder.triangle(corner03, corner02, corner00);

			}
		}
		Material baseMaterial = new Material();

		modelBuilder.part("BASE", meshBuilder.end(), GL20.GL_TRIANGLES, baseMaterial);
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


	/*static Vector3 calculateFaceNormal(Vector3 v1,Vector3 v2,Vector3 v3){
		Vector3 crossProduct;
		crossProduct = v2.sub(v1).scl(v3.sub(v2));
		return crossProduct.nor();
	}*/

	public ModelInstance getModelInstance() {
		return modelInstance;
	}


}
