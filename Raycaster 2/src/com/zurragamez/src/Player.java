package com.zurragamez.src;
import java.util.Random;

import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.particles.ParticleAmbient;
import com.zurragamez.src.entities.particles.ParticleBlood;
import com.zurragamez.src.entities.projectiles.EntityProjectile;
import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;
import com.zurragamez.src.utils.Camera;

public class Player {
	private static final String TAG = "Player";
	
	public float x, y;
	public float dir;
	public float health = 100f;
	
	private float speed = 0.03f;
	private float rotSpeed = 0.04f;
	
	private int ambientParticleCooldown = 4;
	
	private World world;
	private Camera camera;
	
	private int sound_walk1, sound_walk2, sound_shoot1;
	private int walkCooldown = 10;
	private Source walkingSource;
	private Source shootingSource;
	private Source hurtSource;
	
	private Random random = new Random();
	
	public Player(World world, Camera camera, float x, float y) {
		this.x = y;
		this.y = x;
		
		this.world = world;
		this.camera = camera;
		
		walkingSource = new Source(TAG);
		walkingSource.setLocation(x, y);
		shootingSource = new Source(TAG);
		shootingSource.setLocation(x, y);
		hurtSource = new Source(TAG);
		sound_walk1 = AudioMaster.walk_01;
		sound_walk2 = AudioMaster.walk_02;
		sound_shoot1 = AudioMaster.player_shoot;
	}
	
	public void update() {
		if(health <= 0) {
			System.out.println("GAME OVER!");
		}
		movement();
		
		if(Keyboard.shoot || Mouse.button == 1) {
			Mouse.button = 0;
			
			shootingSource.setPitch(1.1f - random.nextFloat() * 0.4f);
			shootingSource.play(sound_shoot1);
			
			world.addEntity(new EntityProjectile(x, y, dir, 0.2f, Sprite.ammo));
		}
		
		if(--ambientParticleCooldown <= 0) {
			ambientParticleCooldown = 16;
			float dir = (float)(this.dir + random.nextFloat() * (Math.PI / 2) - Math.PI / 4);
			float dist = random.nextFloat() * 6 + 1;
			float x = (float)Math.cos(dir) * dist + this.x;
			float y = (float)Math.sin(dir) * dist + this.y;
			if(world.isWorldCoordinates(x, y)) world.addEntity(new ParticleAmbient(x, y));
		}
	}
	
	public void hurt(EntitySprite ent, float damage) {
		hurtSource.setLocation(x, y);
		//hurtSource.play(damage_player1);
		world.addEntity(new ParticleBlood((float)(x - Math.cos (dir) * speed), (float)(y - Math.sin(dir) * speed)));
	}
	
	public void movement() {
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		
		float dx = 0;
		float dy = 0;
		
		if(Keyboard.up) {
			dx += Math.cos(dir);
			dy += Math.sin(dir);
		} 
		if(Keyboard.down) {
			dx -= Math.cos(dir);
			dy -= Math.sin(dir);
		} 
		if(Keyboard.left) {
			dx += Math.cos(dir + Math.PI / 2);
			dy += Math.sin(dir + Math.PI / 2);
		} 
		if(Keyboard.right) {
			dx += Math.cos(dir - Math.PI / 2);
			dy += Math.sin(dir - Math.PI / 2);
		}
		
		dx *= speed;
		dy *= speed;
		
		if(!(dx == 0 && dy == 0)) {
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
			walkingSource.setLocation(x, y);
			if(--walkCooldown <= 0) {
				walkCooldown = 25;
				walkingSource.setPitch(1.1f - random.nextFloat() * 0.2f);
				walkingSource.play(random.nextInt(2) == 1 ? sound_walk1 : sound_walk2);
			}
		}
		
		shootingSource.setLocation(x,y);
		
		Mouse.update();
		if(Mouse.finalRotation != 0) {
			float rotation = Mouse.finalRotation * -0.03f;
			camera.rotate(rotation);
			dir += rotation;

		}
		
		if(Keyboard._left) {
			camera.rotate(rotSpeed);
			dir += rotSpeed;
		} 
		else if(Keyboard._right) {
			camera.rotate(-rotSpeed);
			dir -= rotSpeed;
		}
		
		//TODO: Figure out x & y
		camera.setLocation(y, x);
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
	
	
}
