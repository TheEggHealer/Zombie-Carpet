package com.zurragamez.src.entities.particles;

import java.awt.Color;

import com.zurragamez.src.resources.Sprite;

public class ParticleBulletSmoke extends Particle {

	public ParticleBulletSmoke(float x, float y) {
		super(x, y, random.nextFloat() * 0.07f + 0.01f, Sprite.createSprite(2, 2, new Color(10, 10, 10, random.nextInt(255))));
		
		velZ = (random.nextFloat() - 0.5f) * 0.022f;
		velX = (random.nextFloat() - 0.5f) * 0.022f;
		velY = (random.nextFloat() - 0.5f) * 0.022f;
		bounciness = 0.4f;
		life = 70 + random.nextInt(100);
		gravity = 0;
		
		disableFog = false;
	}
	
}
