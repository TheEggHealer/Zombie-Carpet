package com.zurragamez.src;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.Zombie;
import com.zurragamez.src.entities.objects.lights.LightStandingTorch;
import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.resources.audio.Source;
import com.zurragamez.src.utils.Camera;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private  JFrame frame;
	public static final int WIDTH = (int)(1920 / 1.5f);
	public static final int HEIGHT = (int)(1080 / 1.5f);
	private static final String NAME = "Procedural Biome Generation";
	private static final Dimension dim = new Dimension(WIDTH, HEIGHT);
	private static boolean running = false;
	public static boolean useMouseMovement = true;
	
	private Thread thread;
	private Random random = new Random();
	
	private Player player;
	private Camera camera;
	
	private List<EntitySprite> entities = new ArrayList<>();
	
	public static int textureSize = 64;
	public static int mapWidth = 40, mapHeight = 20;
	
	public static int[][] map;
	public static int[][] floor, roof;
	public static float[][] brightness;
	
	private SpriteComparator comparator;
	
	public static int res = 5; // 5
	public static final int DRAW_WIDTH = Main.WIDTH / res;
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
		
		try {
			map = loadMap("maps/sus_walls.txt");
			floor = loadMap("maps/sus_floor.txt");
			roof = loadMap("maps/sus_roof.txt");
			brightness = loadBrightness();
			loadEntities("maps/sus_entities.txt");
			
		} catch (IOException e) {
			System.err.println("Failed to load map");
			e.printStackTrace();
		}
		
		this.camera = new Camera(this);
		this.player = new Player(this, camera, 1.5f, 1.5f);
		
//		for(int i = 0; i < 20; i++) {
//			boolean hasSpawn = false;
//			int x=0, y=0;
//			while(!hasSpawn) {
//				x = random.nextInt(mapWidth);
//				y = random.nextInt(mapHeight);
//				if(map[y][x] == 0) hasSpawn = true;
//			} 
//			addEntity(new Zombie((x + 0.5f), (y + 0.5f)));
//		}
		
//		addEntity(new LightStandingTorch(3.5f, 3.5f));
		addEntity(new LightStandingTorch(5.5f, 3.5f));
		
		comparator = new SpriteComparator();
		comparator.player = player;
	}
	
	public synchronized void start() {
		if(running) return;
		
		running = true;
		thread = new Thread(this, "Main Thread");
		thread.start();
	}
	
	public void exit() {
		for(EntitySprite e : entities) {
			e.remove();
		}
		AudioMaster.destroy();
		System.exit(0);
	}
	
	/**
	 * Creates a 2D array[float] from a .txt file. The array will contain the brightness values of each tile.
	 * @param file File name (Do not include "res/"!)
 	 * @return 2D array[float] of brightness values.
	 * @throws IOException In case the file does not exist.
	 */
	public float[][] loadBrightness() throws IOException {
		float[][] map = new float[mapWidth][mapHeight];
		int width, height;
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("res/maps/sus_brightness.txt"));
		String line = br.readLine();
		int index = 0;
		
	    while (line != null) {
	    	if(index > 0) {
	        String[] cells = line.split(",");
		        for(int i = 0; i < cells.length; i++) {
		        	map[i][index - 1] = Float.parseFloat(cells[i]);
		        }
	    	} else {
	    		String[] size = line.split(",");
	    		width = Integer.parseInt(size[0]);
	    		height = Integer.parseInt(size[1]);
	    		map = new float[height][width];
	    	}
	    	index++;
	        line = br.readLine();
	    }
		
		return map;
	}
	
	/**
	 * Creates a 2D array[int] from a .txt file. The array will contain the ids of each tile.
	 * @param file File name (Do not include "res/"!)
 	 * @return 2D array[int] of ids.
	 * @throws IOException In case the file does not exist.
	 */
	public int[][] loadMap(String file) throws IOException {
		int[][] map = new int[mapWidth][mapHeight];
		int width, height;
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("res/" + file));
		String line = br.readLine();
		int index = 0;
		
		while (line != null) {
	    	if(index > 0) {
	        String[] cells = line.split(",");
		        for(int i = 0; i < cells.length; i++) {
		        	System.out.println(i + ", " + (index - 1));
		        	map[i][index - 1] = Integer.parseInt(cells[i]);
		        }
	    	} else {
	    		String[] size = line.split(",");
	    		width = Integer.parseInt(size[0]);
	    		height = Integer.parseInt(size[1]);
	    		System.out.println(width + ", " + height);
	    		map = new int[height][width];
	    	}
	    	index++;
	        line = br.readLine();
	    }
		
		return map;
	}
	
	public void loadEntities(String file) throws IOException {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("res/" + file));
		String line = br.readLine();
		
		while (line != null) {
	    	String data[] = line.split(",");
	    	int id = Integer.parseInt(data[0]);
	    	float x = Float.parseFloat(data[1]);
	    	float y = Float.parseFloat(data[2]);
	    	
	    	if(id == 1) {
	    		addEntity(new Zombie(y, x));
	    	} else if(id == 2) {
//	    		addEntity(new LightStandingTorch(y, x)); 
	    	}
	        line = br.readLine();
	    }
	}
	
	public void update() {
 		for(int y = 0; y < mapWidth; y++) {
 			for(int x = 0; x < mapHeight; x++) {
 				brightness[x][y] = 0;
 			}
 		}
		
		if(Keyboard.esc) {
			exit();
		}
		
		player.update();
		camera.update();
		
		for(int i = 0; i < entities.size(); i++) {
			EntitySprite e = entities.get(i);
			e.update();
			if(e.isRemoved()) entities.remove(i);
		}
		
		entities.sort(comparator);
		Mouse.dx = 0;
		AudioMaster.setListenerData(player);
	}
	
	/**
	 * Adds the EntitySprite to the list containing all entities. This method also initializes the entity.
	 * @param e EntitySprite
	 */
	public void addEntity(EntitySprite e) {
		e.init(this);
		entities.add(e);
	}
	
	/**
	 * Returns the player
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Returns a list containing all of the entities in the game, in the form of EntitySprite.
	 * @return List of EntitySprites
	 */
	public List<EntitySprite> getEntities() {
		return entities;
	}
	
	public void draw(Graphics2D g) {
		//Clear
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
		
		//Draw
//		player.draw();
//		for(EntitySprite e : entities) {
//			player.drawEntity(e);
//		}
		camera.drawWalls();
		for(EntitySprite e : entities) {
			camera.drawEntity(e);
		}
		
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
	
	public static void main(String[] agrs) {
		Main main = new Main();
		main.start();
		
	}
	
	public class SpriteComparator implements Comparator<EntitySprite> {
		
		private Player player;
		
		public int compare(EntitySprite o1, EntitySprite o2) {
			float distanceA = (float)Math.abs(Math.sqrt((o1.getX() - player.x) * (o1.getX() - player.x) + (o1.getY() - player.y) * (o1.getY() - player.y)));
			float distanceB = (float)Math.abs(Math.sqrt((o2.getX() - player.x) * (o2.getX() - player.x) + (o2.getY() - player.y) * (o2.getY() - player.y)));
			return distanceA < distanceB ? 1 : -1;
		}
		
	}
	
}
