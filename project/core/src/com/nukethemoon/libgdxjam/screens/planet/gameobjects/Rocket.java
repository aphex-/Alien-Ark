package com.nukethemoon.libgdxjam.screens.planet.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.nukethemoon.libgdxjam.App;
import com.nukethemoon.libgdxjam.Balancing;
import com.nukethemoon.libgdxjam.game.SpaceShipProperties;
import com.nukethemoon.libgdxjam.game.attributes.FuelCapacity;
import com.nukethemoon.libgdxjam.game.attributes.ShieldCapacity;
import com.nukethemoon.libgdxjam.screens.planet.physics.CollisionTypes;

import java.awt.*;

public class Rocket extends GameObject implements Disposable {


	private static final float THIRD_PERSON_OFFSET_Z = 2;
	public static final int MIN_DAMAGE_DELAY_MILLIS = 200;
	private static final Vector3 START_POSITION = new Vector3(20, 0, 120);
	private static final Vector3 LAUNCH_IMPULSE = new Vector3(0, 0, 55);
	private static final int LAUNCH_INDESTRUCTIBLE_TICKS = 30;
	private static final float FUEL_CONSUMPTION = 0.1f;
	private static final int SCAN_DELAY = 50;
	private static final int THRUST_START_FUEL_COST = 15;

	private static final int PLANET_Z_LIMIT = 200;

	private final Sound thrustSound;

	private long ticksSinceLastLaunch = 0;
	private long lastDamageTime = -1;

	private int currentScanDelay = SCAN_DELAY;

	private final ModelInstance rocketModelInstance;
	private final ModelInstance shieldModelInstance;
	private final ModelInstance tractorBeamModelInstance;
	private final Model model;

	float drill = 0;

	float xRotation = 0;
	float zRotation = 0;

	private Point lastTilePosition = null;
	private float thirdPersonOffsetY = 3;

	private Vector3 lastCamPosition = new Vector3();
	private Vector3 tmpCamPosition = new Vector3();
	private Vector3 tmpCamOffset = new Vector3();

	private Matrix4 rotationMatrix = new Matrix4();

	private Vector3 tmpDirection = new Vector3();
	private Vector3 tmpPosition = new Vector3();
	private Vector3 tmpMovement = new Vector3();

	private Vector3 tmpVec0 = new Vector3();

	private boolean thrusting = true;
	private boolean moving = true;
	private boolean exploded = false;
	private boolean toHigh = false;

	private boolean tractorBeamVisible = false;

	private RocketListener listener;

	private float tickFuelCount = 1;
	private PerspectiveCamera camera;

	public Rocket() {
		// init graphic
		ModelLoader loader = new ObjLoader();
		model = loader.loadModel(Gdx.files.internal("models/rocket.obj"), new ObjLoader.ObjLoaderParameters(true));
		rocketModelInstance = new ModelInstance(model);


		ModelBuilder modelBuilder = new ModelBuilder();
		final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

		// damage shield
		Material shieldMaterial = new Material();
		shieldMaterial.set(ColorAttribute.createDiffuse(0, 0.3f, 1, 0.5f));
		shieldMaterial.set(ColorAttribute.createSpecular(1, 1, 1, 1f));
		shieldMaterial.set(new BlendingAttribute(1f));
		Model shieldModel = modelBuilder.createSphere(4, 4, 4, 32, 32, shieldMaterial, attributes);
		shieldModelInstance = new ModelInstance(shieldModel);

		Material tractorBeamMaterial = new Material();
		tractorBeamMaterial.set(ColorAttribute.createDiffuse(0, 0, 1, 1));
		tractorBeamMaterial.set(ColorAttribute.createSpecular(1, 1, 1, 1f));
		tractorBeamMaterial.set(new BlendingAttribute(0.1f));
		Model tractorBeamModel = modelBuilder.createSphere(5, 5, 5, 32, 32, tractorBeamMaterial, attributes);
		tractorBeamModelInstance = new ModelInstance(tractorBeamModel);


		// init physic
		//BoundingBox boundingBox = new BoundingBox();
		//model.calculateBoundingBox(boundingBox);
		//btCollisionShape shape = new btBoxShape(boundingBox.getDimensions(new Vector3()).scl(0.5f));
		btCollisionShape shape = new btBoxShape(new Vector3(1, 1, 1));

		rocketModelInstance.transform.setToRotation(0, 0, 1, 0);
		rocketModelInstance.transform.trn(START_POSITION);
		float mass = 1;
		addRigidBody(shape, mass, SpaceShipProperties.properties.getLandslide(), CollisionTypes.ROCKET.mask,
				new RocketMotionState(rocketModelInstance.transform));
		rigidBodyList.get(0).setActivationState(4); // disable deactivation
		rigidBodyList.get(0).setLinearVelocity(tmpMovement.set(getDirection()).nor().scl(
				SpaceShipProperties.properties.getSpeed()));

		SpaceShipProperties.properties.setCurrentInternalFuel((int) FuelCapacity.INTERNAL_MAX);
		SpaceShipProperties.properties.setCurrentInternalShield((int) ShieldCapacity.INTERNAL_MAX);


		thrustSound = App.audioController.getSound("thrust.wav");
		if (App.config.playAudio) {
			thrustSound.loop();
			thrustSound.play();
		}
	}

	public void setThirdPersonCam(PerspectiveCamera camera) {
		this.camera = camera;
	}

	public void setListener(RocketListener listener) {
		this.listener = listener;
	}


	public void rotateLeft() {
		if (thrusting) {
			zRotation = (zRotation + SpaceShipProperties.properties.getManeuverability()) % 360;
		}
	}

	public void rotateRight() {
		if (thrusting) {
			zRotation = (zRotation - SpaceShipProperties.properties.getManeuverability()) % 360;
		}
	}

	public void rotateDown() {
		int invert = 1;
		if (App.config.invertUpDown) {
			invert = -1;
		}

		if (thrusting) {
			if ((xRotation - SpaceShipProperties.properties.getManeuverability()) > -89) {
				xRotation = (xRotation - SpaceShipProperties.properties.getManeuverability() * invert) % 360;
			}
		}
	}

	public void rotateUp() {
		int invert = 1;
		if (App.config.invertUpDown) {
			invert = -1;
		}
		if (thrusting) {
			if((xRotation + SpaceShipProperties.properties.getManeuverability()) < 89) {
				xRotation = (xRotation + SpaceShipProperties.properties.getManeuverability() * invert) % 360;
			}
		}
	}

	public void update() {
		if (exploded) {
			return;
		}

		if (thrusting) {
			thrust();
			drill += 2;
		}

		float zPosition = getPosition().z;
		if (zPosition > PLANET_Z_LIMIT) {
			if (zPosition > PLANET_Z_LIMIT * 1.5f) {
				onExplode();
			}
			listener.onRocketFliesToHigh();
			toHigh = true;
		} else {
			if (toHigh) {
				listener.onRocketBackToNormalHeight();
			}
			toHigh = false;
		}

		rotationMatrix.setToRotation(Vector3.Z, zRotation);
		rotationMatrix.rotate(Vector3.X, xRotation);
		rotationMatrix.rotate(Vector3.Y, drill);
		rocketModelInstance.transform.mul(rotationMatrix);

		if (moving) {
			shieldModelInstance.transform.set(rocketModelInstance.transform);
			tmpVec0.set(getDirection()).scl(1.5f);
			shieldModelInstance.transform.trn(tmpVec0);
			tractorBeamModelInstance.transform.set(rocketModelInstance.transform);
			tractorBeamModelInstance.transform.trn(tmpVec0);
		}

		if (rigidBodyList.get(0).getLinearVelocity().len() < 0.1 && !thrusting) {
			if (moving) {
				onLanded();
			}
			moving = false;
		}

		if (lastTilePosition == null) {
			lastTilePosition = new Point();
			updateTilePosition();
			listener.onRocketChangedTilePosition();
		} else {
			int lastX = lastTilePosition.x;
			int lastY = lastTilePosition.y;
			updateTilePosition();
			if (lastX != lastTilePosition.x || lastY != lastTilePosition.y) {
				listener.onRocketChangedTilePosition();
			}
		}
	}

	public Point getTilePosition() {
		updateTilePosition();
		return lastTilePosition;
	}

	private void updateTilePosition() {
		Vector3 position = getPosition();
		lastTilePosition.setLocation(PlanetPart.getTileX(position.x), PlanetPart.getTileY(position.y));
	}

	private void thrust() {
		if (isOutOfFuel()) {
			return;
		}
		tmpMovement.set(getDirection()).nor().scl(SpaceShipProperties.properties.getSpeed());
		rigidBodyList.get(0).applyCentralForce(tmpMovement);
	}

	private void onLaunch() {
		if (isOutOfFuel()) {
			return;
		}
		if (currentScanDelay <= 0) {
			listener.onRocketScanEnd();
		}
		currentScanDelay = SCAN_DELAY;
		ticksSinceLastLaunch = 0;
		rigidBodyList.get(0).applyCentralImpulse(LAUNCH_IMPULSE);
		moving = true;

		if (listener != null) {
			listener.onRocketLaunched();
		}
	}

	private void onLanded() {
		if (listener != null) {
			listener.onRocketLanded();
		}
	}

	private void onThrustEnabled() {
		SpaceShipProperties.properties.addCurrentInternalFuel(-THRUST_START_FUEL_COST);
		if (isOutOfFuel()) {
			return;
		}

		if (App.config.playAudio) {
			thrustSound.loop();
			thrustSound.play();
		}


		if (!moving) {
			onLaunch();
		}

		if (listener != null) {
			listener.onRocketEnabledThrust();
		}
	}

	private void onThrustDisabled() {
		thrustSound.stop();
		if (listener != null) {
			listener.onRocketDisabledThrust();
		}
	}

	public void onExplode() {
		exploded = true;
		if (listener != null) {
			listener.onRocketExploded();
		}
	}

	public void toggleThrust() {
		thrusting = !thrusting;
		if (!thrusting) {
			onThrustDisabled();
		} else {
			onThrustEnabled();
		}
	}

	public Vector3 getDirection() {
		tmpDirection.set(0, 1, 0);
		tmpDirection.rotate(Vector3.X, xRotation);
		tmpDirection.rotate(Vector3.Z, zRotation);
		return tmpDirection.nor();
	}


	public Vector3 getPosition() {
		return rigidBodyList.get(0).getWorldTransform().getTranslation(tmpPosition);
	}

	public void reduceThirdPersonOffsetY() {
		thirdPersonOffsetY--;
	}

	public void increaseThirdPersonOffsetY() {
		thirdPersonOffsetY++;
	}



	public void drawModel(ModelBatch modelBatch, Environment environment, ParticleEffect thrustEffect,
						  ParticleEffect effectExplosion) {

		if (!exploded) {
			modelBatch.render(rocketModelInstance, environment);

			long damageDelay = System.currentTimeMillis() - lastDamageTime;
			if (damageDelay < MIN_DAMAGE_DELAY_MILLIS) {
				float opacity = (float) damageDelay / (float) (MIN_DAMAGE_DELAY_MILLIS);
				((BlendingAttribute) shieldModelInstance.materials.get(0).get(BlendingAttribute.Type)).opacity = opacity;
				modelBatch.render(shieldModelInstance, environment);
			}

			if (tractorBeamVisible) {
				modelBatch.render(tractorBeamModelInstance, environment);
			}
		}
		thrustEffect.setTransform(rocketModelInstance.transform);
		effectExplosion.setTransform(rocketModelInstance.transform);
	}

	@Override
	public void dispose() {
		thrustSound.stop();
	}

	public void handlePhysicTick() {
		if (isOutOfFuel()) {
			if (thrusting) {
				onThrustDisabled();
			}
			return;
		}
		if (moving) {
			ticksSinceLastLaunch++;
		}

		if (!moving && !thrusting) {
			if (currentScanDelay > 0) {
				currentScanDelay--;
				if (currentScanDelay == 0) {
					listener.onRocketScanStart();
				}
			}
		}

		if (thrusting) {
			tickFuelCount = tickFuelCount - FUEL_CONSUMPTION;
			if (tickFuelCount <= 0) {
				tickFuelCount = 1;
				SpaceShipProperties.properties.addCurrentInternalFuel(-1);
				if (listener != null) {
					listener.onRocketFuelConsumed();
				}
			}

			// cap the speed
			Vector3 linearVelocity = rigidBodyList.get(0).getLinearVelocity();
			float tickSpeed = linearVelocity.len();
			if (tickSpeed > SpaceShipProperties.properties.getSpeed()) {
				linearVelocity.scl(SpaceShipProperties.properties.getSpeed() / tickSpeed);
				rigidBodyList.get(0).setLinearVelocity(tmpMovement);
			}
		}
	}

	public void dealDamage(int damage) {
		if (System.currentTimeMillis() - lastDamageTime < MIN_DAMAGE_DELAY_MILLIS) {
			return;
		}
		lastDamageTime = System.currentTimeMillis();

		if (ticksSinceLastLaunch > LAUNCH_INDESTRUCTIBLE_TICKS) {
			SpaceShipProperties.properties.addCurrentShield(-damage);
			if (listener != null) {
				listener.onRocketDamage();
			}
		}
		if (SpaceShipProperties.properties.getCurrentInternalShield() == 0) {
			onExplode();
		}
	}

	public void handleCollision(CollisionTypes type) {
		if (type == CollisionTypes.GROUND && thrusting) {
			dealDamage(10);
		}

		if (type == CollisionTypes.WATER) {
			dealDamage(20);
		}

		if (type == CollisionTypes.FUEL) {
			SpaceShipProperties.properties.addCurrentInternalFuel(Balancing.FUEL_BONUS);
			listener.onRocketFuelBonus();
		}

		if (type == CollisionTypes.SHIELD) {
			SpaceShipProperties.properties.addCurrentShield(Balancing.SHIELD_BONUS);
			listener.onRocketShieldBonus();
		}

		if (type == CollisionTypes.PORTAL_EXIT) {
			listener.onRocketEntersPortal();
		}
	}


	public boolean isOutOfFuel() {
		return SpaceShipProperties.properties.getCurrentInternalFuel() <= 0;
	}


	public float getZRotation() {
		return zRotation;
	}

	public float getXRotation() {
		return xRotation;
	}

	public void setXRotation(float xRotation) {
		this.xRotation = xRotation;
	}

	public void setZRotation(float zRotation) {
		this.zRotation = zRotation;
	}

	public Vector3 getLastCamPosition() {
		return lastCamPosition;
	}

	public void setTractorBeamVisibility(boolean visible) {
		tractorBeamVisible = visible;
	}

	public ModelInstance getTractorBeamModelInstance() {
		return tractorBeamModelInstance;
	}

	public ModelInstance getRocketModelInstance() {
		return rocketModelInstance;
	}

	/**
	 * This class is used by Bullet to update
	 * and read the transform of the rocket.
	 */
	class RocketMotionState extends btMotionState {
		private Matrix4 transform;
		private Vector3 tmpVector = new Vector3();
		private Vector3 tmpVector2 = new Vector3();

		public RocketMotionState(Matrix4 transform) {
			this.transform = transform;
		}

		@Override
		public void getWorldTransform (Matrix4 worldTrans) {
			worldTrans.set(transform);
		}
		@Override
		public void setWorldTransform (Matrix4 worldTrans) {
			// ignore rotation
			transform.setToTranslation(worldTrans.getTranslation(tmpVector));
			applyThirdPerson(worldTrans);
		}

		public void applyThirdPerson(Matrix4 worldTrans) {
			if (camera == null) {
				return;
			}
			Vector3 position = worldTrans.getTranslation(tmpVector2);
			if (position.z < 0) {
				return;
			}
			tmpCamPosition.set(position);
			tmpCamOffset.set(getDirection());
			tmpCamOffset.scl(-thirdPersonOffsetY);
			tmpCamPosition.add(tmpCamOffset);
			tmpCamPosition.z += THIRD_PERSON_OFFSET_Z;
			// creating a delay for the camera
			tmpCamPosition.x = lastCamPosition.x + (tmpCamPosition.x - lastCamPosition.x) / 10f;
			tmpCamPosition.y = lastCamPosition.y + (tmpCamPosition.y - lastCamPosition.y) / 10f;
			tmpCamPosition.z = lastCamPosition.z + (tmpCamPosition.z - lastCamPosition.z) / 10f;
			tmpCamPosition.z = Math.max(tmpCamPosition.z, 1);
			camera.position.set(tmpCamPosition);
			camera.lookAt(position);
			camera.up.set(Vector3.Z);
			lastCamPosition.set(camera.position);
		}

	}



}
