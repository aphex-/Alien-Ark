package com.nukethemoon.libgdxjam.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.nukethemoon.libgdxjam.App;

public abstract class Artifact {
	private static int nextID;
	public final int ID = ++nextID;

	@Override
	public int hashCode() {
		return ID;
	}

	protected abstract TextureRegion getBackgroundTexture();

	protected Actor getForeground(){
		return null;
	};

	public Actor createActor() {
		final Image bg = new Image(getBackgroundTexture());
		bg.setWidth(75);
		bg.setHeight(75);

		Actor fg = getForeground();
		if(fg == null){
			return bg;
		}

		fg.setWidth(75);
		fg.setHeight(75);

		final Group group = new Group();
		group.addActor(bg);
		group.addActor(fg);

		return group;

	}
}
