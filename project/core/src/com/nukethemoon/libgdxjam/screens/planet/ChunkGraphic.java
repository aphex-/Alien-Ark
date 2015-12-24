package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class ChunkGraphic {


	private static final float MAX_HEIGHT = 3;

	private final ModelInstance modelInstance;

	private static MaterialInterpreter interpreter = new MaterialInterpreter();


	public ChunkGraphic(Chunk chunk, float tileSize) {

		long l = System.currentTimeMillis();


		MeshBuilder meshBuilder = new MeshBuilder();

		int usage = VertexAttributes.Usage.Position
				| VertexAttributes.Usage.ColorUnpacked
				| VertexAttributes.Usage.Normal;

		ModelBuilder mob = new ModelBuilder();
		mob.begin();

		meshBuilder.begin(usage, GL20.GL_TRIANGLES);
		for (int y = 0; y < Math.abs(chunk.getHeight()); y++) {
			for (int x = 0; x < Math.abs(chunk.getWidth()); x++) {

				float height = chunk.getRelative(x, y, 0);

				Vector3 corner01 = new Vector3(
						tileSize * x,
						tileSize * y,
						height * MAX_HEIGHT);

				Vector3 corner02 = new Vector3(
						tileSize * x + tileSize,
						tileSize * y,
						height * MAX_HEIGHT);

				Vector3 corner03 = new Vector3(
						tileSize * x + tileSize,
						tileSize * y + tileSize,
						height * MAX_HEIGHT);

				Vector3 corner04 = new Vector3(
						tileSize * x,
						tileSize * y + tileSize,
						height * MAX_HEIGHT);

				meshBuilder.setColor(interpreter.getColor(height));
				meshBuilder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));

			}
		}
		Material material = interpreter.getMaterial(0.5f);
		mob.part("mesh1", meshBuilder.end(), GL20.GL_TRIANGLES, material);

		modelInstance = new ModelInstance(mob.end());
		modelInstance.transform.translate(
				chunk.getChunkX() * chunk.getWidth() * tileSize,
				chunk.getChunkY() * chunk.getHeight() * tileSize,
				0);


		long now = System.currentTimeMillis();

		Log.l(ChunkGraphic.class, "Created Chunk Graphic x " + chunk.getChunkX() + " y " + chunk.getChunkY() + " in " + (now - l) + " millis.");
	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}


}
