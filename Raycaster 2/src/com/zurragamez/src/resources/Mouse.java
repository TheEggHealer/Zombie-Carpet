package com.zurragamez.src.resources;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import com.zurragamez.src.Main;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static int x, y, button;
	public static boolean pressing;
	public static int wheel;
	public static int dx;
	public static float finalRotation;
	
	private Robot mouse;
	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	private static final int MOUSE_SMOOTHING = 5;
	private static List<Integer> pastMovement = new ArrayList<Integer>();
	
	private int monitor = 0;
	
	public Mouse() {
		try {
			mouse = new Robot();
			mouse.mouseMove(screen.width / 2, screen.height / 2);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void update() {
		pastMovement.add(0);
		if(pastMovement.size() > 0) {
			int total = 0;
			for(int i = 0; i < pastMovement.size() - 3; i++) {
				if(pastMovement.get(i) != null) total += pastMovement.get(i);
			}
			finalRotation = (float)total / pastMovement.size();
			if(pastMovement.size() > MOUSE_SMOOTHING) {
				for(int i = 0; i < pastMovement.size() - MOUSE_SMOOTHING; i++) {
					pastMovement.remove(i);
				}
			}
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		x = e.getXOnScreen();
		y = e.getY();
		if(x != screen.width / 2) {
			dx = e.getXOnScreen() - screen.width / 2;
			pastMovement.add(dx);
			if(Main.useMouseMovement) mouse.mouseMove(screen.width / 2, screen.height / 2);
		}
	}

	public void mouseMoved(MouseEvent e) {
		if(e.getXOnScreen() < 0) monitor = -1;
		else if(e.getXOnScreen() > screen.width) monitor = 1; 
		else monitor = 0;
		
		x = e.getXOnScreen();
		y = e.getY();
		if(x != screen.width / 2) {
			if(monitor == 0) dx = e.getXOnScreen() - screen.width / 2;
			else if(monitor == 1) dx = (e.getXOnScreen() - screen.width) - screen.width / 2;
			else dx = (e.getXOnScreen() + screen.width) - screen.width / 2;
			
			pastMovement.add(dx);
			
			if(Main.useMouseMovement) {
				if(monitor == 0) mouse.mouseMove(screen.width / 2, screen.height / 2);
				else if(monitor == 1) mouse.mouseMove(screen.width + screen.width / 2, screen.height / 2);
				else mouse.mouseMove(-screen.width / 2, screen.height / 2);
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseEntered(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseExited(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		button = e.getButton();
		pressing = true;
	}

	public void mouseReleased(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		button = -1;
		pressing = false;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if(e.getWheelRotation() == 1) {
			wheel = 1;
		}
		else if(e.getWheelRotation() == -1) {
			wheel = -1;
		}
	}

}
