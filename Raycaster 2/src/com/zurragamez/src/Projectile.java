package com.zurragamez.src;
import java.util.Random;

import com.zurragamez.src.entities.EntitySound;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.Zombie;
import com.zurragamez.src.entities.objects.lights.BulletLight;
import com.zurragamez.src.entities.particles.ParticleBlood;
import com.zurragamez.src.entities.particles.ParticleWall;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;

public class Projectile extends EntitySprite {
	
	private float dir;
	private float speed;
	private float heightChange;
	private int soundID;
	private BulletLight bulletLight;
	
	private Random random = new Random();
	
	public Projectile(float x, float y, float direction, float scale, Sprite sprite) {
		super(x, y, 0.05f, false, false, sprite);
		this.dir = direction + (random.nextFloat()-0.5f) * 0.04f;
		this.speed = 0.2f;
		soundID = AudioMaster.sus;
		
		hoverHeight = 10;
		heightChange = random.nextFloat() * 2f - 1;
		
		disableFog = false;
	}
	
	public void init(World world) {
		super.init(world);
		bulletLight = new BulletLight(x,y, this);
		bulletLight.init(world);
	}
	
	public void update() {
		x += Math.cos(dir) * speed;
		y += Math.sin(dir) * speed;
		
		hoverHeight -= heightChange;
		
		if(x < 0 || x > World.mapWidth-1 || y < 0 || y > World.mapHeight-1) remove = true;

		if(World.map[(int)(x)][(int)(y)] != 0) {
			for(int i = 0; i < 1; i++) {
				world.addEntity(new ParticleWall((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed), World.map[(int)(x)][(int)(y)]));
				world.addSound(new EntitySound((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed), soundID));
			}
			remove = true;
		}
		
		bulletLight.update();
		
		for(int i = 0; i < world.getEntities().size(); i++) {
			EntitySprite e = world.getEntities().get(i);
			if(e instanceof Zombie) {
				Zombie z = (Zombie)e;
				if(distance(z) <= z.getHitRadius()) {
					z.hurt(10);
					for(int a = 0; a < 10; a++) {
						world.addEntity(new ParticleBlood((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed)));
					}
					remove = true;
				}
			}
		}
	}
	
}
