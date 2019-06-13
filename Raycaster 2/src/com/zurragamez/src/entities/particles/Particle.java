package com.zurragamez.src.entities.particles;

import com.zurragamez.src.World;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;

public class Particle extends EntitySprite {

	protected final float GRAVITY_BASE = 0.004f;
	
	protected float gravity;
	protected float velZ = 0;
	protected float velX, velY;
	protected float bounciness;
	
	protected int life;
	
	private boolean landed = false;
	
	public Particle(float x, float y, float scale, Sprite sprite) {
		super(x, y, scale, false, false, sprite);
		
		disableFog = false;
	}

	public void update() {
		checkCollision();
		if(--life <= 0) remove = true;
		
		this.x += velX;
		this.y += velY;
		
		if(!landed) {
			velZ += gravity;
			hoverHeight += velZ;
		}
		
		if(hoverHeight > sprite.height * (1f - scale)){
			hoverHeight = sprite.height * (1f - scale);
			velX *= 0.2f;
			velY *= 0.2f;
			if(Math.abs(velZ) < 0.1f) {
				landed = true;
			}
			else velZ *= -bounciness;
		}
		
		if(hoverHeight < -(sprite.height * (1f - scale))){
			hoverHeight = -(sprite.height * (1f - scale));
			velX *= 0.2f;
			velY *= 0.2f;
			velZ *= -bounciness;
		}

		if((int)x < 0 || (int)x >= World.mapWidth || (int)y < 0 || (int)y >= World.mapHeight) remove = true;
	}
	
	public void checkCollision() {
		if(World.map[(int)(x)][(int)(y)] != 0) {
			float dx = x - (int)x;
			float dy = y - (int)y;
			float ax = 1 - dx;
			float ay = 1 - dy;
			float min = Math.min(dx, Math.min(dy, Math.min(ax, ay)));
			
			if(dx == min) {
				x -= min < 0.05 ? 0.05f : min;
				if(velX > 0) velX *= -1;
			} else if(dy == min) {
				y -= min < 0.05 ? 0.05f : min;
				if(velY > 0) velY *= -1;
			} else if(ax == min) {
				x += min < 0.05 ? 0.05f : min;
				if(velX < 0) velX *= -1;
			} else if(ay == min) {
				y += min < 0.05 ? 0.05f : min;
				if(velY < 0) velY *= -1;
			}
		}
	}
	
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		gravity = GRAVITY_BASE * sprite.height;
	}
	
}
