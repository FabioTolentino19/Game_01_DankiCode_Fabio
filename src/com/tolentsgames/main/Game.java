package com.tolentsgames.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.tolentsgames.entities.BulletShoot;
import com.tolentsgames.entities.Enemy;
import com.tolentsgames.entities.Entity;
import com.tolentsgames.entities.Player;
import com.tolentsgames.graficos.Spritesheet;
import com.tolentsgames.graficos.UI;
import com.tolentsgames.world.Camera;
import com.tolentsgames.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
		
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static int WIDTH = 240;
	public static int HEIGHT = 160;
	public static int SCALE = 3;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;	
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("alterebro-pixel-font.tff");
	//public Font newfont;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	//Sistema de cutscene!
	//utilizando gameState
	
	public int waitToStart = 0, maxWaitToStart = 60*3;
	
	public Menu menu;
	
	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightMapPixels;
	public static int[] minimapaPixels;
	public static boolean[] minimapaShow;
	
	public static BufferedImage minimapa;
	
	public int mx, my;	
	public boolean saveGame = false;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize())); fullscreen com camera
		//WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().width/3;
		//HEIGHT =(int)Toolkit.getDefaultToolkit().getScreenSize().height/3;
		//SCALE = 3;
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos.
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));			
		entities.add(player);
		world = new World("/level1.png");
		
		minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapaPixels = ((DataBufferInt)minimapa.getRaster().getDataBuffer()).getData();
		minimapaShow = new boolean[minimapaPixels.length];
		/*
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		menu = new Menu();
	}
	
	public void initFrame() {
		frame = new JFrame("Primeiro Game");
		frame.add(this);
		frame.setUndecorated(true); //Retira a barra de comandos para minimizar ou fechar o app.
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level", "vida"};
				int[] opt2 = {this.CUR_LEVEL,(int) player.life};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo!");
			}
			this.restartGame = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				//Avançar para o próximo nível.
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver)
					this.showMessageGameOver = false;
				else
					this.showMessageGameOver = true;
			}
		} else if(gameState == "MENU") {
			player.updateCamera();
			menu.tick();
		} else if(gameState == "ENTRADA") {
			if(player.getY() > 96 ) {
				player.updateCamera();
				player.y--;
			} else {
				gameState = "DIALOGO1";
			}
		} else if(gameState == "DIALOGO5") {
			waitToStart++;
			if(waitToStart == maxWaitToStart) {
				gameState = "NORMAL";
			}
		}
			
			if(restartGame) {
				this.restartGame = false;
				gameState = "ENTRADA";
				Sound.musicBackground.loop(Sound.musicVolume);
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
	}
	
	/*
	public void drawRectangleExample(int xOff, int yOff) {
		for(int x = 0; x < 32; x++) {
			for(int y = 0; y < 32; y++) {
				int xOffset = x + xOff;
				int yOffset = y + yOff;
				if(xOffset < 0 || yOffset < 0 || xOffset >= WIDTH || yOffset >= HEIGHT)
					continue;				
				pixels[xOffset + (yOffset*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	public void applyLight() {
	/*	for(int xx = 0; xx < Game.WIDTH; xx++) {
			for(int yy = 0; yy < Game.HEIGHT; yy++ ) {
				if(lightMapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff) {
					pixels[xx + (yy * Game.WIDTH)] = 0;
				} else {  } //dentro do else é a area fora da luz  
			}
		} */
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		/* Rederização do jogo */
		//Graphics2D g2 = (Graphics2D) g;
		world.render(g);
		
		Collections.sort(entities, Entity.nodeSorter);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);					
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}			
		
		applyLight();
		ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		//g.drawImage(image, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null); fullscreen com camera
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.drawString("Munição: " + player.ammo, (int) (0.8*WIDTH*SCALE), (int)(HEIGHT*SCALE/24));
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0,  0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setColor(Color.white);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.drawString("Game Over", (WIDTH*SCALE/2)-80, (HEIGHT*SCALE/2)-20);
			g.setFont(new Font("arial", Font.BOLD, 25));
			if(showMessageGameOver)
				g.drawString(">Pressione Enter para reiniciar<", (WIDTH*SCALE/2)-190, (HEIGHT*SCALE/2)+40);
		} else if(gameState == "MENU") {
			menu.render(g);
		} else if(gameState == "DIALOGO1") {
			g.drawString("Chefe, qual a minha missão para hoje ?", 100, 100);
		} else if(gameState == "DIALOGO2") {
			g.drawString("Salvar a Princesa, mas muito cuidado com os inimigos !!", 100, 100);
		} else if(gameState == "DIALOGO3") {
			g.drawString("Será que eu conseguirei ?", 100, 100);
		} else if(gameState == "DIALOGO4") {
			g.drawString("Sim, basta seguir o nosso treinamento, e o seu coração !!", 100, 100);
		} else if(gameState == "DIALOGO5") {
			g.drawString("Obrigado pro tudo chefe, não vou decepcioná-lo !!!", 100, 100);
		} 
		World.renderMiniMap();
		g.drawImage(minimapa, (int)(0.8*WIDTH*SCALE), (int)(0.1*HEIGHT*SCALE), World.WIDTH, World.HEIGHT, null);
		/*
		Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(200+25 - my, 200+25-mx);
		g2.rotate(angleMouse, 200+25, 200+25);
		g.setColor(Color.blue);
		g.fillRect(200, 200, 50, 50);
		*/
		/*	
		g.setFont(newfont);
		g.setColor(Color.yellow);
		g.drawString("Teste nova fonte !!!", 90, 150);
		*/
		bs.show();
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1 ) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				//System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}
		
		
		stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.right = true;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			player.up = true;
			if(gameState == "MENU") {
				menu.up = true;
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			player.down = true;
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_A) {
			if(Sound.musicVolume > 0) {
				Sound.musicVolume--;
				Sound.musicBackground.setVolume(Sound.musicVolume);
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_Q) {
			if(Sound.musicVolume < Sound.maxVolume) {
				Sound.musicVolume++;
				Sound.musicBackground.setVolume(Sound.musicVolume);
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			if(Sound.effectsVolume > 0)
				Sound.effectsVolume--;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_W) {
			if(Sound.effectsVolume < Sound.maxVolume)
				Sound.effectsVolume++;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(gameState == "DIALOGO1")
				gameState = "DIALOGO2";
			else if(gameState == "DIALOGO2")
				gameState = "DIALOGO3";
			else if(gameState == "DIALOGO3")
				gameState = "DIALOGO4";
			else if(gameState == "DIALOGO4")
				gameState = "DIALOGO5";
			else if(gameState == "DIALOGO5")
				gameState = "NORMAL";
			else this.restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
			Sound.musicBackground.stop();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL")
				this.saveGame = true;		
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.right = false;
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			player.up = false;
			if(gameState == "MENU") {
				menu.up = false;
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			player.down = false;
			if(gameState == "MENU") {
				menu.down = false;
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / SCALE);
		player.my = (e.getY() / SCALE);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

	
}
