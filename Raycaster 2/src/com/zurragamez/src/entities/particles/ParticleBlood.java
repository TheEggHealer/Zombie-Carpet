package com.zurragamez.src.entities.particles;

import com.zurragamez.src.resources.Sprite;

public class ParticleBlood extends Particle {

	public ParticleBlood(float x, float y) {
		super(x, y, random.nextFloat() * 0.07f + 0.01f, Sprite.particle_blood.subSprite(random.nextInt(28), random.nextInt(28), 4, 4, 256, 256));
		
		velZ = ((random.nextFloat() - 0.5f) * 0.1f) * sprite.height;
		velX = (random.nextFloat() - 0.5f) * 0.03f;
		velY = (random.nextFloat() - 0.5f) * 0.03f;
		bounciness = 0.2f;
		life = 250 + random.nextInt(100);
		
		disableFog = false;
	}

}
