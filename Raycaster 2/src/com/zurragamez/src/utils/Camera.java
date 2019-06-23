package com.zurragamez.src.utils;

import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.Main;
import com.zurragamez.src.World;
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
	
	private World world;
		
	public Camera(World world) {
		this.world = world;
		
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
		int colorHex = 0;
		int[] texture = new int[World.textureSize*World.textureSize];
		
		float warmR = 1f;
		float warmG = 0.4f;
		float warmB = 0.0f;
		
		for(int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			
			float fog = clamp(wall.height * 0.5f / (float)Main.HEIGHT, 0, 1);
			fog *= (1 + World.brightness[(int) wall.hitY][(int) wall.hitX] * 6);
			fog = clamp(fog, 0, 1);
			
			float lightR = 1 + World.brightness[(int) wall.hitY][(int) wall.hitX] * warmR;
			float lightG = 1 + World.brightness[(int) wall.hitY][(int) wall.hitX] * warmG;
			float lightB = 1 + World.brightness[(int) wall.hitY][(int) wall.hitX] * warmB;
			
			texture = getTextureFromID(wall.id);
			
			int drawStartY = wall.y;
			if(drawStartY < 0) drawStartY = 0;
			int drawEndY = wall.y + wall.height;
			if(drawEndY > Main.HEIGHT - 1) drawEndY = Main.HEIGHT - 1;
			
			for(int y = drawStartY; y < drawEndY; y++) {
				if(wall.y + y >= Main.HEIGHT) continue;
				if(wall.x >= Main.DRAW_WIDTH) continue;
				if(y < 0 || y > Main.HEIGHT - 1) continue; 
				
				float d = ((float)(y - wall.y) / (wall.height));
				
				int texY = (int)(d * World.textureSize);
				if(wall.id == 1 || wall.id == 2) {
					int pixel = texture[wall.textureX + texY * World.textureSize];
					float r = clamp(((pixel & 0xff0000) >> 16) * lightR * fog, 0, 255);
					float g = clamp(((pixel & 0xff00) >> 8) * lightG * fog, 0, 255);
					float b = clamp(((pixel & 0xff)) * lightB * fog, 0, 255);
					colorHex = rgbToHex((int)r, (int)g, (int)b);
				}
				
				world.main.getPixels()[(wall.x) + (y) * (Main.DRAW_WIDTH)] = colorHex;
			}
			
			
			float wallDist, currentDist;
			wallDist = wall.distance;
			if(drawEndY < 0) drawEndY = Main.HEIGHT;  
			
			for(int y = drawEndY + 1; y < Main.HEIGHT; y++) {
				currentDist = Main.HEIGHT / (2f * y - Main.HEIGHT);
				float weight = (currentDist) / (wallDist);
				float currentFloorX = weight * wall.floorXWall + (1f - weight) * wall.dx;
				float currentFloorY = weight * wall.floorYWall + (1f - weight) * wall.dy;
				
				int floorTexX, floorTexY;
				floorTexX = (int)(currentFloorX * World.textureSize) % World.textureSize;
				floorTexY = (int)(currentFloorY * World.textureSize) % World.textureSize;
				
				int floor_id = World.floor[(int)(currentFloorX)][(int)(currentFloorY)];
				int roof_id = World.roof[(int)(currentFloorX)][(int)(currentFloorY)];
				
				float fade = ((float)y - Main.HEIGHT * 0.5f) / (Main.HEIGHT * 0.5f);
				fade *= (1 + World.brightness[(int)(currentFloorX)][(int)(currentFloorY)] * 3);
				fade = clamp(fade, 0, 1);
				
				lightR = 1 + World.brightness[(int)(currentFloorX)][(int)(currentFloorY)] * warmR;
				lightG = 1 + World.brightness[(int)(currentFloorX)][(int)(currentFloorY)] * warmG;
				lightB = 1 + World.brightness[(int)(currentFloorX)][(int)(currentFloorY)] * warmB;
				
				//Floor
				int pixel = getTextureFromID(floor_id)[floorTexX + floorTexY * World.textureSize];
				float r = clamp(((pixel & 0xff0000) >> 16) * lightR * fade, 0, 255);
				float g = clamp(((pixel & 0xff00) >> 8) * lightG * fade, 0, 255);
				float b = clamp(((pixel & 0xff)) * lightB * fade, 0, 255);
				colorHex = rgbToHex((int)r, (int)g, (int)b);
				world.main.getPixels()[wall.x + y * Main.DRAW_WIDTH] = colorHex;
				
				//Roof
				pixel = getTextureFromID(roof_id)[floorTexX + floorTexY * World.textureSize];
				r = clamp(((pixel & 0xff0000) >> 16) * lightR * fade, 0, 255);
				g = clamp(((pixel & 0xff00) >> 8) * lightG * fade, 0, 255);
				b = clamp(((pixel & 0xff)) * lightB * fade, 0, 255);
				colorHex = rgbToHex((int)r, (int)g, (int)b);
				world.main.getPixels()[wall.x + (Main.HEIGHT - y) * Main.DRAW_WIDTH] = colorHex;
			}
		}
	}
	
	public void drawEntity(EntitySprite e) {
		float warmR = 1f;
		float warmG = 0.4f;
		float warmB = 0.0f;
		
		float spriteX = e.getX() - y;
		float spriteY = e.getY() - x;
		float invDet = (float)(1.0 / (planeX * dirX - dirY * planeY));
		float transformX = (float)(invDet * (dirX * spriteX - dirY * spriteY));
		float transformY = (float)(invDet * (-planeY * spriteX + planeX * spriteY));
		int hoverHeight = (int)((e.isOnGround() ? (e.getSpriteSize() * (1f - e.getScale()) / transformY) : ((e.getHoverHeight() / transformY))) * (Main.HEIGHT / (e.getSpriteSize() * 2f)));
	
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
		
		float lightR = 1 + World.brightness[(int) e.getX()][(int) e.getY()] * warmR;
		float lightG = 1 + World.brightness[(int) e.getX()][(int) e.getY()] * warmG;
		float lightB = 1 + World.brightness[(int) e.getX()][(int) e.getY()] * warmB;
		
		Sprite sprite = e.getSprite(e.getRelativeDirection());
		
		for(int stripe = drawStartX; stripe < drawEndX; stripe++) {
			if(spriteScreenX > 0 && spriteScreenX < Main.DRAW_WIDTH) {
				int texX = (int)((stripe - (spriteScreenX - spriteWidth / 2)) / (float)spriteWidth * e.getSpriteSize());
				if(transformY > 0 && stripe > 0 && stripe < Main.DRAW_WIDTH && transformY < zBuffer[stripe]) {
					for(int y = drawStartY; y < drawEndY; y++) {
						float d = ((y - hoverHeight) - (Main.HEIGHT / 2 - spriteHeight / 2)) / (float)spriteHeight;
						int texY = (int)(d * e.getSpriteSize());
						
						int pixel = sprite.pixels[texX + texY * e.getSpriteSize()];
						int bufferedPixel = world.main.getPixels()[stripe + y * Main.DRAW_WIDTH];
						if(pixel != 0xffff00ff) {
							float alpha = (sprite.pixels[texX + texY * e.getSpriteSize()] >> 24 & 0xff) / 255f;
							
							//				Entity Color 														Alpha
							float r = clamp(((pixel & 0xff0000) >> 16) * alpha * darkness * lightR		+		((bufferedPixel & 0xff0000) >> 16) * (1f -  alpha), 0, 255);
							float g = clamp(((pixel & 0xff00) >> 8) * alpha * darkness * lightG			+		((bufferedPixel & 0xff00) >> 8) * (1f -  alpha), 0, 255);
							float b = clamp(((pixel & 0xff)) * alpha * darkness * lightB				+		(bufferedPixel & 0xff) * (1f -  alpha), 0, 255);
							int colorHex = rgbToHex((int)r, (int)g, (int)b);
							world.main.getPixels()[stripe + y * Main.DRAW_WIDTH] = colorHex;
						}
					}
				}
			}
		}
	}
	
	private int rgbToHex(int r, int g, int b) {
		int colorHex = r;
		colorHex = (colorHex << 8) + g;
		colorHex = (colorHex << 8) + b;
		return colorHex;
	}
	
	private int[] getTextureFromID(int id) {
		if(id == ID_STONE) return Sprite.wall_stone.pixels;
		else if(id == ID_WOOD) return Sprite.wall_wood.pixels;
		else return Sprite.wall_stone.pixels;
	}
	
}
