package com.zurragamez.src.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.Main;
import com.zurragamez.src.Ray;
import com.zurragamez.src.Wall;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.resources.Sprite;

public class Camera {

	private final int ID_STONE = 1;
	private final int ID_WOOD = 2;
	
	public float y, x;
	private double dirY, dirX;
	double planeX = 0.0, planeY = -0.9;
	
	private List<Ray> rays = new ArrayList<>();
	private List<Wall> walls = new ArrayList<>();
	private float[] zBuffer = new float[Main.DRAW_WIDTH];
	
	private Main main;
	
	public Camera(Main main) {
		this.main = main;
		
		this.dirX = 0;
		this.dirY = 1;
	}
	
	public void update() {
		rayCasting();
	}
	
	public void rayCasting() {
		rays.clear();
		walls.clear();
		
		for(int x = 0; x < Main.DRAW_WIDTH; x++) {
			double cameraX = 2 * x / (float)Main.DRAW_WIDTH - 1.;
			double rayDirX = dirY + planeX * cameraX;
			double rayDirY = dirX + planeY * cameraX;
			Ray r = new Ray(this.y, this.x, rayDirX, rayDirY);
			Wall wall = r.cast((int)x);
			rays.add(r);
			walls.add(wall);
			
			zBuffer[x] = (float)wall.distance;
		}
	}
	
	public void rotate(float radians) {
		double oldDirX = dirY;
		dirY = dirY * Math.cos(radians) - dirX * Math.sin(radians);
		dirX = oldDirX * Math.sin(radians) + dirX * Math.cos(radians);
		double oldPlaneX = planeX;
		planeX = planeX * Math.cos(radians) - planeY * Math.sin(radians);
		planeY = oldPlaneX * Math.sin(radians) + planeY * Math.cos(radians);
	}
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
	
	public void drawWalls() {
		Color color = Color.white;
		int[] texture = new int[Main.textureSize*Main.textureSize];
		
		for(int i = 0; i < walls.size(); i++) {
			Wall r = walls.get(i);
			
			float alpha = clamp(r.height / (float)Main.HEIGHT, 0, 1);
			
			texture = getTextureFromID(r.id);
			
			int drawStartY = r.y;
			if(drawStartY < 0) drawStartY = 0;
			int drawEndY = r.y + r.height;
			if(drawEndY > Main.HEIGHT - 1) drawEndY = Main.HEIGHT - 1;
			
			for(int y = drawStartY; y < drawEndY; y++) {
				if(r.y + y >= Main.HEIGHT) continue;
				if(r.x >= Main.DRAW_WIDTH) continue;
				if(y < 0 || y > Main.HEIGHT - 1) continue; 
				
				float d = ((float)(y - r.y) / (r.height));
				
				int texY = (int)(d * Main.textureSize);
				if(r.id == 1 || r.id == 2) {
					color = new Color(texture[r.textureX + texY * Main.textureSize]);
					color = new Color((int)(color.getRed() * alpha), (int)(color.getGreen() * alpha), (int)(color.getBlue() * alpha));
				}
				
				main.getPixels()[(r.x) + (y) * (Main.DRAW_WIDTH)] = color.getRGB();
			}
			
			
			float wallDist, playerDist, currentDist;
			wallDist = r.distance;
			playerDist = 0;
			
			if(drawEndY < 0) drawEndY = Main.HEIGHT;  
			//Floor and ceiling
			for(int y = drawEndY + 1; y < Main.HEIGHT; y++) {
				currentDist = Main.HEIGHT / (2f * y - Main.HEIGHT);
				float weight = (currentDist - playerDist) / (wallDist - playerDist);
				float currentFloorX = weight * r.floorXWall + (1f - weight) * r.dx;
				float currentFloorY = weight * r.floorYWall + (1f - weight) * r.dy;
				
				int floorTexX, floorTexY;
				floorTexX = (int)(currentFloorX * Main.textureSize) % Main.textureSize;
				floorTexY = (int)(currentFloorY * Main.textureSize) % Main.textureSize;
				
				int floor_id = Main.floor[(int)(currentFloorX)][(int)(currentFloorY)];
				int roof_id = Main.roof[(int)(currentFloorX)][(int)(currentFloorY)];
				float fade = ((float)y - Main.HEIGHT * 0.5f) / (Main.HEIGHT * 0.5f);
				fade *= Main.brightness[(int)(currentFloorX)][(int)(currentFloorY)] == 0 ? 1 : Main.brightness[(int)(currentFloorX)][(int)(currentFloorY)];
				if(fade > 1) fade = 1;
				
				//Draw floor
				color = new Color(getTextureFromID(floor_id)[floorTexX + floorTexY * Main.textureSize]);
				color = new Color((int)(color.getRed() * fade), (int)(color.getGreen() * fade), (int)(color.getBlue() * fade));
				main.getPixels()[r.x + y * Main.DRAW_WIDTH] = color.getRGB();
				
				//Draw roof
				color = new Color(getTextureFromID(roof_id)[floorTexX + floorTexY * Main.textureSize]);
				color = new Color((int)(color.getRed() * fade), (int)(color.getGreen() * fade), (int)(color.getBlue() * fade));
				main.getPixels()[r.x + (Main.HEIGHT - y) * Main.DRAW_WIDTH] = color.getRGB();
			}
		}
	}
	
	public void drawEntity(EntitySprite e) {
		float spriteX = e.getX() - y;
		float spriteY = e.getY() - x;
		float invDet = (float) (1.0 / (planeX * dirX - dirY * planeY));
		float transformX = (float)(invDet * (dirX * spriteX - dirY * spriteY));
		float transformY = (float)(invDet * (-planeY * spriteX + planeX * spriteY));
		int hoverHeight = (int)((e.isOnGround() ? (e.getSprite().height * (1f - e.getScale()) / transformY) : ((e.getHoverHeight() / transformY))) * (Main.HEIGHT / (e.getSprite().height * 2f)));
	
		int spriteScreenX = (int)((Main.DRAW_WIDTH / 2) * (1 + transformX / transformY));
		
		int spriteHeight = (int)((Main.HEIGHT / transformY) * e.getScale());
		int drawStartY = (int) (Main.HEIGHT / 2 - spriteHeight / 2 + hoverHeight);
		if(drawStartY < 0) drawStartY = 0;
		int drawEndY = (int) (Main.HEIGHT / 2 + spriteHeight / 2 + hoverHeight);
		if(drawEndY >= Main.HEIGHT) drawEndY = Main.HEIGHT - 1;
		
		int spriteWidth = (int)((Main.HEIGHT / transformY) * e.getScale()) / 5;
		int drawStartX = spriteScreenX - spriteWidth / 2;
		if(drawStartX < 0) drawStartX = 0;
		int drawEndX = spriteScreenX + spriteWidth / 2;
		if(drawEndX >= Main.DRAW_WIDTH) drawEndX = Main.DRAW_WIDTH - 1;
		
		float darkness = 2f / transformY;
		if(darkness < 0) darkness = 0;
		else if(darkness > 1) darkness = 1;
		if(e.disableFog) darkness = 1;
		
		for(int stripe = drawStartX; stripe < drawEndX; stripe++) {
			if(spriteScreenX > 0 && spriteScreenX < Main.DRAW_WIDTH) {
				int texX = (int)((stripe - (spriteScreenX - spriteWidth / 2)) / (float)spriteWidth * e.getSprite().width);
				if(transformY > 0 && stripe > 0 && stripe < Main.DRAW_WIDTH && transformY < zBuffer[stripe]) {
					for(int y = drawStartY; y < drawEndY; y++) {
						float d = ((y - hoverHeight) - (Main.HEIGHT / 2 - spriteHeight / 2)) / (float)spriteHeight;
						int texY = (int)(d * e.getSprite().height);
						
						Color c = new Color(e.getSprite().pixels[texX + texY * e.getSprite().width]);
						if(c.getRGB() != 0xffff00ff) main.getPixels()[stripe + y * Main.DRAW_WIDTH] = new Color((int)(c.getRed() * darkness), (int)(c.getGreen() * darkness), (int)(c.getBlue() * darkness)).getRGB();
					}
				}
			}
		}
	}
	
	private int[] getTextureFromID(int id) {
		if(id == ID_STONE) return Sprite.wall_stone.pixels;
		else if(id == ID_WOOD) return Sprite.wall_wood.pixels;
		else return Sprite.wall_stone.pixels;
	}
	
}
