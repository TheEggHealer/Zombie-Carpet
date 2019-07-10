package com.zurragamez.src.entities.projectiles;

import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;

public class ProjectileSimple extends EntityProjectile {

	public ProjectileSimple(float x, float y, float dir) {
		super(x, y, dir, 0.04f, 0.05f, Sprite.ammo);
	
		speed = 0.2f;
		setSounds(AudioMaster.player_shoot, AudioMaster.walk_01);
	}

}
