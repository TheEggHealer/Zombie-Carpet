package com.zurragamez.src.main;

import java.awt.image.BufferedImage;

public class Entity {

	public BufferedImage img;
	public float x, y;
	public int id;
	public boolean remove = false;
	public boolean hover = false;
	
	public Entity(float x, float y, int id, BufferedImage img) {
		this.x = x; 
		this.y = y;
		this.id = id;
		this.img = img;
	}
	
	public void update() {
		if(Mouse.x + Main.x >= x - Main.GRIDSIZE / 2 && Mouse.x + Main.x <= x + Main.GRIDSIZE - Main.GRIDSIZE / 2 && Mouse.y + Main.y >= y - Main.GRIDSIZE && Mouse.y + Main.y <= y - Main.GRIDSIZE + Main.GRIDSIZE) {
			hover = true;
			if(Mouse.button == 3) {
				Mouse.button = 0;
				remove = true;
			}
		} else {
			hover = false;
		}
	}
	
}
