package com.zurragamez.src.main;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

	public static int x, y, button;
	public static boolean pressing;
	public static int wheel;
	public static int dx;
	
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();

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
