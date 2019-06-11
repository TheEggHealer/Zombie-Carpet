package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.Projectile;
import com.zurragamez.src.entities.particles.ParticleBulletSmoke;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.Source;

public class BulletLight extends Light {

	private int flickerCooldown = 4;
	private int smokeCooldown = 4;
	private int soundCooldown = random.nextInt(400) + 40;
	private Projectile projectile;
	private Source source;
	private int sound_flame1, sound_flame2;
	
	public BulletLight(float x, float y, Projectile projectile) {
		super(x, y, 0.6f, true, 1, Sprite.object_light);
		this.projectile = projectile;
		nearRadius = 7;
		this.blurRadius = 30;
		this.brightness = 1f;
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
			main.addEntity(new ParticleBulletSmoke(x, y));
		}
	}
	
}
