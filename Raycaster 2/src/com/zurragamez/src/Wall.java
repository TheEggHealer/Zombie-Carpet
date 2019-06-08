package com.zurragamez.src;

public class Wall {

	public int x, y;
	public int height;
	public int id;
	public int textureX;
	public float distance;
	public float floorXWall, floorYWall;
	public float dx, dy;
	
	public Wall(int x, int y, int height, int id, float distance, int textureX, float floorXWall, float floorYWall, float dx, float dy) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.id = id;
		this.textureX = textureX;
		this.distance = distance;
		this.floorXWall = floorXWall;
		this.floorYWall = floorYWall;
		this.dx = dx;
		this.dy = dy;
	}
	
}
