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

import com.zurragamez.src.Main;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static int x, y, button;
	public static boolean pressing;
	public static int wheel;
	public static int dx;
	
	private Robot mouse;
	private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	
	public Mouse() {
		try {
			mouse = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		x = e.getXOnScreen();
		y = e.getY();
		if(x != screen.width / 2) {
			dx = e.getXOnScreen() - screen.width / 2;
			if(Main.useMouseMovement) mouse.mouseMove(screen.width / 2, screen.height / 2);
		}
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getXOnScreen();
		y = e.getY();
		if(x != screen.width / 2) {
			dx = e.getXOnScreen() - screen.width / 2;
			if(Main.useMouseMovement) mouse.mouseMove(screen.width / 2, screen.height / 2);
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
