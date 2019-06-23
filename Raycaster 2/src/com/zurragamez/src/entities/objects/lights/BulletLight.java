package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.entities.particles.ParticleBulletSmoke;
import com.zurragamez.src.entities.projectiles.EntityProjectile;
import com.zurragamez.src.resources.Sprite;

public class BulletLight extends Light {

	private int smokeCooldown = 4;
	private EntityProjectile projectile;
	
	public BulletLight(float x, float y, EntityProjectile projectile) {
		super(x, y, 0.6f, true, 1, false, Sprite.object_light);
		this.projectile = projectile;
		nearRadius = 7;
		this.blurRadius = 30;
		this.brightness = 0.1f;
	}
	
	public void update() {
		super.update();
		this.x = projectile.getX();
		this.y = projectile.getY();
		setupLight();
	}
	
	@Override
	public void updateNear() {
		super.updateNear();
		if(--smokeCooldown <= 0) {
			smokeCooldown = random.nextInt(20);
			world.addEntity(new ParticleBulletSmoke(x, y));
		}
	}
	
}
