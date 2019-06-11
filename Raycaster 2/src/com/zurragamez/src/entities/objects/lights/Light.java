package com.zurragamez.src.entities.objects.lights;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.Main;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;

public class Light extends EntitySprite {

	protected int lightRadius;
	protected int rays = 200;
	protected int blurRadius = 3;
	protected float brightness = 1;

	protected List<Integer> affectedTiles = new ArrayList<Integer>();
	
	public Light(float x, float y, float scale, boolean onGround, int lightRadius, Sprite sprite) {
		super(x, y, scale, onGround, sprite);
		this.lightRadius = lightRadius;
		
		setupLight();
	}
	
	public void setupLight() {
		affectedTiles.clear();
		float dir = 0;
		for(int i = 0; i < rays; i++) {
			dir = (float)((Math.PI * 2) / rays) * i; 
			float dx = (float)Math.cos(dir) * 0.9f;
			float dy = (float)Math.sin(dir) * 0.9f;
			for(int dist = 0; dist < lightRadius; dist++) {
				int tilePosX = (int)(x + dx * dist);
				int tilePosY = (int)(y + dy * dist);
				int tilePosXY = tilePosX + tilePosY * Main.mapHeight;
				
				if(tilePosX < 0 || tilePosY < 0 || tilePosX > Main.mapWidth - 1 || tilePosY > Main.mapHeight - 1) break;
				if(Main.map[tilePosX][tilePosY] != 0) break;
				
				if(!affectedTiles.contains(tilePosXY)) {
					float distanceToTile = (float)Math.abs(Math.sqrt(((tilePosX) - x) * ((tilePosX) - x) + ((tilePosY) - y) * ((tilePosY) - y)));
					
					Main.brightness[tilePosX][tilePosY] += (1f - ((float)distanceToTile / lightRadius)) * brightness;
					for(int y = -blurRadius; y <= blurRadius; y++) {
						for(int x = -blurRadius; x <= blurRadius; x++) {
							if(x == 0 && y == 0) continue;
							if(tilePosX + x < 0 || tilePosY + y < 0 || tilePosX + x > Main.mapWidth - 1 || tilePosY + y > Main.mapHeight - 1) continue;
							float d = (float)Math.abs(Math.sqrt(x * x + y * y)) * 3;
							if(Main.brightness[tilePosX + x][tilePosY + y] < Main.brightness[tilePosX][tilePosY] / d) Main.brightness[tilePosX + x][tilePosY + y] = Main.brightness[tilePosX][tilePosY] / d;
						}
					}
					
					affectedTiles.add(tilePosXY);
				}
			}
		}
	}
	
	
	
}
