package com.zurragamez.src.entities.particles;

import java.awt.Color;

import com.zurragamez.src.resources.Sprite;

public class ParticleSmoke extends Particle {

	public ParticleSmoke(float x, float y) {
		super(x, y, random.nextFloat() * 0.07f + 0.01f, Sprite.createSprite(2, 2, new Color(10, 10, 10, random.nextInt(255))));
		
		velZ = -0.004f + (-random.nextFloat() * 0.03f) * sprite.height;
		velX = (random.nextFloat() - 0.5f) * 0.007f;
		velY = (random.nextFloat() - 0.5f) * 0.007f;
		bounciness = 0.1f;
		life = 30 + random.nextInt(100);
		gravity = 0;
		
		disableFog = false;
	}
	
}
