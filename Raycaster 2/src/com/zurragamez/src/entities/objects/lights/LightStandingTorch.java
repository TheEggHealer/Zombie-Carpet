package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.Player;
import com.zurragamez.src.resources.Sprite;

public class LightStandingTorch extends Light {

	private int cooldown = 4;
	
	public LightStandingTorch(float x, float y) {
		super(x, y, 0.6f, true, 6, Sprite.object_light);
	}
	
	public void update() {
//		Player p = main.getPlayer();
//		x = p.x;
//		y = p.y;
		setupLight();
		if(--cooldown <= 0) {
			cooldown = random.nextInt(20);
			brightness = 1f + random.nextFloat() * 0.2f;
		}
	}
	
}
