package com.zurragamez.src.entities.particles;

import com.zurragamez.src.resources.Sprite;

public class ParticleWall extends Particle {

	public ParticleWall(float x, float y, int wallId) {
		super(x, y, random.nextFloat() * 0.07f + 0.01f, Sprite.wall_stone.subSprite(random.nextInt(59), random.nextInt(59), 5, 5, 256, 256));
		
		switch(wallId) {
			case 1:
				setSprite(Sprite.wall_stone.subSprite(random.nextInt(59), random.nextInt(59), 5, 5, 256, 256)); 
				break;
			case 2:
				setSprite(Sprite.wall_wood.subSprite(random.nextInt(59), random.nextInt(59), 5, 5, 256, 256));
				break;
		}
		
		velZ = ((random.nextFloat() - 0.5f) * 0.1f) * sprite.height;
		velX = (random.nextFloat() - 0.5f) * 0.03f;
		velY = (random.nextFloat() - 0.5f) * 0.03f;
		bounciness = 0.3f;
		life = 250 + random.nextInt(100);	
	}

	
	
}
