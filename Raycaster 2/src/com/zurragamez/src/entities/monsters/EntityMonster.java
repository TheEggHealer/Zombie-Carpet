package com.zurragamez.src.entities.monsters;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.utils.Constants;

public class EntityMonster extends EntitySprite {

	public float health = Constants.MONSTER_HEALTH;
	
	protected boolean ai_roam, ai_followPlayer;
	protected boolean has_sound_living, has_sound_hurt, dead, has_sound_walk;
	
	protected float detectionRadius;
	protected float hitRadius;
	
	private int maxTimeSinceSound, minTimeSinceSound;
	private int timeSinceSound;
	
	public int sound_death;
	protected List<Integer> soundBuffers_living = new ArrayList<Integer>(), 
							soundBuffers_hurt = new ArrayList<Integer>(),
							soundBuffers_walk = new ArrayList<Integer>();
	
	protected float walkingSpeed = Constants.MONSTER_WALKING_SPEED;
	protected int walkSoundCooldown = random.nextInt(Constants.MONSTER_RAND_WALK_SOUND_COOLDOWN_MAX);
	protected int walkTime = random.nextInt(Constants.MONSTER_RAND_WALK_TIME_MAX - Constants.MONSTER_RAND_WALK_TIME_MIN) + Constants.MONSTER_RAND_WALK_TIME_MIN;
	protected int noiseOffsetX, noiseOffsetY;
	protected boolean walking = false;
	
	public EntityMonster(float x, float y, float scale, boolean onGround) {
		super(x, y, scale, onGround, true);
		
		maxTimeSinceSound = 1800;
		minTimeSinceSound = 400;
		timeSinceSound = minTimeSinceSound + random.nextInt(maxTimeSinceSound - minTimeSinceSound);
	}
	
	public void update() {
		super.update();
		
		if(ai_roam) roam();
		
		if(has_sound_living) {
			if(--timeSinceSound <= 0) {
				timeSinceSound = minTimeSinceSound + random.nextInt(maxTimeSinceSound - minTimeSinceSound);
				
				playSound(getSoundLiving(), true);
			}
		}
	}
	
	public int move(float dx, float dy) {
		if(!dead) {			
			int r = super.move(dx, dy);
			
			if(has_sound_walk) {
				if(--walkSoundCooldown <= 0) {
					walkSoundCooldown = Constants.MONSTER_WALK_SOUND_COOLDOWN;
					playSound(getSoundWalk(), true);
				}
			}
			return r;
		}
		
		return 2;
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
			playSound(getSoundHurt(), true);
			health -= damage;
			if(health <= 0) {
				playSound(sound_death, true);
				ai_followPlayer = false;
				ai_roam = false;
				has_sound_living = false;
				
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
		walkTime--;
		if(walkTime < 0) {
			walking = !walking;
			walkTime = random.nextInt(Constants.MONSTER_RAND_WALK_TIME_MAX - Constants.MONSTER_RAND_WALK_TIME_MIN) + Constants.MONSTER_RAND_WALK_TIME_MIN;
			if(walking) {
				noiseOffsetX = random.nextInt(500);
				noiseOffsetY = random.nextInt(500);
				direction = (float)(random.nextFloat() * Math.PI * 2);
			}
		} 
		if(walking) {
			this.direction += (float)(noise.eval(x + noiseOffsetX, y + noiseOffsetY)) / 20f;
			int r = move((float)Math.cos(direction) * walkingSpeed, (float)Math.sin(direction) * walkingSpeed);
			if(r != 0) direction = (float)(random.nextFloat() * Math.PI * 2);
		}
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
	
}
