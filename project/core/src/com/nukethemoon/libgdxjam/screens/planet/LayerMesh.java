package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.nukethemoon.libgdxjam.Config;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.tools.opusproto.region.Chunk;

public class LayerMesh extends Mesh {

	private static final int RECT_SIZE = 2;
	private static final float MAX_HEIGHT = 10;

	private ShaderProgram shaderProgram;
	private Matrix4 modelViewMatrix = new Matrix4();

	public LayerMesh(Chunk chunk, int layerIndex) {
		super(true,
				getVertexCount(chunk),
				getIndexCount(chunk.getWidth(), chunk.getHeight()),
				new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));




		shaderProgram = new ShaderProgram(
				new FileHandle("shaders/grid.vertex.glsl"),
				new FileHandle("shaders/grid.fragment.glsl"));

		/*shaderProgram = new ShaderProgram(
				new FileHandle("shaders/default.vertex.glsl"),
				new FileHandle("shaders/default.fragment.glsl"));*/

		if (Config.DEBUG && !shaderProgram.isCompiled()) {
			Log.l(LayerMesh.class, "Shader log " + shaderProgram.getLog());
		}



		setVertices(createVertices(chunk, layerIndex));
		setIndices(createIndices(chunk.getWidth(), chunk.getHeight()));
	}



	/*
				25------26 29------30 33-------34
				|		 | |		| |			|
				|	6	 | |	7	| |		8	|
				|		 | |		| |			|
				24------27 28------31 32-------35  <== end
				13------14 17------18 21-------22
				|		 | |		| |			|
				|	3	 | |	4	| |		5 	|
				|		 | |		| |			|
				12------15 16------19 20-------23
				1--------2 5--------6 9--------10
				|		 | |		| |			|
				|	0	 | |	1	| |		2	|
				|		 | |		| |			|
	start ==>	0--------3 4--------7 8--------11
	 */

	private static short[] createIndices(int width, int height) {
		short[] indices = new short[getIndexCount(width, height)];

		Log.l(LayerMesh.class, "index count " + getIndexCount(width, height));

		for (int y = 0; y < width; y++) {
			for (int x = 0; x < height; x++) {
				short rectIndex = (short) getRectIndex(x, y, width, height);

				int indexOffset = rectIndex * 6;
				// triangle 1
				indices[indexOffset] = rectIndex;
				indices[indexOffset + 1] = (short) (rectIndex + 1);
				indices[indexOffset + 2] = (short) (rectIndex + 2);

				// triangle 2
				indices[indexOffset + 3] = (short) (rectIndex + 2);
				indices[indexOffset + 4] = (short) (rectIndex + 3);
				indices[indexOffset + 5] = rectIndex;
			}
		}
		return indices;
	}

	private static int getRectIndex(int x, int y, int width, int height) {
		return (x % width) + (y * height);
	}

	private static int getRectVertexOffset(int rectIndex) {
		int coordinateCount = 3; // x y z
		int vertexCount = 4; // 4 vertices per rect
		return rectIndex * coordinateCount * vertexCount;
	}

	private static int getVertexCount(Chunk chunk) {
		return getIndexCount(chunk.getWidth(), chunk.getHeight()) * 4;
	}

	private static int getIndexCount(int width, int height) {
		return width * height * 6;
	}

	private static float[] createVertices(Chunk chunk, int layerIndex) {
		float[] vertices = new float[getVertexCount(chunk)];

		for (int y = 0; y < chunk.getHeight(); y++) {
			for (int x = 0; x < chunk.getWidth(); x++) {
				float heightValue = chunk.getRelative(x, y, layerIndex);

				int rectIndex = getRectIndex(x, y, chunk.getWidth(), chunk.getHeight());
				int vertexOffset = getRectVertexOffset(rectIndex);

				// vertex 0  bottom left
				vertices[vertexOffset] =	 x * RECT_SIZE; // x
				vertices[vertexOffset + 1] = y * RECT_SIZE; // y
				vertices[vertexOffset + 2] = heightValue * MAX_HEIGHT; // z

				// vertex 1 top left
				vertices[vertexOffset + 3] = x * RECT_SIZE; // x
				vertices[vertexOffset + 4] = y * RECT_SIZE + RECT_SIZE; // y
				vertices[vertexOffset + 5] = heightValue * MAX_HEIGHT; // z

				// vertex 2 top right
				vertices[vertexOffset + 6] = x * RECT_SIZE + RECT_SIZE; // x
				vertices[vertexOffset + 7] = y * RECT_SIZE + RECT_SIZE; // y
				vertices[vertexOffset + 8] = heightValue * MAX_HEIGHT; // z

				// vertex 3 bottom right
				vertices[vertexOffset + 9] = x * RECT_SIZE + RECT_SIZE; // x
				vertices[vertexOffset + 10] = y * RECT_SIZE; // y
				vertices[vertexOffset + 11] = heightValue * MAX_HEIGHT; // z

				//Log.l(LayerMesh.class, "x " + x + " y " + y + " index " + vertexOffset);
			}
		}
		return vertices;
	}


	public void render(Matrix4 projectonMatrix) {


		shaderProgram.begin();

		if (shaderProgram.hasUniform("_mv")) {
			shaderProgram.setUniformMatrix("_mv", modelViewMatrix); // model-view matrix
		}
		if (shaderProgram.hasUniform("_mvProj")) {
			shaderProgram.setUniformMatrix("_mvProj", projectonMatrix); // model-view-projection matrix (camera.combined)
		}

		render(shaderProgram, GL20.GL_TRIANGLES);
		shaderProgram.end();




	}
}
