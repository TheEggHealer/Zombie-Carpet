package com.zurragamez.src.entities;

import com.zurragamez.src.resources.Sprite;

public class EntityMonster extends EntitySprite {

	protected boolean ai_roam, ai_followPlayer;
	protected boolean sound_living, sound_hurt, sound_death;
	
	protected float detectionRadius;
	protected float hitRadius;
	
	private int maxTimeSinceSound, minTimeSinceSound;
	private int timeSinceSound;
	
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
	
	public void updateNear() {
		super.updateNear();
		
	}
	
	/**
	 * Plays a random hurt sound, removed damage from health and induces knockback
	 * @param damage
	 */
	public void hurt(float damage) {
		playSound(getSoundHurt());
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
	 * Finds the sound source with the longest time since used, then sends the specified sound buffer to that source.
	 * @param id
	 */
	public void playSound(int buffer) {
		int sourceNumber = 0;
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(sources[i].timeSinceUsed > sources[sourceNumber].timeSinceUsed) sourceNumber = i;
		}
		
		sources[sourceNumber].setPitch(1.1f - random.nextFloat() * 0.2f);
		sources[sourceNumber].play(buffer);
	}
	
}
