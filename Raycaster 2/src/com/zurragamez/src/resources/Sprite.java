package com.zurragamez.src.resources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	
	public BufferedImage image;
	public int width, height;
	public String path;
	public int[] pixels;
	
	public static Sprite jeb = new Sprite(32, 32, true, "/test.png");
	public static Sprite zombie = new Sprite(256, 256, true, "/zombie_fix.png");
	public static Sprite zombie_2 = new Sprite(256, 256, true, "/entities/zombies/zombie2.png");
	public static Sprite ammo = new Sprite(128, 128, true, "/ammo.png");
	
	public static Sprite wall_stone = new Sprite(64, 64, true, "/walls/stone.png");
	public static Sprite wall_wood = new Sprite(64, 64, true, "/walls/planks01.png");
	
	public static Sprite particle_blood = new Sprite(32, 32, true, "/particles/lol.png");
	
	//Objects
	public static Sprite object_light = new Sprite(64, 64, true, "/entities/objects/light01.png");
	
	public Sprite(int width, int height, boolean getPixels, String path) {
		this.width = width;
		this.height = height;
		this.path = path;
		pixels = new int[width * height];
		load(getPixels);
	}
	
	private Sprite(int width, int height, int[] pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	public void load(boolean getPixels) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
			
			if(getPixels) {
				image.getRGB(0, 0, width, height, pixels, 0, width);
			}
		} catch(IOException e) {
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
		return new Sprite(customWidth, customHeight, pixels);
	}
	
	public static Sprite createSprite(int width, int height, Color color) {
		int[] pixels = new int[width * height];
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = color.getRGB();
		}
		
		return new Sprite(width, height, pixels);
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
	 	boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	 	WritableRaster raster = bi.copyData(null);
	 	return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
}
