package com.zurragamez.src.entities;

import com.zurragamez.src.entities.particles.ParticleGore;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;

public class Zombie extends EntityMonster {
	private static final String TAG = "EntitySprite>EntityMonster>Zombie";
	
	private Source shootingSource;
	private int sound_shoot1;
	
	public Zombie(float x, float y) {
		super(x, y, 0.65f, true);
		hoverHeight = spriteSize;

		initSprites(Sprite.zombie_sheet[0], Sprite.zombie_sheet[1].flip(), Sprite.zombie_sheet[2].flip(), Sprite.zombie_sheet[3].flip(),  Sprite.zombie_sheet[4],  Sprite.zombie_sheet[3],  Sprite.zombie_sheet[2],  Sprite.zombie_sheet[1]);
		
		//TODO: Use sources[] instead
		shootingSource = new Source(TAG);
		shootingSource.setLocation(x, y);
		sound_shoot1 = AudioMaster.player_shoot;
		
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
		
		if(!dead) {
			float dx = world.getPlayer().x - x;
			float dy = world.getPlayer().y - y;
			float dir = (float)Math.atan2(dy, dx);
			
			if(1f + random.nextInt(20) == 1) {			
//					shootingSource.setPitch(1.1f - random.nextFloat() * 0.4f);
//					shootingSource.play(sound_shoot1);
					
//					main.addEntity(new Projectile(x, y, dir, 0.2f, Sprite.ammo));
			}

//			move((float)Math.cos(dir) * speed, (float)Math.sin(dir) * speed);
		}
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
