package com.zurragamez.src.entities;

import com.zurragamez.src.Projectile;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;

public class Zombie extends EntityMonster {

	private float speed = 0.01f;
	private Source shootingSource;
	private int sound_shoot1;
	
	public Zombie(float x, float y) {
		super(x, y, 0.6f, true, Sprite.zombie_2);
		hoverHeight = sprite.height;
		
		shootingSource = new Source();
		shootingSource.setLocation(x, y);
		sound_shoot1 = AudioMaster.loadSound("res/sounds/shoot.ogg");
		
		ai_roam = true;
		ai_followPlayer = true;
		
		detectionRadius = 3;
		hitRadius = 0.035f;
		nearRadius = 10;
		
		sound_living = true;
		sound_hurt = true;
		sound_walk = true;
		soundBuffers_living.add(AudioMaster.loadSound("res/sounds/zombie_01.ogg"));
		soundBuffers_living.add(AudioMaster.loadSound("res/sounds/zombie_02.ogg"));
		soundBuffers_living.add(AudioMaster.loadSound("res/sounds/zombie_03.ogg"));
		soundBuffers_living.add(AudioMaster.loadSound("res/sounds/zombie_04.ogg"));
		soundBuffers_living.add(AudioMaster.loadSound("res/sounds/zombie_05.ogg"));
		soundBuffers_hurt.add(AudioMaster.loadSound("res/sounds/zombie_hurt_01.ogg"));
		soundBuffers_hurt.add(AudioMaster.loadSound("res/sounds/zombie_hurt_02.ogg"));
		soundBuffers_hurt.add(AudioMaster.loadSound("res/sounds/zombie_hurt_03.ogg"));
		soundBuffers_hurt.add(AudioMaster.loadSound("res/sounds/zombie_hurt_04.ogg"));
		soundBuffers_walk.add(AudioMaster.loadSound("res/sounds/player/walk.ogg"));
		soundBuffers_walk.add(AudioMaster.loadSound("res/sounds/player/walk2.ogg"));
		sound_death = AudioMaster.loadSound("res/sounds/player/death.ogg");
	}

	public void update() {
		super.update();
		
		if(!dead) {
			float dx = main.getPlayer().x - x;
			float dy = main.getPlayer().y - y;
			float dir = (float)Math.atan2(dy, dx);
			
			if(1f + random.nextInt(20) == 1) {			
					shootingSource.setPitch(1.1f - random.nextFloat() * 0.4f);
					shootingSource.play(sound_shoot1);
					
					main.addEntity(new Projectile(x, y, dir, 0.2f, Sprite.ammo));
			}
		
			move((float)Math.cos(dir) * speed, (float)Math.sin(dir) * speed);
		}
	}
	
}
