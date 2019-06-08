package com.zurragamez.src;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class Ray {
	
	public float x, y;
	public double cX, cY;
	
	private double dirX, dirY;
	
	private Point target;
	
	public Ray(float x, float y, double dirX, double dirY) {
		this.x = x;
		this.y = y;
		this.dirX = dirX;
		this.dirY = dirY;
	}
	
	public Wall cast(int scanX) {
		int cellX = (int)(x);
		int cellY = (int)(y);
		double dx = (x);
		double dy = (y);
		double sideDistX, sideDistY;
		double deltaDistX = Math.abs(1 / dirX);
		double deltaDistY = Math.abs(1 / dirY);
		int stepX, stepY;
		int hit = 0;
		int side = 0;
		double perpWallDist;
		
		if(dirX < 0) {
			stepX = -1;
			sideDistX = (dx - cellX) * deltaDistX;
		} else {
			stepX = 1;
			sideDistX = (cellX + 1 - dx) * deltaDistX;
		}
		if(dirY < 0) {
			stepY = -1;
			sideDistY = (dy - cellY) * deltaDistY;
		} else {
			stepY = 1;
			sideDistY = (cellY + 1 - dy) * deltaDistY;
		}
		
		while(hit == 0) {
			if(sideDistX < sideDistY) {
				sideDistX += deltaDistX;
				cellX += stepX;
				side = 0;
			}
			else {
				sideDistY += deltaDistY;
				cellY += stepY;
				side = 1;
			}
			
			if(Main.map[cellY][cellX] > 0) hit = 1;
		}
		
		target = new Point(cellX, cellY);
		
		if(side == 0) perpWallDist = (cellX - dx + (1 - stepX) / 2) / dirX;
		else perpWallDist = (cellY - dy + (1 - stepY) / 2) / dirY;
		int height = (int)(Main.HEIGHT / perpWallDist);
		
		double wallX;
		if(side == 0) wallX = dy + perpWallDist * dirY;
		else wallX = dx + perpWallDist * dirX;
		wallX -= Math.floor(wallX);
		
		//64 is size of texture!
		int texX = (int)(wallX * 64.);
		if(side == 0 && dirX > 0) texX = 64 - texX - 1;
		if(side == 1 && dirY < 0) texX = 64 - texX - 1;
		
		float floorXWall, floorYWall;
		
		if(side == 0 && dirX > 0) {
			floorXWall = cellX;
			floorYWall = (float)(cellY + wallX);
		}
		else if(side == 0 && dirX < 0) {
			floorXWall = cellX + 1;
			floorYWall = (float)(cellY + wallX);
		}
		else if(side == 1 && dirY > 0) {
			floorXWall = (float)(cellX + wallX);
			floorYWall = cellY;
		}
		else {
			floorXWall = (float)(cellX + wallX);
			floorYWall = cellY + 1;
		}
		
		return new Wall(scanX, Main.HEIGHT / 2 - height / 2, height, Main.map[cellY][cellX], (float)perpWallDist, texX, floorXWall, floorYWall, (float)dx, (float)dy);
	}
	
	public void draw(Graphics2D g) {
		g.drawLine((int)x, (int)y, (int)(x + dirX), (int)(y + dirY));
		
		g.setColor(Color.green);
		if(target != null) {
			g.fillOval(target.x - 5, target.y - 5, 10, 10);
		}
	}
	
}
