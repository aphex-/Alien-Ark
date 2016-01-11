package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Styles;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Collectible;
import com.nukethemoon.libgdxjam.screens.planet.gameobjects.Rocket;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

public class MiniMap {

	private static final int MINI_MAP_SIZE = 250;
	private static final float MINI_ZOOM = 1.5f;

	private OrthographicCamera miniMapCamera;
	private SpriteBatch miniMapBatch;
	private ModelBatch miniMapModelBatch;

	private Matrix4 tmpMatrix = new Matrix4();

	private TextureRegion rocketArrow;
	private TextureRegion shieldIcon;
	private TextureRegion fuelIcon;
	private TextureRegion artifactIcon;
	private TextureRegion minimapBorder;
	private TextureRegion planetPortal;

	private Rocket rocket;
	private ControllerPlanet planetController;

	private Vector2 tmpVec5 = new Vector2();
	private Vector2 tmpV3 = new Vector2();
	private Vector2 tmpV2 = new Vector2();
	private Vector2 tmpVec6 = new Vector2();

	public MiniMap(Rocket rocket, ControllerPlanet planetController) {
		this.rocket = rocket;
		this.planetController = planetController;

		miniMapModelBatch = new ModelBatch();
		miniMapBatch = new SpriteBatch();

		miniMapCamera = new OrthographicCamera(MINI_MAP_SIZE * MINI_ZOOM, MINI_MAP_SIZE * MINI_ZOOM);
		miniMapCamera.near = 1f;
		miniMapCamera.far = 300f;

		rocketArrow = 	App.TEXTURES.findRegion("rocket_arrow");
		shieldIcon = 	App.TEXTURES.findRegion("minimap_shield");
		fuelIcon =		App.TEXTURES.findRegion("minimap_fuel");
		artifactIcon = 	App.TEXTURES.findRegion("minimap_artifact");
		minimapBorder = App.TEXTURES.findRegion("minimapBorder");
		planetPortal = 	App.TEXTURES.findRegion("planetPortal");
	}

	public void drawMiniMap() {
		Gdx.gl.glViewport(
				Gdx.graphics.getWidth() - MINI_MAP_SIZE - 10,
				Gdx.graphics.getHeight() - MINI_MAP_SIZE - 10,
				MINI_MAP_SIZE,
				MINI_MAP_SIZE);

		Vector3 rocketPosition = rocket.getPosition();
		float groundZRotation = rocket.getGroundZRotation();

		miniMapCamera.up.set(rocket.getDirection());
		miniMapCamera.position.set(rocket.getPosition());
		miniMapCamera.position.z = 100;
		miniMapCamera.update();

		// === mini map landscape ===
		miniMapModelBatch.begin(miniMapCamera);
		planetController.render(miniMapModelBatch, null, true);
		miniMapModelBatch.end();

		// === mini map items ===
		miniMapBatch.setProjectionMatrix(miniMapCamera.combined);
		miniMapBatch.begin();
		// rocket arrow
		miniMapBatch.draw(rocketArrow,
				rocket.getPosition().x - rocketArrow.getRegionWidth() / 2f,
				rocket.getPosition().y - rocketArrow.getRegionHeight() / 2f,
				rocketArrow.getRegionWidth() / 2f,
				rocketArrow.getRegionHeight() / 2f,
				rocketArrow.getRegionWidth(),
				rocketArrow.getRegionHeight(),
				MINI_ZOOM, MINI_ZOOM, rocket.getGroundZRotation());
		// collectibles
		drawMiniMapCollectibles(miniMapBatch, groundZRotation);
		miniMapBatch.end();

		// === mini map border ===
		tmpMatrix.setToOrtho2D(0, 0, MINI_MAP_SIZE * MINI_ZOOM, MINI_MAP_SIZE * MINI_ZOOM);
		miniMapBatch.setProjectionMatrix(tmpMatrix);
		miniMapBatch.begin();
		miniMapBatch.draw(minimapBorder, 0, 0);
		miniMapBatch.end();

		// === mini map extensions ===
		miniMapBatch.setProjectionMatrix(miniMapCamera.combined);
		miniMapBatch.begin();
		drawMiniMapExtensions(groundZRotation, rocketPosition);
		miniMapBatch.end();

		// === daw distance text ====
		tmpMatrix.setToOrtho2D(0, 0, MINI_MAP_SIZE * MINI_ZOOM, MINI_MAP_SIZE * MINI_ZOOM);
		miniMapBatch.setProjectionMatrix(tmpMatrix);
		miniMapBatch.begin();
		drawMiniMapDistanceText(planetController.getNearestArtifactPosition(), rocketPosition, groundZRotation);
		drawMiniMapDistanceText(tmpVec5.set(0, 0), rocketPosition, groundZRotation);
		if (App.config.debugMode) {
			int tileX = (int) (rocketPosition.x / ControllerPlanet.TILE_GRAPHIC_SIZE);
			int tileY = (int) (rocketPosition.y / ControllerPlanet.TILE_GRAPHIC_SIZE);

			Styles.FONT_LIBERATION_SMALL_BORDER.draw(miniMapBatch, "x " + tileX  + " y " + tileY, 0, 20);
		}
		miniMapBatch.end();
	}

	private Vector2 getPositionInsideMiniMap(Vector3 centerPosition, Vector2 itemPosition, float radiusOffset) {
		float radius = 170 + radiusOffset;
		tmpV2.set(itemPosition.x - centerPosition.x, itemPosition.y - centerPosition.y);
		if (tmpV2.len() > radius) {
			tmpV2.nor().scl(radius);
		}
		tmpV2.add(centerPosition.x, centerPosition.y);
		return tmpV2;
	}

	public void drawMiniMapCollectibles(SpriteBatch miniMapBatch, float upRotation) {
		for (Collectible c : planetController.getCurrentVisibleCollectibles()) {
			TextureRegion textureRegion = null;
			if (c.getType() == CollisionTypes.FUEL) {
				textureRegion = fuelIcon;
			}
			if (c.getType() == CollisionTypes.SHIELD) {
				textureRegion = shieldIcon;
			}
			if (textureRegion != null) {
				Vector2 position = tmpV3.set(c.getPosition().x, c.getPosition().y);
				drawMiniMapItem(miniMapBatch, textureRegion, position, upRotation);
			}
		}
	}

	public void drawMiniMapExtensions(float upRotation, Vector3 rocketPosition) {
		// nearest artifact
		Vector2 artifactPosition = planetController.getNearestArtifactPosition();
		if (artifactPosition != null) {
			artifactPosition = getPositionInsideMiniMap(rocketPosition, artifactPosition, 0);
			drawMiniMapItem(miniMapBatch, artifactIcon, artifactPosition, upRotation);
		}

		// planet portal
		tmpVec5.set(0, 0);
		tmpVec5 = getPositionInsideMiniMap(rocket.getPosition(), tmpVec5, 0);
		drawMiniMapItem(miniMapBatch, planetPortal, tmpVec5, upRotation);
	}

	public void drawMiniMapDistanceText(Vector2 position, Vector3 rocketPosition, float upRotation) {
		BitmapFont font = Styles.FONT_LIBERATION_SMALL_BORDER;
		if (position != null) {
			float distance = tmpVec6.set(rocketPosition.x, rocketPosition.y).sub(position).len();
			if (distance > 170) {
				float x = (MINI_MAP_SIZE * MINI_ZOOM) / 2f;
				float y = x;
				float km = (float) Math.floor(distance / 100f) / 10f;
				layout.setText(font, km + "km");
				x -= layout.width / 2;
				//y -= layout.height / 2;
				tmpVec5.set(position).sub(rocketPosition.x, rocketPosition.y).nor().scl(150).rotate(-upRotation);
				x += tmpVec5.x;
				y += tmpVec5.y;
				font.draw(miniMapBatch, layout, x, y);
			}
		}
	}

	GlyphLayout layout = new GlyphLayout();

	private void drawMiniMapItem(SpriteBatch batch, TextureRegion textureRegion, Vector2 position, float upRotation) {
		batch.draw(textureRegion,
				position.x - textureRegion.getRegionWidth() / 2f,
				position.y - textureRegion.getRegionHeight() / 2f,
				textureRegion.getRegionWidth() / 2f,
				textureRegion.getRegionHeight() / 2f,
				textureRegion.getRegionWidth(),
				textureRegion.getRegionHeight(),
				MINI_ZOOM, MINI_ZOOM, upRotation);

	}
}
