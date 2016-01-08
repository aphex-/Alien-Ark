package com.nukethemoon.libgdxjam.screens.solar;

public class StarsBackground  {

	/*private static final class StarConfig {

		private static Random random = new Random(654);
		public Vector2 position;
		public float speed;

		public StarConfig(Vector2 pPosition, float pSpeed) {
			position = pPosition;
			speed = pSpeed;
		}

		public static StarConfig create() {
			return new StarConfig(
					new Vector2(
							(Config.SCREEN_WIDTH + Math.abs(App.ratioCorrectionXOffset * 2)) * random.nextFloat() + App.ratioCorrectionXOffset,
							Config.SCREEN_HEIGHT * random.nextFloat()),
					random.nextFloat() + 0.5f);
		}
	}

	private static final int STAR_COUNT_01 = 5;
	private static final int STAR_COUNT_01_SMALL = 80;
	private static final int STAR_COUNT_02 = 8;
	private static final int STAR_COUNT_02_SMALL = 150;

	private static final float SPEED_FACTOR = 0.1f;

	private TextureRegion star01;
	private TextureRegion star01small;
	private TextureRegion star02;
	private TextureRegion star02small;

	private List<StarConfig> configsStar01 = new ArrayList<StarConfig>();
	private List<StarConfig> configsStar01Small = new ArrayList<StarConfig>();
	private List<StarConfig> configsStar02 = new ArrayList<StarConfig>();
	private List<StarConfig> configsStar02Small = new ArrayList<StarConfig>();

	public StarsBackground() {
		star01 = 		Resources.getScreenTextures().findRegion("star-01");
		star01small = 	Resources.getScreenTextures().findRegion("star-01-small");
		star02 = 		Resources.getScreenTextures().findRegion("star-02");
		star02small = 	Resources.getScreenTextures().findRegion("star-02-small");

		for (int i = 0; i < STAR_COUNT_01; i++) {
			configsStar01.add(StarConfig.create());
		}

		for (int i = 0; i < STAR_COUNT_01_SMALL; i++) {
			configsStar01Small.add(StarConfig.create());
		}

		for (int i = 0; i < STAR_COUNT_02; i++) {
			configsStar02.add(StarConfig.create());
		}

		for (int i = 0; i < STAR_COUNT_02_SMALL; i++) {
			configsStar02Small.add(StarConfig.create());
		}
	}

	public void draw(TextureRegion pStar, StarConfig pConfig, SpriteBatch pSpriteBatch) {
		float width = pStar.getRegionWidth();
		float height = pStar.getRegionHeight();

		pSpriteBatch.draw(pStar, (int) pConfig.position.x, (int) pConfig.position.y,
				0f, 0f, width, height, 1.0f, 1.0f, 0f);

		pConfig.position.x += pConfig.speed * SPEED_FACTOR;
		pConfig.position.y += pConfig.speed * SPEED_FACTOR;

		if (pConfig.position.x > Config.SCREEN_WIDTH + Math.abs(App.ratioCorrectionXOffset)) {
			pConfig.position.x = -60f + App.ratioCorrectionXOffset;
		}

		if (pConfig.position.y > Config.SCREEN_HEIGHT) {
			pConfig.position.y = -60f;
		}
	}


	@Override
	public void draw(SpriteBatch pSpriteBatch) {


		for (int i = 0; i < configsStar01.size(); i++) {
			draw(star01, configsStar01.get(i), pSpriteBatch);
		}

		for (int i = 0; i < configsStar02.size(); i++) {
			draw(star02, configsStar02.get(i), pSpriteBatch);
		}

		for (int i = 0; i < configsStar01Small.size(); i++) {
			draw(star01small, configsStar01Small.get(i), pSpriteBatch);
		}

		for (int i = 0; i < configsStar02Small.size(); i++) {
			draw(star02small, configsStar02Small.get(i), pSpriteBatch);
		}
	}*/



}

