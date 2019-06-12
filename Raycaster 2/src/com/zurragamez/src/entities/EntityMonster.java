package com.zurragamez.src.entities;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.resources.Sprite;

public class EntityMonster extends EntitySprite {

	public float health = 100;
	
	protected boolean ai_roam, ai_followPlayer;
	protected boolean sound_living, sound_hurt, dead, sound_walk;
	
	protected float detectionRadius;
	protected float hitRadius;
	
	private int maxTimeSinceSound, minTimeSinceSound;
	private int timeSinceSound;
	
	public int sound_death;
	protected List<Integer> soundBuffers_living = new ArrayList<Integer>(), 
							soundBuffers_hurt = new ArrayList<Integer>(),
							soundBuffers_walk = new ArrayList<Integer>();
	
	protected int walkCooldown = 10;
	
	public EntityMonster(float x, float y, float scale, boolean onGround, Sprite sprite) {
		super(x, y, scale, onGround, sprite); 
		maxTimeSinceSound = 1800;
		minTimeSinceSound = 400;
		timeSinceSound = minTimeSinceSound + random.nextInt(maxTimeSinceSound - minTimeSinceSound);
	}
	
	public void update() {
		super.update();
		
		if(ai_roam) roam();
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			sources[i].timeSinceUsed++;
			sources[i].setLocation(x, y);
		}
		
		if(sound_living) {
			if(--timeSinceSound <= 0) {
				timeSinceSound = minTimeSinceSound + random.nextInt(maxTimeSinceSound - minTimeSinceSound);
				
				playSound(getSoundLiving());
			}
		}
	}
	
	public void move(float dx, float dy) {
		if(!dead) {			
			super.move(dx, dy);
			
			if(sound_walk) {
				if(--walkCooldown <= 0) {
					walkCooldown = 35;
					playSound(getSoundWalk());
	;			}
			}
		}
	}
	
	public void updateNear() {
		super.updateNear();
		
	}
	
	/**
	 * Plays a random hurt sound, removed damage from health and induces knockback
	 * @param damage
	 */
	public void hurt(float damage) {
		if(!dead) {
			playSound(getSoundHurt());
			health -= damage;
			if(health <= 0) {
				playSound(sound_death);
				ai_followPlayer = false;
				ai_roam = false;
				sound_living = false;
				
				dead = true;
				die();
				//health = 100;
			}
		}
	}
	
	/**
	 *  When the ent dies
	 */
	public void die() {
	}
	
	/**
	 * AI that automatically moves the entity at random.
	 */
	public void roam() {
		//TODO: Add roaming ai.
	}

	public float getHitRadius() {
		return hitRadius;
	}
	
	/**
	 * Returns a random buffer from the soundBuffers_living list.
	 * @return random buffer from the soundBuffers_living list.
	 */
	public int getSoundLiving() {
		return soundBuffers_living.get(random.nextInt(soundBuffers_living.size()));
	}
	
	/**
	 * Returns a random buffer from the soundBuffers_hurt list.
	 * @return random buffer from the soundBuffers_hurt list.
	 */
	public int getSoundHurt() {
		return soundBuffers_hurt.get(random.nextInt(soundBuffers_hurt.size()));
	}
	
	/**
	 * Returns a random buffer from the soundBuffers_die list.
	 * @return random buffer from the soundBuffers_die list.
	 */
	public int getSoundDie() {
		return soundBuffers_hurt.get(random.nextInt(soundBuffers_hurt.size()));
	}
	
	/**
	 * Returns a random buffer from the soundBuffers_walk list.
	 * @return random buffer from the soundBuffers_walk list.
	 */
	public int getSoundWalk() {
		return soundBuffers_walk.get(random.nextInt(soundBuffers_walk.size()));
	}
	
	/**
	 * Finds the sound source with the longest time since used, then sends the specified sound buffer to that source.
	 * @param id
	 */
	public void playSound(int buffer) {
		int sourceNumber = 0;
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(sources[i].timeSinceUsed > sources[sourceNumber].timeSinceUsed) sourceNumber = i;
		}
		
		sources[sourceNumber].setPitch(1.1f - random.nextFloat() * 0.3f);
		sources[sourceNumber].play(buffer);
	}
	
}
