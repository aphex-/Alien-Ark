package com.nukethemoon.libgdxjam;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;


public class AudioController {

	private static Music music = null;

	private static final String SOUND_PATH = "audio/sounds/";
	private static final String MUSIC_PATH = "audio/music/";


	private float soundVolume = 1f;
	private float musicVolume = 1f;

	private boolean musicEnabled = true;
	private boolean soundEnabled = true;

	private static final String[] STANDARD_SOUNDS = new String[] {
			"hit_deep.mp3",
			"hit_high.mp3",
			"explosion.mp3",
			"bonus.mp3",
			"energy_shield.mp3"
	};


	private Map<String, Sound> soundMap = new HashMap<String, Sound>();


	public AudioController() {
		loadSounds(STANDARD_SOUNDS);
	}

	private void loadSounds(final String[] pSoundNames) {
		for (String soundName : pSoundNames) {
			FileHandle soundFile = Gdx.files.internal(SOUND_PATH + soundName);
			if (soundFile.exists()) {
				Sound tmpSound = Gdx.audio.newSound(soundFile);
				soundMap.put(soundName, tmpSound);
			} else {
				Gdx.app.log(AudioController.class.getSimpleName(), "Can not find sound: " + SOUND_PATH + soundName);
			}
		}
	}


	public void playSound(String pSoundName) {
		playSound(pSoundName, 1f);
	}

	public void playSound(String pSoundName, float pVolume) {
		if (!soundEnabled) {
			return;
		}

		Sound sound = soundMap.get(pSoundName);
		if (sound != null) {

			sound.play(soundVolume * pVolume);
		} else {
			Gdx.app.log(AudioController.class.getSimpleName(), "Can not play sound: " + pSoundName + ". It was not found.");
		}
	}

	public void playMusic(String pMusicName) {
		if (!musicEnabled) {
			return;
		}
		if (isMusicPlaying()) {
			stopMusic();
		}

		FileHandle file = Gdx.files.internal(MUSIC_PATH + pMusicName);
		if (file.exists()) {
			music = Gdx.audio.newMusic(file);
			music.setVolume(musicVolume);
		} else {
			Gdx.app.log(AudioController.class.getSimpleName(), "Can not load music: " + MUSIC_PATH + pMusicName + ". It was not found.");
		}
		if (music != null) {
			music.play();
		}
	}

	public void stopMusic() {
		if (music != null) {
			music.stop();
			music.dispose();
		}
	}

	public boolean isMusicPlaying() {
		if (music != null) {
			return music.isPlaying();
		}
		return false;
	}

	public void dispose() {
		for (Sound tmpSound : soundMap.values()) {
			tmpSound.dispose();
		}
	}

	public void setMusicEnabled(boolean pMusicEnabled) {
		if (this.musicEnabled && !pMusicEnabled) {
			this.musicEnabled = pMusicEnabled;
			if (music != null) {
				music.stop();
			}
		}
	}

	public void setSoundEnabled(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
	}

	public void fadeOut() {
		stopMusic();
	}


}
