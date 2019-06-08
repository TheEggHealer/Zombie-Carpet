package com.zurragamez.src.resources;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Keyboard extends KeyAdapter {

	public static boolean up, down, left, right, _left, _right;
	public static boolean shoot;
	public static boolean esc;
	
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) up = true;
		if(code == KeyEvent.VK_S) down = true;
		if(code == KeyEvent.VK_A) left = true;
		if(code == KeyEvent.VK_D) right = true;
		if(code == KeyEvent.VK_LEFT) _left = true;
		if(code == KeyEvent.VK_RIGHT) _right = true;
		
		if(code == KeyEvent.VK_SPACE) shoot = true;
		
		if(code == KeyEvent.VK_ESCAPE) esc = true;
	}

	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) up = false;
		if(code == KeyEvent.VK_S) down = false;
		if(code == KeyEvent.VK_A) left = false;
		if(code == KeyEvent.VK_D) right = false;
		if(code == KeyEvent.VK_LEFT) _left = false;
		if(code == KeyEvent.VK_RIGHT) _right = false;
		
		if(code == KeyEvent.VK_SPACE) shoot = false;
		
		if(code == KeyEvent.VK_ESCAPE) esc = false;
	}
	
}
