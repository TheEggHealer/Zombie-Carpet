package com.zurragamez.src.entities.objects.lights;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.Main;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;

public class Light extends EntitySprite {

	protected int lightRadius;
	protected int rays = 80;

	protected List<Integer> affectedTiles = new ArrayList<Integer>();
	
	public Light(float x, float y, float scale, boolean onGround, int lightRadius, Sprite sprite) {
		super(x, y, scale, onGround, sprite);
		this.lightRadius = lightRadius;
		
		setupLight();
	}
	
	public void setupLight() {
		float dir = 0;
		for(int i = 0; i < rays; i++) {
			dir = (float)((Math.PI * 2) / rays) * i; 
			float dx = (float)Math.cos(dir) * 0.9f;
			float dy = (float)Math.sin(dir) * 0.9f;
			for(int dist = 0; dist < lightRadius; dist++) {
				int tilePosX = (int)(x + dx * dist);
				int tilePosY = (int)(y + dy * dist);
				int tilePosXY = tilePosX + tilePosY * Main.mapWidth;
				
				if(tilePosX < 0 || tilePosY < 0 || tilePosX > Main.mapHeight - 1 || tilePosY > Main.mapWidth - 1) break;
				if(Main.map[tilePosX][tilePosY] != 0) break;
				
				if(!affectedTiles.contains(tilePosXY)) {
					float distanceToTile = (float)Math.abs(Math.sqrt(((tilePosX) - x) * ((tilePosX) - x) + ((tilePosY) - y) * ((tilePosY) - y)));
					
					Main.brightness[tilePosX][tilePosY] += (1f - ((float)distanceToTile / lightRadius));
					for(int y = -2; y <= 2; y++) {
						for(int x = -2; x <= 2; x++) {
							if(x == 0 && y == 0) continue;
							if(tilePosX + x < 0 || tilePosY + y < 0 || tilePosX + x > Main.mapHeight - 1 || tilePosY + y > Main.mapWidth - 1) continue;
							float d = (float)Math.abs(Math.sqrt(x * x + y * y)) * 2;
							if(Main.brightness[tilePosX + x][tilePosY + y] < Main.brightness[tilePosX][tilePosY] / d) Main.brightness[tilePosX + x][tilePosY + y] = Main.brightness[tilePosX][tilePosY] / d;
						}
					}
					
					affectedTiles.add(tilePosXY);
				}
			}
		}
	}
	
	
	
}
