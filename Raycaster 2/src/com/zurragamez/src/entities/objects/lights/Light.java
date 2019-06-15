package com.zurragamez.src.entities.objects.lights;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.World;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;

public class Light extends EntitySprite {
	
	protected int lightRadius;
	protected int rays = 200;
	protected int blurRadius = 3;
	protected int blurQuality = 4;
	protected float brightness = 1;

	protected List<Integer> affectedTiles = new ArrayList<Integer>();
	protected List<Integer> bluredTiles = new ArrayList<Integer>();
	
	public Light(float x, float y, float scale, boolean onGround, int lightRadius, boolean hasSound, Sprite sprite) {
		super(x, y, scale, onGround, hasSound, sprite);
		this.lightRadius = lightRadius;
		
		setupLight();
	}
	
	public void setupLight() {
		affectedTiles.clear();
		bluredTiles.clear();
		float dir = 0;
		for(int i = 0; i < rays; i++) {
			dir = (float)((Math.PI * 2) / rays) * i; 
			float dx = (float)Math.cos(dir) * 0.9f;
			float dy = (float)Math.sin(dir) * 0.9f;
			for(int dist = 0; dist < lightRadius; dist++) {
				int tilePosX = (int)(x + dx * dist);
				int tilePosY = (int)(y + dy * dist);
				int tilePosXY = tilePosX + tilePosY * World.mapHeight;
				
				if(tilePosX < 0 || tilePosY < 0 || tilePosX > World.mapWidth - 1 || tilePosY > World.mapHeight - 1) break;
				if(World.map[tilePosX][tilePosY] != 0) break;
				
				
				if(!affectedTiles.contains(tilePosXY)) {
					float distanceToTile = (float)Math.abs(Math.sqrt(((tilePosX) - x) * ((tilePosX) - x) + ((tilePosY) - y) * ((tilePosY) - y)));
					World.brightness[tilePosX][tilePosY] += (1f - ((float)distanceToTile / lightRadius)) * brightness;
					
					if(dist == lightRadius - 1) {
						for(int a = 0; a < blurQuality; a++) {
							float blurDir = (float)((Math.PI * 2) / blurQuality) * a;
							float blurDx = (float)Math.cos(blurDir) * 0.9f;
							float blurDy = (float)Math.sin(blurDir) * 0.9f;
							for(int blurDist = 0; blurDist < blurRadius; blurDist++) {
								int blurX = (int)(tilePosX + blurDx * blurDist);
								int blurY = (int)(tilePosY + blurDy * blurDist);
								int blurXY = blurX + blurY * World.mapHeight;
								if(!bluredTiles.contains(blurXY)) {
									if(blurXY < 0 && blurXY >= World.mapWidth * World.mapHeight) break;
									if(World.map[blurX][blurY] != 0) break;
									float d = (float)Math.abs(Math.sqrt((blurX - tilePosX) * (blurX - tilePosX) + (blurY - tilePosY) * (blurY - tilePosY))) * 3;
									if(d > 0) if(World.brightness[blurX][blurY] < World.brightness[tilePosX][tilePosY] / d) World.brightness[blurX][blurY] = World.brightness[tilePosX][tilePosY] / d;
									bluredTiles.add(blurXY);
								}
							}
						}
					}
					
					affectedTiles.add(tilePosXY);
				}
			}
		}
	}
	
	
	
}
