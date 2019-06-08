package com.zurragamez.src.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zurragamez.src.Main;
import com.zurragamez.src.Player;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.Source;

public class EntitySprite {
	
	/** The more sources, the more sounds the entity can play at once. However, for the sake of memory, this value should be as low as possible. */
	protected final int AMOUNT_OF_SOURCES = 5;
	
	protected float y, x;
	protected float scale;
	protected float hoverHeight;
	protected boolean onGround;
	protected boolean remove = false;
	protected Sprite sprite;
	protected Main main;
	protected static Random random = new Random();
	
	protected float nearRadius;
	
	public boolean disableFog = false;
	
	protected List<Integer> soundBuffers_living = new ArrayList<Integer>(), soundBuffers_hurt = new ArrayList<Integer>();
	protected Source[] sources = new Source[AMOUNT_OF_SOURCES];

	public EntitySprite(float x, float y, float scale, boolean onGround, Sprite sprite) {
		this.x = x;
		this.y = y;
		this.scale = scale;
		this.onGround = onGround;
		setSprite(sprite);
		
		for(int i = 0; i < AMOUNT_OF_SOURCES; i++) {
			sources[i] = new Source();
		}
	}
	
	public void init(Main main) {
		this.main = main;
	}
	
	/**
	 * Queues the entity to be removed from the entities list in the Main class.
	 */
	public void remove() {
		for(Source source : sources) {
			source.delete();
		}
		remove = true;
	}
	
	public boolean isRemoved() {
		return remove;
	}
	
	public void update() {
		if(distanceToPlayer() <= nearRadius) updateNear();
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
		return Main.map[(int)y][(int)x] != 0;
	}
	
	/**
	 * Returns the distance between this entity and another.
	 * @param e The other entity.
	 * @return Distance (float).
	 */
	protected float distance(EntitySprite e) {
		return (float)Math.abs((e.x - x) * (e.x - x) + (e.y - y) * (e.y - y));
	}
	
	protected float distanceToPlayer() {
		Player p = main.getPlayer();
		return (float)Math.abs((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
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
