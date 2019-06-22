package com.zurragamez.src.resources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.zurragamez.src.Main;

public class Sprite {
	private static final String TAG = "Sprite";
	
//	private static Random random = new Random();
	
	public BufferedImage image;
	public int width, height;
	public String path;
	public int[] pixels;
	
	public static Sprite jeb = new Sprite(32, 32, false, "/test.png");
	public static Sprite zombie = new Sprite(256, 256, false, "/zombie_fix.png");
	public static Sprite zombie_2 = new Sprite(256, 256, false, "/entities/zombies/zombie2.png");
	public static Sprite zombie_2dead = new Sprite(256, 256, false, "/entities/zombies/zombie3.png");
	public static Sprite ammo = new Sprite(128, 128, false, "/ammo.png");
	
	public static Sprite wall_stone = new Sprite(64, 64, false, "/walls/stone.png");
	public static Sprite wall_wood = new Sprite(64, 64, false, "/walls/planks01.png");
	
	//Monsters
	public static Sprite[] zombie_sheet = loadSpriteSheet(32, 32, 160, 32, "/entities/zombies/zombies (32x32).png");
	
	//Objects
	public static Sprite object_light = new Sprite(64, 64, false, "/entities/objects/light01.png");
	
	//Particles
	public static Sprite particle_blood = new Sprite(32, 32, false, "/particles/blood.png");
	public static Sprite particle_gore = new Sprite(8, 8, false, "/particles/gore.png");
	
	public Sprite(int width, int height, boolean flip, String path) {
		this.width = width;
		this.height = height;
		this.path = path;
		pixels = new int[width * height];
		load(flip);
	}
	
	private Sprite(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	public void load(boolean flip) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			
			image.getRGB(0, 0, width, height, pixels, 0, width);
			
			Main.printDebug(TAG, "Loaded sprite: " + path + " successfully.");
		} catch(IOException e) {
			Main.printDebugErr(TAG, "Failed to load sprite: " + path);
			e.printStackTrace();
		}
	}
	
	public Sprite subSprite(int x, int y, int width, int height) {
		int[] pixels = new int[width * height];
		for(int dy = 0; dy < height; dy++) {
			for(int dx = 0; dx < width; dx++) {
				pixels[dx + dy * width] = this.pixels[(x + dx) + (y + dy) * this.width];
			}
		}
		
		Main.printDebug(TAG, "Created a sub sprite from: " + this.path + ", at x: " + x + ", y: " + y + ", w: " + width + ", h: " + height);
		return new Sprite(width, height, pixels);
	}
	
	public Sprite subSprite(int x, int y, int width, int height, int customWidth, int customHeight) {
		int[] pixels = new int[customWidth * customHeight];
		float px = customWidth / width;
		float py = customHeight / height;
		for(int dy = 0; dy < customHeight; dy++) {
			for(int dx = 0; dx < customWidth; dx++) {
				pixels[dx + dy * customWidth] = this.pixels[(x + (int)(dx / px)) + (y + (int)(dy / py)) * this.width];
			}
		}
		
		Main.printDebug(TAG, "Created a sub sprite from: " + this.path + ", at x: " + x + ", y: " + y + ", w: " + width + ", h: " + height);
		return new Sprite(customWidth, customHeight, pixels);
	}
	
	public static Sprite createSprite(int width, int height, Color color) {
		int[] pixels = new int[width * height];
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = color.getRGB();
		}
		
		Main.printDebug(TAG, "Generated a new sprite");
		return new Sprite(width, height, pixels);
	}
	
	public static Sprite[] loadSpriteSheet(int spriteWidth, int spriteHeight, int sheetWidth, int sheetHeight, String path) {
		int w = (sheetWidth / spriteWidth);
		int h = (sheetHeight / spriteHeight);
		Sprite[] sprites = new Sprite[w * h];
		Sprite sheet = new Sprite(sheetWidth, sheetHeight, false, path);
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				sprites[x + y * w] = sheet.subSprite(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight);
			}
		}
		
		Main.printDebug(TAG, "Loaded spritesheet with " + w*h  + " sprites from: " + path);
		return sprites;
	}
	
	public Sprite flip() {
		Sprite s = new Sprite(this.width, this.height, this.pixels.clone());
		
		for(int y = 0; y < this.height; y++) {
			for(int x = 0; x < this.width; x++) {
				s.pixels[(this.width - x - 1) + y * this.width] = this.pixels[x + y * this.width];
			}
		}
		
		return s;
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
	 	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	 	WritableRaster raster = bi.copyData(null);
	 	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
}
