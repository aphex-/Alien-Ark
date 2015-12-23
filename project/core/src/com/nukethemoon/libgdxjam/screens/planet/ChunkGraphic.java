package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class ChunkGraphic {


	private static final float RECT_SIZE = 5;
	private static final float MAX_HEIGHT = 5;

	private final ModelInstance modelInstance;
	private final ShaderProgram shaderProgram;


	public ChunkGraphic(Chunk chunk) {

		MeshBuilder meshBuilder = new MeshBuilder();

		shaderProgram = new ShaderProgram(
				new FileHandle("shaders/default.vertex.glsl"),
				new FileHandle("shaders/default.fragment.glsl"));

		if (Config.DEBUG && !shaderProgram.isCompiled()) {
			Log.l(LayerMesh.class, "Shader log " + shaderProgram.getLog());
		}

		int usage = VertexAttributes.Usage.Position
				| VertexAttributes.Usage.ColorUnpacked
				| VertexAttributes.Usage.Normal;

		ModelBuilder mob = new ModelBuilder();
		mob.begin();

		for (int y = 0; y < chunk.getHeight(); y++) {
			for (int x = 0; x < chunk.getWidth(); x++) {
				meshBuilder.begin(usage, GL20.GL_TRIANGLES);

				float height = chunk.getAbsolute(x, y, 0);

				Vector3 corner01 = new Vector3(
						RECT_SIZE * x,
						RECT_SIZE * y,
						height * MAX_HEIGHT);

				Vector3 corner02 = new Vector3(
						RECT_SIZE * x + RECT_SIZE,
						RECT_SIZE * y,
						height * MAX_HEIGHT);

				Vector3 corner03 = new Vector3(
						RECT_SIZE * x + RECT_SIZE,
						RECT_SIZE * y + RECT_SIZE,
						height * MAX_HEIGHT);

				Vector3 corner04 = new Vector3(
						RECT_SIZE * x,
						RECT_SIZE * y + RECT_SIZE,
						height * MAX_HEIGHT);

				meshBuilder.rect(corner01, corner02, corner03, corner04, new Vector3(0, 0, 1));

				Material material = new Material(ColorAttribute.createDiffuse(Color.RED),
						ColorAttribute.createSpecular(1, 1, 1, 1),
						FloatAttribute.createShininess(8f));
				mob.part("mesh1", meshBuilder.end(), GL20.GL_TRIANGLES, material);
			}
		}


		modelInstance = new ModelInstance(mob.end());

	}


	public ModelInstance getModelInstance() {
		return modelInstance;
	}


}
