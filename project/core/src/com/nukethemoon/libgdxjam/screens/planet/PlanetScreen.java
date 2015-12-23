package com.nukethemoon.libgdxjam.screens.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Log;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.input.InputController;
import com.nukethemoon.libgdxjam.screens.ark.ArkScreen;

import java.util.ArrayList;
import java.util.List;

public class PlanetScreen implements Screen {

    private static final int INITIAL_ARK_POSITION_Y = 10;
    private static final int INITIAL_ARK_POSITION_X = 10;
    private List<Vector2> planetPositions = new ArrayList<Vector2>();
    private InputController inputController = new InputController();

    private float arkX = INITIAL_ARK_POSITION_X;
    private float arkY = INITIAL_ARK_POSITION_Y;

    private SpriteBatch batch;

    private Sprite arkSprite;
    private int screenHeight;
    private int screenWidth;

    float arkHeight;
    float arkWidth;

    @Override
    public void show() {
        batch = new SpriteBatch();
        arkSprite = new Sprite(App.TEXTURES.findRegion("ntm_logo"));
        arkSprite.setX(INITIAL_ARK_POSITION_X);
        arkSprite.setY(INITIAL_ARK_POSITION_Y);
        arkWidth = arkSprite.getWidth();
        arkHeight = arkSprite.getHeight();

        planetPositions = calculatePlanetPositions();
    }

    private List<Vector2> calculatePlanetPositions() {
        return null;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderArc();
        renderPlanets();
        handleArkMovementInput();
        handleAppNavigation();
    }

    private void renderArc() {
        batch.begin();
        arkSprite.draw(batch);
        batch.end();
    }

    private void renderPlanets() {

    }

    /*
         x     y
    deg sin  cos
    ---------------
    0    0    1
    90   1    0
    180  0   -1
    270 -1    0
    */
    private void handleArkMovementInput() {
        if (inputController.keyDown(Input.Keys.UP)) {
            moveArcPositionByDirection(0);
        } else if (inputController.keyDown(Input.Keys.RIGHT)) {
            moveArcPositionByDirection(90);
        } else if (inputController.keyDown(Input.Keys.DOWN)) {
            moveArcPositionByDirection(180);
        } else if (inputController.keyDown(Input.Keys.LEFT)) {
            moveArcPositionByDirection(270);
        }
    }

    private void moveArcPositionByDirection(int degree) {
        double radians = Math.toRadians(degree);
        float translateX = (float) Math.sin(radians);
        float translateY = (float) Math.cos(radians);
        arkSprite.translate(translateX, translateY);
        arkX = arkSprite.getX();
        arkY = arkSprite.getY();
        Log.l(PlanetScreen.class, "x: "+arkX +"- y: " +arkY);

        checkIfArkIsOffScreenAndCorrect();
    }

    private void checkIfArkIsOffScreenAndCorrect() {
        if (isArkOffscreenLeft()) {
            arkSprite.translateX(0 - arkX + screenWidth - 20);
        }
        if (isArkOffscreenRight()) {
            arkSprite.translateX( -1*(arkWidth + screenWidth - 20));
        }
        if (isArkOffscreenTop()) {
            arkSprite.translateY( -1 * (arkHeight + screenHeight -20));
        }
        if (isArkOffscreenBottom()) {
            arkSprite.translateY( 0 - arkY + screenHeight - 20);
        }

    }

    private boolean isArkOffscreenBottom() {
        return arkY < 0 - arkHeight;
    }

    private boolean isArkOffscreenTop() {
        return screenHeight < arkY;
    }

    private boolean isArkOffscreenRight() {
        return screenWidth < arkX;
    }

    private boolean isArkOffscreenLeft() {
        return arkX < 0 - arkWidth;
    }

    private void handleAppNavigation() {
        if (isArkCollidingWithPlanet()) {
            openPlanetScreen();
        }

        if (isArcSelected()) {
            openArkScreen();
        }
    }


    @Override
    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        SpaceShipProperties.testInit();
        SpaceShipProperties.computeSpeedPerUnit();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private boolean isArkCollidingWithPlanet() {
        return false;
    }

    private boolean isArcSelected() {
        return false;
    }

    private void openPlanetScreen() {
        App.openScreen(PlanetScreen.class);
    }

    private void openArkScreen() {
        App.openScreen(ArkScreen.class);
    }


}
