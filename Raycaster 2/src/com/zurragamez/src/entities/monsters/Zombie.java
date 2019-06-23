package com.zurragamez.src.entities.monsters;

import com.zurragamez.src.entities.particles.ParticleGore;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;

public class Zombie extends EntityMonster {
	private static final String TAG = "EntitySprite>EntityMonster>Zombie";
	
	private Source shootingSource;
	
	public Zombie(float x, float y) {
		super(x, y, 0.65f, true);
		hoverHeight = spriteSize;

		initSprites(Sprite.zombie_sheet[0], Sprite.zombie_sheet[1].flip(), Sprite.zombie_sheet[2].flip(), Sprite.zombie_sheet[3].flip(),  Sprite.zombie_sheet[4],  Sprite.zombie_sheet[3],  Sprite.zombie_sheet[2],  Sprite.zombie_sheet[1]);
		
		//TODO: Use sources[] instead
		shootingSource = new Source(TAG);
		shootingSource.setLocation(x, y);
		
		ai_roam = true;
		ai_followPlayer = true;
		
		detectionRadius = 3;
		hitRadius = 0.035f;
		nearRadius = 10;
		
		has_sound_living = true;
		has_sound_hurt = true;
		has_sound_walk = true;
		soundBuffers_living.add(AudioMaster.zombie_01);
		soundBuffers_living.add(AudioMaster.zombie_02);
		soundBuffers_living.add(AudioMaster.zombie_03);
		soundBuffers_living.add(AudioMaster.zombie_04);
		soundBuffers_living.add(AudioMaster.zombie_05);
		soundBuffers_hurt.add(AudioMaster.zombie_hurt_01);
		soundBuffers_hurt.add(AudioMaster.zombie_hurt_02);
		soundBuffers_hurt.add(AudioMaster.zombie_hurt_03);
		soundBuffers_hurt.add(AudioMaster.zombie_hurt_04);
		soundBuffers_walk.add(AudioMaster.walk_01);
		soundBuffers_walk.add(AudioMaster.walk_02);
		sound_death = AudioMaster.zombie_death_01;
	}

	public void update() {
		super.update();
	}
	
	public void die() {
		initSprites(Sprite.zombie_2dead);
		
		//TODO: Use for loop instead
		world.addEntity(new ParticleGore(x, y));
		world.addEntity(new ParticleGore(x, y));
		world.addEntity(new ParticleGore(x, y));
		world.addEntity(new ParticleGore(x, y));
		world.addEntity(new ParticleGore(x, y));
	}

}
