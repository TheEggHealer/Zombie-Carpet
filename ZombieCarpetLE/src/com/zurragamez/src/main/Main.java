package com.zurragamez.src.main;
import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private final int ID_STONE = 1;
	private final int ID_WOOD = 2;
	
	private  JFrame frame;
	public static final int WIDTH = (int)(1920 / 1.5f);
	public static final int HEIGHT = (int)(1080 / 1.5f);
	private static final String NAME = "Procedural Biome Generation";
	private static final Dimension dim = new Dimension(WIDTH, HEIGHT);
	private static boolean running = false;
	
	private Thread thread;
	
	public static final int GRIDSIZE = 30;
	
	private int mapWidth = 40, mapHeight = 20;
	
	private int speed = 4;
	public static int x, y, selectedX, selectedY;
	private int layer = 1;
	private int selectedID = 1;
	
	private float[][] brightness;
	private int[][] walls, floor, roof;
	private List<Entity> entities = new ArrayList<>();
	
	private boolean placingEntities = false;
	private boolean paintingBrightness;
	private float brushRadius = 4;
	private float brushStrength = 0.6f;
	
	private Button entitiesSwitch, snapSwitch, layerUp, layerDown;
	private int snap;
	
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
		
		addKeyListener(new Keyboard());
		addMouseListener(new Mouse());
		addMouseMotionListener(new Mouse());
		addMouseWheelListener(new Mouse());
		
		brightness = new float[mapHeight][mapWidth];
		walls = new int[mapHeight][mapWidth];
		floor = new int[mapHeight][mapWidth];
		roof = new int[mapHeight][mapWidth];
		
		for(int y = 0; y < mapHeight; y++) {
			for(int x = 0; x < mapWidth; x++) {
				brightness[y][x] = 0;
			}
		}
		
		x = -WIDTH / 2 + mapWidth / 2 * GRIDSIZE;
		y = -HEIGHT / 2 + mapHeight / 2 * GRIDSIZE;
		
		entitiesSwitch = new Button(10, 70, 90, 25, "Switch to Entities");
		snapSwitch = new Button(10, 100, 90, 25, "Snap: Off");
		layerUp = new Button(80, 10, 12, 12, "^");
		layerDown = new Button(98, 10, 12, 12, "v");
	}
	
	public synchronized void start() {
		if(running) return;
		
		running = true;
		thread = new Thread(this, "Main Thread");
		thread.start();
	}
	
	public void update() {
		entitiesSwitch.update();
		if(entitiesSwitch.click) {
			Mouse.button = 0;
			entitiesSwitch.click = false;
			placingEntities = !placingEntities;
			entitiesSwitch.setText(placingEntities ? "Switch to Blocks" : "Switch to Entities");
		}
		snapSwitch.update();
		if(snapSwitch.click) {
			Mouse.button = 0;
			snapSwitch.click = false;
			snap++;
			if(snap > 2) snap = 0;
			snapSwitch.setText("Snap: " + (snap == 0 ? "Off" : (snap == 1 ? "Half" : "Whole")));
		}
		
		layerUp.update();
		if(layerUp.click) {
			Mouse.button = 0;
			layerUp.click = false;
			layer++;
		}
		layerDown.update();
		if(layerDown.click) {
			Mouse.button = 0;
			layerDown.click = false;
			layer--;
		}
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if(e.remove) entities.remove(i);
			e.update();
		}
		
		if(Keyboard.left) x-=speed;
		if(Keyboard.right) x+=speed;
		if(Keyboard.up) y-=speed;
		if(Keyboard.down) y+=speed;
		
		if(Mouse.wheel != 0 && !Keyboard.shift) {
			selectedID -= Mouse.wheel;
			if(selectedID < 1) selectedID = 1;
			Mouse.wheel = 0;
		}
		
		paintingBrightness = Keyboard.shift;
		
		
		if(selectedX > 0 && selectedY > 0 && selectedX < mapWidth * GRIDSIZE && selectedY < mapHeight * GRIDSIZE) {
			if(Mouse.wheel != 0 && Keyboard.shift) {
				if(!Keyboard.ctrl) {
					brushRadius -= Mouse.wheel;
					if(brushRadius < 1) brushRadius = 1;
					else if(brushRadius > 10) brushRadius = 10;
				} else {
					brushStrength -= Mouse.wheel * 0.1f;
					if(brushStrength < 0) brushStrength = 0;
					else if(brushStrength > 1) brushStrength = 1;
				}
				Mouse.wheel = 0;
			}
			
			if(Mouse.button == 1) {
				if(!placingEntities && !paintingBrightness) {
					for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
						for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
							int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE;
							int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE;
							
							if(tileX < 0 || tileY < 0 || tileX > mapWidth-1 || tileY > mapHeight-1) continue;
							
							if(layer == 0) floor[tileY][tileX] = selectedID;
							else if(layer == 1) walls[tileY][tileX] = selectedID;
							else if(layer == 2) roof[tileY][tileX] = selectedID;
						}
					}
				} else if(!paintingBrightness) {
					Mouse.button = 0;
					int n = GRIDSIZE / 2;
					if(snap == 0) entities.add(new Entity(selectedX, selectedY, selectedID, createEntityFromID(selectedID)));
					else if(snap == 1) entities.add(new Entity(((selectedX + n) / n) * n - n, ((selectedY + n) / n) * n - n, selectedID, createEntityFromID(selectedID)));
					else if(snap == 2) entities.add(new Entity(selectedX / GRIDSIZE * GRIDSIZE, selectedY / GRIDSIZE * GRIDSIZE, selectedID, createEntityFromID(selectedID)));
				} else {
					Mouse.button = 0;
					for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
						for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
							if((selectedX - x * GRIDSIZE) < 0 || (selectedY - y * GRIDSIZE) < 0) continue;
							
							int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
							int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
							float brightness = (1f - (float)Math.abs(Math.sqrt((tileX - selectedX) * (tileX - selectedX) + (tileY - selectedY) * (tileY - selectedY))) / GRIDSIZE / brushRadius) * brushStrength;
							if(brightness < 0) brightness = 0;
							if(tileX < 0 || tileY < 0 || tileX > mapWidth * GRIDSIZE || tileY > mapHeight * GRIDSIZE) continue;
							
							float result = this.brightness[tileY / GRIDSIZE][tileX / GRIDSIZE] + brightness;
							if(result > 1) result = 1;
							this.brightness[tileY / GRIDSIZE][tileX / GRIDSIZE] = result;
						}
					}
				}
			} else if(Mouse.button == 3) {
				if(!placingEntities && !paintingBrightness) {
					for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
						for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
							int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE;
							int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE;
							
							if(tileX < 0 || tileY < 0 || tileX > mapWidth-1 || tileY > mapHeight-1) continue;
							
							if(layer == 0) floor[tileY][tileX] = 0;
							else if(layer == 1) walls[tileY][tileX] = 0;
							else if(layer == 2) roof[tileY][tileX] = 0;
						}
					}
				} else if(paintingBrightness) {
					Mouse.button = 0;
					for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
						for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
							if((selectedX - x * GRIDSIZE) < 0 || (selectedY - y * GRIDSIZE) < 0) continue;
							
							int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
							int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
							float brightness = (1f - (float)Math.abs(Math.sqrt((tileX - selectedX) * (tileX - selectedX) + (tileY - selectedY) * (tileY - selectedY))) / GRIDSIZE / brushRadius) * brushStrength;
							if(brightness < 0) brightness = 0;
							if(tileX < 0 || tileY < 0 || tileX > mapWidth * GRIDSIZE || tileY > mapHeight * GRIDSIZE) continue;
							
							float result = this.brightness[tileY / GRIDSIZE][tileX / GRIDSIZE] - brightness;
							if(result < 0) result = 0;
							this.brightness[tileY / GRIDSIZE][tileX / GRIDSIZE] = result;
						}
					}
				}
			}
		}
		
		if(Keyboard.esc) {
			Keyboard.esc = false;
			save();
		}
		
		if(Keyboard._up) {
			Keyboard._up = false;
			layer++;
		}
		else if(Keyboard._down) {
			Keyboard._down = false;
			layer--;
		}
		if(layer < 0) layer = 0;
		else if(layer > 2) layer = 2;
		
		selectedX = Mouse.x + x;
		selectedY = Mouse.y + y;
	}
	
	public void save() {
		try {
			JFileChooser chooser = new JFileChooser();
			int choice = chooser.showOpenDialog(null);
			if(choice != JFileChooser.APPROVE_OPTION) return;
			String chosenPath = chooser.getSelectedFile().getPath();
			if(chosenPath.endsWith(".txt")) chosenPath = chosenPath.substring(0, chosenPath.length() - 4);
			File f_floor = new File(chosenPath + "_floor.txt");
			File f_walls = new File(chosenPath + "_walls.txt");
			File f_roof = new File(chosenPath + "_roof.txt");
			File f_brightness = new File(chosenPath + "_brightness.txt");
			File f_entities = new File(chosenPath + "_entities.txt");

			StringBuilder floor = new StringBuilder();
			StringBuilder walls = new StringBuilder();
			StringBuilder roof = new StringBuilder();
			StringBuilder brightness = new StringBuilder();
			StringBuilder entities = new StringBuilder();
			
			floor.append(mapWidth + "," + mapHeight + "\n");
			walls.append(mapWidth + "," + mapHeight + "\n");
			roof.append(mapWidth + "," + mapHeight + "\n");
			brightness.append(mapWidth + "," + mapHeight + "\n");
			
			for(int y = 0; y < mapWidth; y++) {
				for(int x = 0; x < mapHeight; x++) {
					floor.append(this.floor[x][y]);
					if(x != mapHeight - 1) floor.append(",");
					
					walls.append(this.walls[x][y]);
					if(x != mapHeight - 1) walls.append(",");
					
					roof.append(this.roof[x][y]);
					if(x != mapHeight - 1) roof.append(",");
					
					brightness.append(this.brightness[x][y]);
					if(x != mapHeight - 1) brightness.append(",");
				}
				if(y != mapWidth - 1) {
					floor.append("\n");
					walls.append("\n");
					roof.append("\n");
					brightness.append("\n");
				}
			}
			
			for(Entity e : this.entities) {
				entities.append(e.id + "," + e.x / (float)GRIDSIZE + "," + e.y / (float)GRIDSIZE + "\n");
			}
			
			PrintWriter w_floor = new PrintWriter(f_floor);
			w_floor.println(floor.toString());
			w_floor.close();
			
			PrintWriter w_walls = new PrintWriter(f_walls);
			w_walls.println(walls.toString());
			w_walls.close();
			
			PrintWriter w_roof = new PrintWriter(f_roof);
			w_roof.println(roof.toString());
			w_roof.close();
			
			PrintWriter w_brightness = new PrintWriter(f_brightness);
			w_brightness.println(brightness.toString());
			w_brightness.close();
			
			PrintWriter w_entities = new PrintWriter(f_entities);
			w_entities.println(entities.toString());
			w_entities.close();
		} catch(Exception e) {
			System.err.println(e.getStackTrace());
			e.printStackTrace();
		}
	}
	
	public float clamp(float value, float min, float max) {
		return Math.min(max, Math.max(value, min));
	}
	
	public BufferedImage getImageFromID(int id) {
		if(id == ID_STONE) return SpriteSheet.stone.image;
		else if(id == ID_WOOD) return SpriteSheet.wood.image;
		else return null;
	}
	
	public BufferedImage createEntityFromID(int id) {
		if(id == 1) {
			return SpriteSheet.zombie.image;
		}
		else if(id == 2) {
			return SpriteSheet.light.image;
		}
		return null;
	}
	
	public void draw(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		for(int y = 0; y < mapHeight; y++) {
			for(int x = 0; x < mapWidth; x++) {
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
				g.setComposite(ac);
				BufferedImage image = getImageFromID(floor[y][x]);
				if(image != null) g.drawImage(image, x * GRIDSIZE - Main.x, y * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE, null);
				
				ac = ac.derive(1f);
				image = getImageFromID(walls[y][x]);
				g.setComposite(ac);
				if(image != null) g.drawImage(image, x * GRIDSIZE - Main.x, y * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE, null);
				
				if(layer == 2) {
					image = getImageFromID(roof[y][x]);
					if(image != null) g.drawImage(image, x * GRIDSIZE - Main.x, y * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE, null);
				}
					
				if(paintingBrightness) {
					g.setColor(new Color(255, 255, 255, (int)(brightness[y][x] * 255 * 0.9f)));
					g.fillRect(x * GRIDSIZE - Main.x, y * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE);
					g.setColor(new Color(255, 100, 0));
					g.drawString((int)(brightness[y][x] * 100) + "", x * GRIDSIZE - Main.x + GRIDSIZE / 2 - 8, y * GRIDSIZE - Main.y + 3 + GRIDSIZE / 2);
				}
			}
		}
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			g.drawImage(e.img, -x + (int)e.x - GRIDSIZE / 2, -y + (int)e.y - GRIDSIZE, GRIDSIZE, GRIDSIZE, null);
			if(e.hover) {
				g.setColor(Color.white);
				g.drawRect( -x + (int)e.x - GRIDSIZE / 2, -y + (int)e.y - GRIDSIZE, GRIDSIZE, GRIDSIZE);
			}
		}
		
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		if(selectedX > 0 && selectedY > 0 && selectedX < mapWidth * GRIDSIZE && selectedY < mapHeight * GRIDSIZE) {
			g.setColor(new Color(255, 255, 255, 50));
			if(!paintingBrightness) {
				int n = GRIDSIZE / 2;
				if(!placingEntities) {
					for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
						for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
							int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE;
							int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE;
							
							if((selectedX - x * GRIDSIZE) < 0 || (selectedY - y * GRIDSIZE) < 0 || tileX > mapWidth - 1 || tileY > mapHeight - 1) continue;
							
							g.fillRect(tileX * GRIDSIZE - Main.x, tileY * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE);
						}
					}
				}
				else if(snap == 0) g.fillRect(selectedX - x - 2, selectedY - y - 2, 4, 4);
				else if(snap == 1) g.fillRect(((selectedX + n) / n) * n - n - x - 2, ((selectedY + n) / n) * n - n - y - 2, 4, 4);
				else if(snap == 2) g.fillRect(selectedX / GRIDSIZE * GRIDSIZE - x - 2, selectedY / GRIDSIZE * GRIDSIZE - y - 2, 4, 4);
			} else {
				for(int y = -(int)brushRadius + 1; y < (int)brushRadius; y++) {
					for(int x = -(int)brushRadius + 1; x < (int)brushRadius; x++) {
						if((selectedX - x * GRIDSIZE) < 0 || (selectedY - y * GRIDSIZE) < 0) continue;
						
						int tileX = (selectedX - x * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
						int tileY = (selectedY - y * GRIDSIZE) / GRIDSIZE * GRIDSIZE + GRIDSIZE / 2;
						float brightness = (1f - (float)Math.abs(Math.sqrt((tileX - selectedX) * (tileX - selectedX) + (tileY - selectedY) * (tileY - selectedY))) / GRIDSIZE / brushRadius) * brushStrength;
						if(brightness < 0) brightness = 0;
						if(tileX < 0 || tileY < 0 || tileX > mapWidth * GRIDSIZE || tileY > mapHeight * GRIDSIZE) continue;
						
						g.setColor(new Color(255, 255, 255, (int)(255 * brightness)));
						g.fillRect(tileX / GRIDSIZE * GRIDSIZE - Main.x, tileY / GRIDSIZE * GRIDSIZE - Main.y, GRIDSIZE, GRIDSIZE);
					}
				}
			}
			
			//(selectedX + n) / n) * n, ((selectedY + n) / n) * n
		} else g.setColor(new Color(200, 20, 20, 50));
		
		g.drawString(selectedX / GRIDSIZE + "", selectedX / GRIDSIZE * GRIDSIZE - x + 5, -y - 2);
		g.drawString(selectedY / GRIDSIZE + "", -x - g.getFontMetrics().stringWidth(selectedY / GRIDSIZE + "") - 2, selectedY / GRIDSIZE * GRIDSIZE - y + g.getFontMetrics().getHeight());
		
		if(x < 0 && x > -WIDTH) {
			g.drawLine(-x, 0, -x, HEIGHT);
		}
		if(y < 0 && y > -HEIGHT) {
			g.drawLine(0, -y, WIDTH, -y);
		}
		if(x < mapWidth*GRIDSIZE && x > -WIDTH + mapWidth * GRIDSIZE) {
			g.drawLine(mapWidth*GRIDSIZE - x, 0, mapWidth*GRIDSIZE - x, HEIGHT);
		}
		if(y < mapHeight*GRIDSIZE && y > -HEIGHT + mapHeight * GRIDSIZE) {
			g.drawLine(0, mapHeight*GRIDSIZE - y, WIDTH, mapHeight*GRIDSIZE - y);
		}
		
		g.setColor(Color.white);
		g.drawString("Layer: " + (layer == 0 ? "Floor" : (layer == 1 ? "Walls" : "Roof")), 10, 20);
		
		BufferedImage selectedTile = getImageFromID(selectedID);
		if(!placingEntities) {
			if(selectedTile != null) g.drawImage(selectedTile, 10, 30, GRIDSIZE, GRIDSIZE, null);
		} else {
			if(createEntityFromID(selectedID) != null) g.drawImage(createEntityFromID(selectedID), 10, 30, GRIDSIZE, GRIDSIZE, null);
		}
		g.drawRect(9, 29, GRIDSIZE + 2, GRIDSIZE + 2);
		g.drawString("Selected ID: ", GRIDSIZE + 15, 36);
		g.drawString("" + selectedID, GRIDSIZE + 15, 48);
		g.drawString("Brush radius: " + brushRadius, 10, 140);
		g.drawString("Brush Strength: " + (int)(brushStrength * 100) + "%", 10, 155);
		
		entitiesSwitch.draw(g);
		snapSwitch.draw(g);
		layerUp.draw(g);
		layerDown.draw(g);
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(new Color(20, 20, 20));
		g.fillRect(0, 0, WIDTH, HEIGHT);
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
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Main main = new Main();
		main.start();
		
	}
	
}
