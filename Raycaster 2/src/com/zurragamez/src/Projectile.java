package com.zurragamez.src;
import java.util.Random;

import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.Zombie;
import com.zurragamez.src.entities.particles.ParticleBlood;
import com.zurragamez.src.entities.particles.ParticleWall;
import com.zurragamez.src.resources.Sprite;

public class Projectile extends EntitySprite {
	
	private float dir;
	private float speed;
	private float heightChange;
	
	private Random random = new Random();
	
	public Projectile(float x, float y, float direction, float scale, Sprite sprite) {
		super(x, y, 0.05f, false, sprite);
		this.dir = direction + (random.nextFloat()-0.5f) * 0.04f;
		this.speed = 0.2f;
		
		hoverHeight = 10;
		heightChange = random.nextFloat() * 2f - 1;
		
		disableFog = false;
	}
	
	public void update() {
		x += Math.cos(dir) * speed;
		y += Math.sin(dir) * speed;
		
		hoverHeight -= heightChange;
		
		if(x < 0 || x > Main.mapHeight-1 || y < 0 || y > Main.mapWidth-1) remove = true;

		if(Main.map[(int)(x)][(int)(y)] != 0) {
			for(int i = 0; i < 1; i++) {
				main.addEntity(new ParticleWall((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed), Main.map[(int)(x)][(int)(y)]));
			}
			remove = true;
		}
		
		for(int i = 0; i < main.getEntities().size(); i++) {
			EntitySprite e = main.getEntities().get(i);
			if(e instanceof Zombie) {
				Zombie z = (Zombie)e;
				if(distance(z) <= z.getHitRadius()) {
					z.hurt(10);
					for(int a = 0; a < 10; a++) {
						main.addEntity(new ParticleBlood((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed)));
					}
					remove = true;
				}
			}
		}
	}
	
}
