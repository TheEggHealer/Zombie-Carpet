package com.zurragamez.src;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.audio.AudioMaster;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private  JFrame frame;
	public static final int WIDTH = (int)(1920 / 1.5f);
	public static final int HEIGHT = (int)(1080 / 1.5f);
	private static final String NAME = "Zombie Carpet";
	private static final Dimension dim = new Dimension(WIDTH, HEIGHT);
	private static boolean running = false;
	
	public static boolean useMouseMovement = true;
	public static boolean debug = true;
	
	private Thread thread;
	private World world;
	
	public static int res = 5; // 5
	public static final int DRAW_WIDTH = Main.WIDTH / res;
	public static final int DRAW_HEIGHT = Main.HEIGHT / res;
	private BufferedImage screen = new BufferedImage(DRAW_WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) screen.getRaster().getDataBuffer()).getData();
	
	public Main() {
		setPreferredSize(dim);
		frame = new JFrame(NAME);
		frame.add(this);
		frame.pack();
		frame.setSize(dim);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
		if(useMouseMovement) frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
		
		addKeyListener(new Keyboard());
		addMouseListener(new Mouse());
		addMouseMotionListener(new Mouse());
		addMouseWheelListener(new Mouse());
			
		AudioMaster.init();
		AudioMaster.loadSounds();
		
		world = new World(this, "sis");
	}
	
	public synchronized void start() {
		if(running) return;
		
		running = true;
		thread = new Thread(this, "Main Thread");
		thread.start();
	}
	
	public void exit() {
		world.exit();
	}
	
	public void update() {
		world.update();
	}
	
	public void draw(Graphics2D g) {
		//Clear
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
		
		world.draw(g);
		
		//Display screen
		g.drawImage(screen, 0, 0, WIDTH, HEIGHT, null);
	}

	/**
	 * Returns the array of screen pixel colors. The array size is set to DRAW_WIDTH * HEIGHT
	 * @return Array of pixels.
	 */
	public int[] getPixels() {
		return pixels;
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		draw((Graphics2D)g);
		bs.show();
		g.dispose();
	}

	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1.0 / (1000000000 / 60);
		double delta = 0;
		int updates = 0, frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) * ns;
			lastTime = now;
			while(delta > 1) {
				delta--;
				updates++;
				update();
			}
			render();
			frames++;
			
			if(System.currentTimeMillis() - 1000 > timer) {
				timer += 1000;
				frame.setTitle("FPS: " + frames + ", UPS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}
	
	public static void printDebug(String TAG, String message) {
		if(debug) {
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        System.out.print("[" + sdf.format(cal.getTime()) + "] ");
	        System.out.print(TAG  + ": ");
	        System.out.println(message);
		}
	}
	
	public static void printDebugErr(String TAG, String message) {
		if(debug) {
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        System.err.print("[" + sdf.format(cal.getTime()) + "] ");
	        System.err.print(TAG  + ": ");
	        System.err.println(message);
		}
	}
	
	public static void main(String[] agrs) {
		Main main = new Main();
		main.start();
		
	}
	
}
