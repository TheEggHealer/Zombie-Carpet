package com.zurragamez.src.entities.objects.lights;

import com.zurragamez.src.Player;
import com.zurragamez.src.resources.Sprite;

public class LightStandingTorch extends Light {

	public LightStandingTorch(float x, float y) {
		super(x, y, 0.6f, true, 6, Sprite.object_light);
	}
	
	public void update() {
		Player p = main.getPlayer();
		x = p.x;
		y = p.y;
		setupLight();
	}
	
}
