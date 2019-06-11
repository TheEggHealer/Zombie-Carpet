package com.zurragamez.src.main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Button {

	private int x, y;
	private int width, height;
	
	private boolean hover;
	private String text;
	public boolean click;
	
	public Button(int x, int y, int width, int height, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.text = text;
	}
	
	public void update() {
		if(Mouse.x >= x && Mouse.x <= x + width && Mouse.y >= y && Mouse.y <= y + height) {
			hover = true;
		} else {
			hover = false;
			click = false;
		}
		
		if(hover) {
			if(Mouse.button == 1) { 
				click = true;
			} else {
				click = false;
			}
		}
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void draw(Graphics2D g) {
		if(!hover) g.setColor(Color.white);
		else {
			if(!click) g.setColor(Color.gray);
			else g.setColor(new Color(100, 200, 100));
		}
		
		int textWidth = g.getFontMetrics().stringWidth(text);
		g.drawRect(x, y, width, height);
		g.drawString(text, x + width / 2 - textWidth / 2, y + height / 2 + 5);			
	}
}
