package com.zurragamez.src.entities.projectiles;
import com.zurragamez.src.World;
import com.zurragamez.src.entities.EntitySound;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.monsters.Zombie;
import com.zurragamez.src.entities.objects.lights.BulletLight;
import com.zurragamez.src.entities.particles.ParticleBlood;
import com.zurragamez.src.entities.particles.ParticleWall;
import com.zurragamez.src.resources.Sprite;

public class EntityProjectile extends EntitySprite {
	
	protected float speed;
	protected float velZ;
	protected int shootSound, hitSound;
	protected BulletLight bulletLight;
	
	public EntityProjectile(float x, float y, float dir, float spread, float scale, Sprite sprite) {
		super(x, y, 0.05f, false, false);
		initSprites(sprite);
		
		direction = dir + (random.nextFloat()-0.5f) * spread;
		velZ = random.nextFloat() * spread;
		
		hoverHeight = 10;
		disableFog = false;
	}
	
	public void init(World world) {
		super.init(world);
		bulletLight = new BulletLight(x,y, this);
		bulletLight.init(world);
		world.addSound(new EntitySound(x, y, shootSound));
	}
	
	public void setSounds(int shootSound, int hitSound) {
		this.hitSound = hitSound;
		this.shootSound = shootSound;
	}
	
	public void update() {
		super.update();
		x += Math.cos(direction) * speed;
		y += Math.sin(direction) * speed;
		
		hoverHeight -= velZ;
		
		if(!world.isWorldCoordinates(x, y)) remove();

		if(World.map[(int)(x)][(int)(y)] != 0) {
			for(int i = 0; i < 1; i++) {
				if(distanceToPlayer() <= 10) {
					world.addEntity(new ParticleWall((float)(x - Math.cos (direction) * speed), (float)(y - Math.sin(direction) * speed), World.map[(int)(x)][(int)(y)]));
					world.addSound(new EntitySound((float)(x - Math.cos (direction) * speed), (float)(y - Math.sin(direction) * speed), hitSound));
				}
			}
			remove();
		}
		
		bulletLight.update();
		
		for(int i = 0; i < world.getEntities().size(); i++) {
			EntitySprite e = world.getEntities().get(i);
			if(e instanceof Zombie) {
				Zombie z = (Zombie)e;
				if(distance(z) <= z.getHitRadius()) {
					z.hurt(10);
					for(int a = 0; a < 10; a++) {
						world.addEntity(new ParticleBlood((float)(x - Math.cos (direction) * speed), (float)(y - Math.sin(direction) * speed)));
					}
					remove();
				}
			}
		}
	}
	
}
