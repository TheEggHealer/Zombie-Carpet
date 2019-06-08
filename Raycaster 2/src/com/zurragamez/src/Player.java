package com.zurragamez.src;
import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.utils.Camera;

public class Player {
	
	public float x, y;
	public float dir;
	
	private float speed = 0.06f;
	private float rotSpeed = 0.04f;
	
	private Main main;
	private Camera camera;
	
	public Player(Main main, Camera camera, float x, float y) {
		this.x = y;
		this.y = x;
		
		this.main = main;
		this.camera = camera;
	}
	
	public void update() {
		movement();
		
		if(Keyboard.shoot || Mouse.button == 1) {
			Keyboard.shoot = false;
			Mouse.button = 0;
			
			main.addEntity(new Projectile(x, y, dir, 0.2f, Sprite.ammo));
		}
	}
	
	public void movement() {
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		
		float dx = 0;
		float dy = 0;
		
		if(Keyboard.up) {
			dx += Math.cos(dir) * speed;
			dy += Math.sin(dir) * speed;
		} 
		if(Keyboard.down) {
			dx -= Math.cos(dir) * speed;
			dy -= Math.sin(dir) * speed;
		} 
		if(Keyboard.left) {
			dx += Math.cos(dir + Math.PI / 2) * speed;
			dy += Math.sin(dir + Math.PI / 2) * speed;
		} 
		if(Keyboard.right) {
			dx += Math.cos(dir - Math.PI / 2) * speed;
			dy += Math.sin(dir - Math.PI / 2) * speed;
		}
		
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
		
		if(Mouse.dx != 0) {
			float rotation = Mouse.dx * -0.03f;
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
		
		camera.setLocation(y, x);
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
	
	
}
