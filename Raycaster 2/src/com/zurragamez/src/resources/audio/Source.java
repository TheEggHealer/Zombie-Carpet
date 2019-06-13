package com.zurragamez.src.resources.audio;

import org.lwjgl.openal.AL10;

import com.zurragamez.src.Main;

public class Source {
	private static final String TAG = "Source";

	private int sourceId;
	public int timeSinceUsed = 0;
	
	public Source(String tag) {
		sourceId = AL10.alGenSources();
//		System.out.println("Debug Tag: " + call + ": " + sourceId + ", " + AL10.alGetError());
		if(AL10.alGetError() == 0) Main.printDebug(tag + "->" + TAG, "Created source: " + sourceId + " successfully");
		else  Main.printDebugErr(tag + "->" + TAG, "Created source: " + sourceId + "AL Error msg: " + AL10.alGetError());
		stop();
	}
	
	public void delete() {
		stop();
		AL10.alDeleteSources(sourceId);
	}
	
	public void play(int buffer) {
		timeSinceUsed = 0;
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(sourceId);
	}
	
	public void stop() {
		AL10.alSourceStop(sourceId);
	}
	
	public void setLocation(float x, float y) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, 0);
	}
	
	public void setPitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}
	
	public void setGain(float gain) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, gain);
	}
	
	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
}
