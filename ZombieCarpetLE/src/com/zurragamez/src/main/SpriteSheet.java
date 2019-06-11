package com.zurragamez.src.main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	public BufferedImage image;
	public int width, height;
	public String path;
	
	public static SpriteSheet stone = new SpriteSheet(64, 64, "/walls/stone.png");
	public static SpriteSheet wood = new SpriteSheet(64, 64, "/walls/planks01.png");
	
	public static SpriteSheet light = new SpriteSheet(64, 64, "/entities/light01.png");
	public static SpriteSheet zombie = new SpriteSheet(64, 64, "/entities/zombie_fix.png");

	public SpriteSheet(int width, int height, String path) {
		this.width = width;
		this.height = height;
		this.path = path;
		load();
	}
	
	public void load() {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(path));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}
