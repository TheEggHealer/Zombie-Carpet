package com.zurragamez.src.entities;

import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;

public class Zombie extends EntityMonster {

	private float speed = 0.01f;
	
	public Zombie(float x, float y) {
		super(x, y, 0.6f, true, Sprite.zombie_2);
		hoverHeight = sprite.height;
		
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
	}

	public void update() {
		super.update();
		
		float dx = main.getPlayer().x - x;
		float dy = main.getPlayer().y - y;
		float dir = (float)Math.atan2(dy, dx);
		
		move((float)Math.cos(dir) * speed, (float)Math.sin(dir) * speed);
	}
	
}
