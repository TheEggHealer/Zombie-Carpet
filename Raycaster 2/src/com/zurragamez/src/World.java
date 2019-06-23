package com.zurragamez.src;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.zurragamez.src.entities.EntitySound;
import com.zurragamez.src.entities.EntitySprite;
import com.zurragamez.src.entities.monsters.Zombie;
import com.zurragamez.src.entities.objects.lights.LightStandingTorch;
import com.zurragamez.src.resources.Keyboard;
import com.zurragamez.src.resources.Mouse;
import com.zurragamez.src.resources.audio.AudioMaster;
import com.zurragamez.src.utils.Camera;

public class World {

	public static int textureSize = 64;
	public static int mapHeight = 40, mapWidth = 20;
	
	public static int[][] map;
	public static int[][] floor, roof;
	public static float[][] brightness;
	
	private SpriteComparator comparator;
	
	public Main main;
	private Player player;
	private Camera camera;
	
	private List<EntitySprite> entities = new ArrayList<>();
	private List<EntitySound> entitySounds = new ArrayList<>(); 
	
	public World(Main main, String mapName) {
		this.main = main;
		
		try {
			map = loadMap("maps/" + mapName + "_walls.txt");
			floor = loadMap("maps/" + mapName + "_floor.txt");
			roof = loadMap("maps/" + mapName + "_roof.txt");
			//TODO: Do not load brightness
			brightness = loadBrightness("maps/" + mapName + "_brightness.txt");
			loadEntities("maps/" + mapName + "_entities.txt");
			
		} catch (IOException e) {
			System.err.println("Failed to load map");
			e.printStackTrace();
		}
		
		this.camera = new Camera(this);
		this.player = new Player(this, camera, 1.5f, 1.5f);
		
		comparator = new SpriteComparator();
		comparator.player = player;
		
		addEntity(new Zombie(3.5f, 3.5f));
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
	public float[][] loadBrightness(String file) throws IOException {
		float[][] map = new float[mapHeight][mapWidth];
		int width, height;
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("res/" + file));
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
		int[][] map = new int[mapHeight][mapWidth];
		int width, height;
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader("res/" + file));
		String line = br.readLine();
		int index = 0;
		
		while (line != null) {
	    	if(index > 0) {
	        String[] cells = line.split(",");
		        for(int i = 0; i < cells.length; i++) {
		        	//System.out.println(i + ", " + (index - 1));
		        	map[i][index - 1] = Integer.parseInt(cells[i]);
		        }
	    	} else {
	    		String[] size = line.split(",");
	    		width = Integer.parseInt(size[0]);
	    		height = Integer.parseInt(size[1]);
	    		//System.out.println(width + ", " + height);
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
	    		addEntity(new LightStandingTorch(y, x)); 
	    	}
	        line = br.readLine();
	    }
	}
	
	public void update() {
 		for(int y = 0; y < mapHeight; y++) {
 			for(int x = 0; x < mapWidth; x++) {
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
		
		for(int i = 0; i < entitySounds.size(); i++) {
			EntitySound e = entitySounds.get(i);
			e.update();
			if(e.remove) entitySounds.remove(i);
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
	
	public void addSound(EntitySound e) {
		entitySounds.add(e);
	}
	
	/**
	 * Returns the player
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	public boolean isWorldCoordinates(float x, float y) {
		if(x < 0 || x > mapWidth-1 || y < 0 || y > mapHeight-1) return false;
		return true;
	}
	
	/**
	 * Returns a list containing all of the entities in the game, in the form of EntitySprite.
	 * @return List of EntitySprites
	 */
	public List<EntitySprite> getEntities() {
		return entities;
	}
	
	public void draw(Graphics2D g) {
		camera.drawWalls();
		for(EntitySprite e : entities) {
			camera.drawEntity(e);
		}
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
