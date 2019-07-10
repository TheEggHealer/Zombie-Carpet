package com.zurragamez.src.entities;

import java.util.Random;

import com.zurragamez.src.Player;
import com.zurragamez.src.World;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.Source;
import com.zurragamez.src.utils.Constants;
import com.zurragamez.src.utils.OpenSimplexNoise;

public class EntitySprite {
	private static final String TAG = "EntitySprite";
	
	/** The more sources, the more sounds the entity can play at once. Total maximum amount of sources at once is 256. */
	protected int AMOUNT_OF_SOURCES = 4;
	
	protected float y, x;
	protected float scale;
	protected float hoverHeight;
	protected boolean onGround;
	protected boolean remove = false;
	protected World world;
	
	protected static Random random = new Random();
	protected static OpenSimplexNoise noise = new OpenSimplexNoise();
	
	protected boolean directional = true;
	protected float direction;
	protected Sprite[] sprites;
	protected int spriteSize;
	
	protected float nearRadius;
	
	public boolean disableFog = false;
	
	protected boolean hasSound = false;
	protected Source[] sources = new Source[AMOUNT_OF_SOURCES];

	public EntitySprite(float x, float y, float scale, boolean onGround, boolean hasSound) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.onGround = onGround;
		
		//TODO: Does this need to be initialized?
		sprites = new Sprite[4];
		
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
	
	/**
	 * Sets the sprites for the entity. If more than one is given, the entity becomes a 
	 * directional one and will change sprite depending on the viewing angle from the player. 
	 * This does however draw more performance.
	 * @param sprites You can give this method as many sprites as you want.
	 */
	public void initSprites(Sprite... sprites) {
		this.directional = sprites.length > 1;
		this.spriteSize = sprites[0].height;
		this.sprites = sprites;
	}
	
	public void setSources(int amount) {
		this.hasSound = true;
		for(Source source : sources) {
			if(source != null) {
				source.delete();
			}
		}
		
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			if(i < amount) sources[i] = new Source(TAG);
		}
		this.AMOUNT_OF_SOURCES = amount;
	}
	
	/**
	 * Queues the entity to be removed from the entities list in the Main class. Destroys the sources attached to this entity.
	 */
	public void remove() {
		if(hasSound) {
			for(Source source : sources) {
				if(source != null) {
					source.delete();
				}
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
	 * This method gets called 60 times per second, if the player is within it's nearRadius. Usually gets called before update().
	 */
	public void updateNear() {
		
	}
	
	/**
	 * Tries to move entity by given amount. Acts upon collision.
	 * @param dx Delta x
	 * @param dy Delta y
	 */
	public int move(float dx, float dy) {
		if(!checkCollision(x + dx, y + dy)) {
			this.x += dx;
			this.y += dy;
			return 0;
		} else {
			int side = 0;
			
			if(!checkCollision(x + dx, y)) this.x += dx; 
			else side++;
			
			if(!checkCollision(x, y + dy)) this.y += dy;
			else side--;
			
			return side;
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
	 * Returns the angle to the player in radians.
	 * @return Angle (float)
	 */
	protected float angleToPlayer() {
		Player p = world.getPlayer();
		return (float)Math.atan2(p.y - y, p.x - x);
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
		
		if(pitchChange) sources[sourceNumber].setPitch(Constants.ENTITYSPRITE_RAND_PITCH_MAX - random.nextFloat() * (Constants.ENTITYSPRITE_RAND_PITCH_MAX - Constants.ENTITYSPRITE_RAND_PITCH_MIN));
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
		if(pitchChange) sources[sourceNumber].setPitch(Constants.ENTITYSPRITE_RAND_PITCH_MAX - random.nextFloat() * (Constants.ENTITYSPRITE_RAND_PITCH_MAX - Constants.ENTITYSPRITE_RAND_PITCH_MIN));
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
	
	public Sprite getSprite(int direction) {
		return sprites[direction];
	}
	
	public void setSprite(Sprite sprite, int direction) {
		this.sprites[direction] = sprite;
	}
	
	public int getSpriteSize() {
		return spriteSize;
	}
	
	public int getRelativeDirection() {
		//TODO: Optimize
		if(directional) {
			float alpha = (float)Math.toDegrees(angleToPlayer());
			float beta = (float)Math.toDegrees(direction);
			float a = (float)(Math.abs(beta - alpha)) % 360;
			float b = a > 180 ? 360 - a : a;
			int sign = (alpha - beta >= 0 && alpha - beta <= 180) || (alpha - beta <=-180 && alpha - beta>= -360) ? 1 : -1; 
			float f = (sign == -1 ? 180 + (180 - b) : b);
			int r = Math.round(f / 360 * sprites.length) % sprites.length;
			return r;
		}
		return 0;
	}
	
	public boolean isOnGround() {
		return onGround;
	}
	
}
