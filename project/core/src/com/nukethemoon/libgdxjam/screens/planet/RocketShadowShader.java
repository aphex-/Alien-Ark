package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public class RocketShadowShader extends DefaultShader {

	private int jumpValueIndex;

	public RocketShadowShader(Renderable renderable, Config config) {
		super(renderable, config);
	}

	@Override
	public void init() {
		super.init();
		jumpValueIndex = program.getUniformLocation("u_jumpValue");
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		program.setUniformf(jumpValueIndex, 1f);
		super.begin(camera, context);
	}

}
