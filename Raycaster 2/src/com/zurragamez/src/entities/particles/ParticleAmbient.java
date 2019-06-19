package com.zurragamez.src.entities.particles;

import java.awt.Color;

import com.zurragamez.src.resources.Sprite;

public class ParticleAmbient extends Particle {

	public ParticleAmbient(float x, float y) {
		super(x, y, random.nextFloat() * 0.07f + 0.01f, Sprite.createSprite(2, 2, new Color(30, 30, 30, random.nextInt(200) + 50)));
		
		velZ = (random.nextFloat() - 0.5f) * 0.007f * spriteSize;
		velX = (random.nextFloat() - 0.5f) * 0.007f;
		velY = (random.nextFloat() - 0.5f) * 0.007f;
		
		
		hoverHeight = -(1f - getScale()) * getSpriteSize() + (random.nextFloat() * (1f - getScale()) * getSpriteSize()) * 2;
		
		bounciness = 1f;
		life = 30 + random.nextInt(100);
		gravity = 0;
		
		disableFog = false;
	}

}
