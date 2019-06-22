package com.zurragamez.src.entities.particles;

import com.zurragamez.src.World;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;
import com.zurragamez.src.utils.Constants;

public class Particle extends EntitySprite {

	protected float gravity;
	protected float velX, velY, velZ;
	protected float bounciness;
	
	protected int life;
	
	private boolean landed = false;
	
	public Particle(float x, float y, float scale, Sprite sprite) {
		super(x, y, scale, false, false);
		initSprites(sprite);
		
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
		
		if(hoverHeight > spriteSize * (1f - scale)){
			hoverHeight = spriteSize * (1f - scale);
			velX *= 0.2f;
			velY *= 0.2f;
			if(Math.abs(velZ) < 0.1f) {
				landed = true;
			}
			else velZ *= -bounciness;
		}
		
		if(hoverHeight < -(spriteSize * (1f - scale))){
			hoverHeight = -(spriteSize * (1f - scale));
			velX *= 0.2f;
			velY *= 0.2f;
			velZ *= -bounciness;
		}

		if(!world.isWorldCoordinates(x, y)) remove = true;
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
	
	/**
	 * Sets the sprites for the entity. If more than one is given, the entity becomes a 
	 * directional one and will change sprite depending on the viewing angle from the player. 
	 * This does however draw more performance.<br><br>
	 * <i>This method also sets the gravity for the particle, based on the size of the sprites.</i>
	 * @param sprites You can give this method as many sprites as you want.
	 */
	public void initSprites(Sprite... sprites) {
		super.initSprites(sprites);
		gravity = Constants.ENTITYPARTICLE_GRAVITY_BASE * spriteSize;
	}
	
}
