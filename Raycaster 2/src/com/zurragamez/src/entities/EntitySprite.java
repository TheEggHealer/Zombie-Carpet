package com.zurragamez.src.entities;

import java.util.Random;

import com.zurragamez.src.Player;
import com.zurragamez.src.World;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.Source;

public class EntitySprite {
	private static final String TAG = "EntitySprite";
	
	/** The more sources, the more sounds the entity can play at once. Total maximum amount of sources at once is 256. */
	protected int AMOUNT_OF_SOURCES = 4;
	
	protected float y, x;
	protected float scale;
	protected float hoverHeight;
	protected boolean onGround;
	protected boolean remove = false;
	protected Sprite sprite;
	protected World world;
	protected static Random random = new Random();
	
	protected float nearRadius;
	
	public boolean disableFog = false;
	
	protected boolean hasSound = false;
	protected Source[] sources = new Source[AMOUNT_OF_SOURCES];

	public EntitySprite(float x, float y, float scale, boolean onGround, boolean hasSound, Sprite sprite) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.onGround = onGround;
		setSprite(sprite);
		
		this.hasSound = hasSound;
		if(hasSound) {
			for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
				sources[i] = new Source(TAG);
			}
		}
	}
	
	public void init(World world) {
		this.world = world;
	}
	
	public void setSources(int amount) {
		for(Source source : sources) {
			source.delete();
		}
		
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(i < amount) sources[i] = new Source(TAG);
		}
		this.AMOUNT_OF_SOURCES = amount;
	}
	
	/**
	 * Queues the entity to be removed from the entities list in the Main class.
	 */
	public void remove() {
		if(hasSound) {
			for(Source source : sources) {
				source.delete();
			}
		}
		remove = true;
	}
	
	public boolean isRemoved() {
		return remove;
	}
	
	public void update() {
		if(distanceToPlayer() <= nearRadius) updateNear();
		
		if(hasSound) {
			for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
				sources[i].timeSinceUsed++;
				sources[i].setLocation(x, y);
			}
		}
	}
	
	/**
	 * This method gets called 60 times per second, if the player is within it's nearRadius.
	 */
	public void updateNear() {
		
	}
	
	/**
	 * Tries to move entity by given amount. Stops if collision is detected.
	 * @param dx Delta x
	 * @param dy Delta y
	 */
	public void move(float dx, float dy) {
		if(!checkCollision(x + dx, y + dy)) {
			this.x += dx;
			this.y += dy;
		} else {
			if(!checkCollision(x + dx, y)) {
				this.x += dx;
			} 
			
			if(!checkCollision(x, y + dy)) {
				this.y += dy;
			}
		}
	}
	
	/**
	 * Checks if the given position contains a wall or not. Returns true if a collision is detected.
	 * @param y
	 * @param x
	 * @return True if collision, else False
	 */
	protected boolean checkCollision(float x, float y) {
		return World.map[(int)x][(int)y] != 0;
	}
	
	/**
	 * Returns the distance between this entity and another.
	 * @param e The other entity.
	 * @return Distance (float).
	 */
	protected float distance(EntitySprite e) {
		return (float)Math.abs((e.x - x) * (e.x - x) + (e.y - y) * (e.y - y));
	}
	
	/**
	 * Returns the distance between this entity and the player.
	 * @return Distance (float)
	 */
	protected float distanceToPlayer() {
		Player p = world.getPlayer();
		return (float)Math.abs(Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y)));
	}
	
	/**
	 * Finds the sound source with the longest time since used, then sends the specified sound buffer to that source. Set pitchChange to true if you want random pitch.
	 * @param buffer Soundbuffer id
	 * @param pitchChange Enables random pitch (range = 0.9 - 1.1)
	 */
	public void playSound(int buffer, boolean pitchChange) {
		int sourceNumber = 0;
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(sources[i].timeSinceUsed > sources[sourceNumber].timeSinceUsed) sourceNumber = i;
		}
		
		if(pitchChange) sources[sourceNumber].setPitch(1.1f - random.nextFloat() * 0.2f);
		sources[sourceNumber].play(buffer);
	}
	
	/**
	 * Finds the sound source with the longest time since used, then sends the specified sound buffer to that source. Set pitchChange to true if you want random pitch. Allows you to set the gain.
	 * @param buffer Soundbuffer id
	 * @param pitchChange Enables random pitch (range = 0.9 - 1.1)
	 * @param gain Sets the gain of the source
	 */
	public void playSound(int buffer, boolean pitchChange, float gain) {
		int sourceNumber = 0;
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(sources[i].timeSinceUsed > sources[sourceNumber].timeSinceUsed) sourceNumber = i;
		}
		
		sources[sourceNumber].setGain(gain);
		if(pitchChange) sources[sourceNumber].setPitch(1.1f - random.nextFloat() * 0.2f);
		sources[sourceNumber].play(buffer);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getScale() {
		return scale;
	}
	
	public float getHoverHeight() {
		return hoverHeight;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public boolean isOnGround() {
		return onGround;
	}
	
}
