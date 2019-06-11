package com.zurragamez.src;
import java.util.Random;

import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;
import com.zurragamez.src.utils.Camera;

public class Player {
	
	public float x, y;
	public float dir;
	
	private float speed = 0.03f;
	private float rotSpeed = 0.04f;
	
	private Main main;
	private Camera camera;
	
	private int sound_walk1, sound_walk2, sound_shoot1;
	private int walkCooldown = 10;
	private Source walkingSource;
	private Source shootingSource;
	
	private Random random = new Random();
	
	public Player(Main main, Camera camera, float x, float y) {
		this.x = y;
		this.y = x;
		
		this.main = main;
		this.camera = camera;
		
		walkingSource = new Source();
		walkingSource.setLocation(x, y);
		shootingSource = new Source();
		shootingSource.setLocation(x, y);
		sound_walk1 = AudioMaster.loadSound("res/sounds/player/walk.ogg");
		sound_walk2 = AudioMaster.loadSound("res/sounds/player/walk2.ogg");
		sound_shoot1 = AudioMaster.loadSound("res/sounds/shoot.ogg");
		
	}
	
	public void update() {
		movement();
		
		if(Keyboard.shoot || Mouse.button == 1) {
			//Keyboard.shoot = false;
			Mouse.button = 0;
			
			shootingSource.setPitch(1.1f - random.nextFloat() * 0.4f);
			shootingSource.play(sound_shoot1);
			
			main.addEntity(new Projectile(x, y, dir, 0.2f, Sprite.ammo));
		}
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
		return Main.map[(int)x][(int)y] != 0;
	}
	
	
}
