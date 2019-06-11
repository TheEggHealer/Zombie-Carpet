package com.zurragamez.src.entities;

import com.zurragamez.src.resources.audio.Source;

public class EntitySound {
	public float x, y;
	public Source source;
	public int soundbuffer;
	public boolean remove;
	
	public EntitySound (float x, float y, int soundbuffer) {
		this.x = x;
		this.y = y;
		this.soundbuffer = soundbuffer;
		
		source = new Source();
		
		source.setLocation(x, y);
		source.play(soundbuffer);
		
	}
	
	public void update() {
		System.out.println("Sus");
		if (!source.isPlaying()) {
			remove = true;
			source.stop();
			source.delete();
		}
	}
}
