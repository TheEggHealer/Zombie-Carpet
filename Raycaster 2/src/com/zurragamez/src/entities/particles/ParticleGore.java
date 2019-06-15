package com.zurragamez.src.entities.particles;

import com.zurragamez.src.resources.Sprite;

public class ParticleGore extends Particle {

	public ParticleGore(float x, float y) {
		super(x, y, 0.1f, Sprite.particle_gore);
		
		velZ = ((random.nextFloat() - 0.5f) * 0.1f) * sprite.height;
		velX = (random.nextFloat() - 0.5f) * 0.03f;
		velY = (random.nextFloat() - 0.5f) * 0.03f;
		bounciness = 0.7f;
		life = 400 + random.nextInt(100);
		
		disableFog = false;
	}

}
