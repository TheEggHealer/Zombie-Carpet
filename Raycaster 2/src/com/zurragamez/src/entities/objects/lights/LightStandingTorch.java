package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.entities.particles.ParticleSmoke;
import com.zurragamez.src.resources.Sprite;

public class LightStandingTorch extends Light {

	private int flickerCooldown = 4;
	private int smokeCooldown = 4;
	
	public LightStandingTorch(float x, float y) {
		super(x, y, 0.6f, true, 6, Sprite.object_light);
		nearRadius = 7;
	}
	
	public void update() {
		super.update();
//		Player p = main.getPlayer();
//		x = p.x;
//		y = p.y;
		setupLight();
		if(--flickerCooldown <= 0) {
			flickerCooldown = random.nextInt(20);
			brightness = 1f + random.nextFloat() * 0.6f;
		}
	}
	
	@Override
	public void updateNear() {
		super.updateNear();
		if(--smokeCooldown <= 0) {
			smokeCooldown = random.nextInt(20);
			main.addEntity(new ParticleSmoke(x, y));
		}
	}
	
}
