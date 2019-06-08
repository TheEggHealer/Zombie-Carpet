package com.zurragamez.src.resources.audio;

import org.lwjgl.openal.AL10;

public class Source {

	private int sourceId;
	public int timeSinceUsed = 0;
	
	public Source() {
		sourceId = AL10.alGenSources();
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
