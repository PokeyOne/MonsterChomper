package com.pokeyone.ludum33;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.pokeyone.ludum33.data.Score;
import com.pokeyone.ludum33.entity.Enemy;
import com.pokeyone.ludum33.entity.Player;
import com.pokeyone.ludum33.shop.ShopItem;

public class Game extends JPanel implements Runnable, KeyListener{

	Thread thread = new Thread(this);
	
	private enum Gamestate {
		MENU, GAME, SCORE, NAME, HELP, SHOP
	}
	private Gamestate gamestate = Gamestate.NAME;
	
	private String[] menuItems = {"Play", "Quit", "Help", "Shop"};
	private int menuItemSelected = 0;
	
	private Score[] scores = new Score[10];
	private int money = 0;
	
	private ShopItem[] shopItems = {new ShopItem(10, 10, 2, "Starting Size"), new ShopItem(10, 40, 2, "Downgrade Enemy Size by One per Upgrade")};
	private int shopItemSelected = 0;
	
	private Image imageBackground;
	private Image imageButton;
	private Image imageButtonSelected;
	private Image imagePowerUp;
	
	private String name = "BetaTester";
	
	private boolean rightPressed = false;
	private boolean leftPressed = false;
	
	private Player player = new Player();
	private Enemy enemy;
	private double enemySpeed;
	
	public Game(){
		setPreferredSize(new Dimension((int)(640*1.5), (int)(480*1.5)));
		setFocusable(true);
		addKeyListener(this);
		
		try{
			loadScores();
			loadStats();
			
			imageBackground = ImageIO.read(new File("res/Background.png"));
			imageButton = ImageIO.read(new File("res/Button.png"));
			imageButtonSelected = ImageIO.read(new File("res/ButtonSelected.png"));
			imagePowerUp = ImageIO.read(new File("res/PowerUp.png"));
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/AldotheApache.ttf")));
		}catch(NullPointerException e){
			e.printStackTrace();
			System.exit(404);
		}catch(IOException e){
			try{
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				InputStream fontInput = this.getClass().getResourceAsStream("/AldotheApache.ttf");
				ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontInput));
				
				imageBackground = ImageIO.read(this.getClass().getResourceAsStream("/Background.png"));
				imageButton = ImageIO.read(this.getClass().getResourceAsStream("/Button.png"));
				imageButtonSelected = ImageIO.read(this.getClass().getResourceAsStream("/ButtonSelected.png"));
				imagePowerUp = ImageIO.read(this.getClass().getResourceAsStream("/PowerUp.png"));
			}catch(IOException en){
				en.printStackTrace();
				System.exit(404);
			} catch (FontFormatException e1) {
				e1.printStackTrace();
			}
		}catch(FontFormatException e){
			e.printStackTrace();
			System.exit(404);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		thread.start();
	}
	
	private void loadScores() throws URISyntaxException, IOException{
		try{
			File file = new File("res/scores.save");
			FileReader fr = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fr);
			
			for(int i = 0; i < 10; i++){
				String str = bfr.readLine();
				if(str == null || str == " " || str == "\n" || str == ""){
					break;
				}else{
				}
				String[] strings = str.split(" ", 2);
				
				try{
					scores[i] = new Score(Integer.valueOf(strings[0]), strings[1]);
				}catch(NumberFormatException e){
					
				}
			}
			
			bfr.close();
		}catch(FileNotFoundException e){
			try{
				InputStreamReader isr = new InputStreamReader(this.getClass().getResourceAsStream("/scores.save"));
				BufferedReader bfr = new BufferedReader(isr);
				
				for(int i = 0; i < 10; i++){
					String str = bfr.readLine();
					if(str == null || str == " " || str == "\n" || str == ""){
						break;
					}else{
					}
					String[] strings = str.split(" ", 2);
					
					try{
						scores[i] = new Score(Integer.valueOf(strings[0]), strings[1]);
					}catch(NumberFormatException en){
						
					}
				}
				
				bfr.close();
			}catch(FileNotFoundException en){
				System.out.println("scores file not found");
				System.exit(404);
			}
		}
	}
	
	private void newScore(String name, int amo){
		Score score = new Score(amo, name);
		
		money += amo;
		
		for(int i = 0; i < 10; i++){
			if(scores[i] != null){
				if(score.getScore() > scores[i].getScore()){
					for(int f = 9; f > i; f--){
						scores[f] = scores[f-1];
					}
					
					scores[i] = score;
					break;
				}
			}else{
				scores[i] = score;
				break;
			}
		}
	}
	
	private void saveScores() throws IOException, URISyntaxException {
		try {
			File file = new File("res/scores.save");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < 10; i++){
				if(scores[i] != null){
					String s = scores[i].getSaveString();
					bw.write(s);
				}else{
					System.out.println("scores[" + i + "] = null");
				}
			}
			
			bw.close();
		} catch (FileNotFoundException e) {
			try {
				String path = "res/";
				File file = new File(path + "scores.save");
				File file2 = new File(path);
				file2.mkdirs();
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				
				for(int i = 0; i < 10; i++){
					if(scores[i] != null){
						String s = scores[i].getSaveString();
						bw.write(s);
					}else{
						System.out.println("scores[" + i + "] = null");
					}
				}
				
				bw.close();
			} catch (FileNotFoundException en) {
				System.out.println("severe error, cannot save score board\n\nStack Trace:\n\n");
				en.printStackTrace();
			}
		}
	}
	
	public void loadStats() throws URISyntaxException, IOException{
		try{
			File file = new File("res/stats.save");
			FileReader fr = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fr);
			
			money = Integer.valueOf(bfr.readLine());
			
			for(int i = 0; i < shopItems.length; i++){
				try{
					shopItems[i].setAmount(Integer.valueOf(bfr.readLine()));
				}catch(NumberFormatException e){
					System.out.println("couldn't find current amount of " + shopItems[i].getName());
					shopItems[i].setAmount(0);
				}
				
				try{
					shopItems[i].setPrice(Integer.valueOf(bfr.readLine()));
				}catch(NumberFormatException e){
					System.out.println("couldn't find current price of " + shopItems[i].getName());
					shopItems[i].setAmount(0);
				}
			}
			
			bfr.close();
		}catch(FileNotFoundException e){
			try{
				InputStreamReader isr = new InputStreamReader(this.getClass().getResourceAsStream("/stats.save"));
				BufferedReader bfr = new BufferedReader(isr);
				
				money = Integer.valueOf(bfr.readLine());
				
				for(int i = 0; i < shopItems.length; i++){
					try{
						shopItems[i].setAmount(Integer.valueOf(bfr.readLine()));
					}catch(NumberFormatException en){
						System.out.println("couldn't find current amount of " + shopItems[i].getName());
						shopItems[i].setAmount(0);
					}
					
					try{
						shopItems[i].setPrice(Integer.valueOf(bfr.readLine()));
					}catch(NumberFormatException en){
						System.out.println("couldn't find current price of " + shopItems[i].getName());
						shopItems[i].setAmount(0);
					}
				}
				
				bfr.close();
			}catch(FileNotFoundException en){
				System.out.println("stats not found");
				System.exit(404);
			}
		}
	}
	
	public void saveStats() throws IOException, URISyntaxException {
		try{
			File file = new File("res/stats.save");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(money + "\n");
			
			for(int i = 0; i < shopItems.length; i++){
				bw.write(shopItems[i].getAmount() + "\n");
				bw.write(shopItems[i].getPrice() + "\n");
			}
			
			bw.close();
		}catch(FileNotFoundException e){
			try{
				String path = "res/";
				File file = new File(path + "stats.save");
				File file2 = new File(path);
				file2.mkdirs();
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write(money + "\n");
				
				for(int i = 0; i < shopItems.length; i++){
					bw.write(shopItems[i].getAmount() + "\n");
					bw.write(shopItems[i].getPrice() + "\n");
				}
				
				bw.close();
			}catch(FileNotFoundException en){
				System.out.println("severe error, cannot save stats\n\nStack Trace:\n\n");
				en.printStackTrace();
			}
		}
	}
	
	public void exit(){
		try {
			saveScores();
			saveStats();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	private void tick(){
		if(gamestate == Gamestate.GAME){
			if(rightPressed){
				if(player.getX()+80 < getWidth())
					player.addX(9);
			}
			if(leftPressed){
				if(player.getX() > 0)
					player.addX(-9);
			}
			
			if(player.getY() < 320){
				player.addYSpeed(1);
			}
			
			player.addY(player.getYSpeed());
			
			if(player.getY() >= 320){
				player.setY(320);
				player.setYSpeed(0);
			}
			
			if(enemy == null){
				enemySpeed = Math.floor(player.getEnemiesDefeated()/25.0)+6;
				enemy = new Enemy(player.getPoints()-(2*shopItems[1].getAmount()), enemySpeed);
			}
			
			if(enemy.getX() < -80){
				enemy = null;
			}
			
			if(enemy != null && player.getX()+90 > enemy.getX() && player.getX() < enemy.getX()+80
					&& player.getY()+90 > 330 && player.getY()+90 < 411){
				if(enemy.isPowerUp()){
					switch(enemy.getPowerType()){
					case NONE:
						break;
					case PLUS_SIZE:
						player.addPoints(100);
						break;
					case PLUS_SPEED:
						player.addKills(50);
						break;
					}
					enemy = null;
				}else if(player.getPoints() >= enemy.getPoints()){
					enemy = null;
					player.killedEnemy();
					player.addPoints((int)Math.floor(player.getEnemiesDefeated()/10.0)+1);
				}else{
					enemy = null;
					newScore(name, player.getPoints());
					gamestate = Gamestate.SCORE;
				}
			}
			
			if(enemy != null)
				enemy.tick();
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.drawImage(imageBackground, 0, 0, getWidth(), getHeight(), null);
		g.setFont(new Font("Aldo the Apache", Font.PLAIN, 120));
		
		switch(gamestate){
		case MENU:
			
			for(int i = 0; i < 15; i++){
				g.setColor(new Color(((i+1)*(255/15)), ((i+1)*(102/15)), ((i+1)*(0/15))));
				g.drawString("Monster Chomper", getWidth()/2-(60*7)-(1*i), 150-(1*i));
			}
			
			for(int i = 0; i < menuItems.length; i++){
				if(menuItemSelected != i){
					g.drawImage(imageButton, getWidth()/2-128*2, getHeight()/2-50*2+(70*i*2)-100, 256*2, 64*2, null);
					g.setColor(new Color(100, 100, 100));
					g.drawString(menuItems[i], getWidth()/2-128*2+42, getHeight()/2-50*2+(70*i*2)+57*2-100);
					g.setColor(new Color(250, 250, 250));
					g.drawString(menuItems[i], getWidth()/2-128*2+40, getHeight()/2-50*2+(70*i*2)+55*2-100);
				}else{
					g.drawImage(imageButtonSelected, getWidth()/2-133*2, getHeight()/2-50*2+(70*i*2)-100, 256*2+20, 64*2, null);
					g.setColor(new Color(100, 100, 100));
					g.drawString(menuItems[i], getWidth()/2-128*2+42, getHeight()/2-50*2+(70*i*2)+57*2-100);
					g.setColor(new Color(250, 250, 250));
					g.drawString(menuItems[i], getWidth()/2-128*2+40, getHeight()/2-50*2+(70*i*2)+55*2-100);
				}
			}
			
			break;
		case GAME:
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 60));
			g.setColor(new Color(100, 70, 30));
			g.fillRect(0, getHeight()-80, getWidth(), 80);
			g.setColor(new Color(0, 150, 10));
			g.fillRect(0, getHeight()-80, getWidth(), 20);
			
			g2d.setColor(new Color(37, 149, 37));
			g2d.fillRoundRect(player.getX(), player.getY()+240, 90, 90, 10, 10);
			g2d.setColor(new Color(51, 204, 51));
			g2d.fillRoundRect(player.getX()+5, player.getY()+245, 80, 80, 10, 10);
			g2d.setColor(Color.WHITE);
			g2d.fillOval(player.getX()+70, player.getY()+240, 30, 30);
			g2d.setColor(new Color(10, 100, 200));
			g2d.fillOval(player.getX()+90, player.getY()+250, 10, 10);
			
			g.setColor(Color.WHITE);
			g.drawString("Size: " + player.getPoints() + "        Killed: " + player.getEnemiesDefeated(), 13, 73);
			g.setColor(Color.BLACK);
			g.drawString("Size: " + player.getPoints() + "        Killed: " + player.getEnemiesDefeated(), 10, 70);
			
			if(enemy != null){
				if(!enemy.isPowerUp()){
					g2d.setColor(enemy.getColor());
					g2d.fillRoundRect(enemy.getX(), 570, 80, 80, 10, 10);
					g2d.setColor(new Color(enemy.getColor().getRed()+55, enemy.getColor().getGreen()+55, enemy.getColor().getBlue()+55));
					g2d.fillRoundRect(enemy.getX()+5, 575, 70, 70, 10, 10);
					g2d.setColor(Color.WHITE);
					g2d.fillOval(enemy.getX()-10, 580, 30, 30);
					g2d.setColor(new Color(10, 100, 200));
					g2d.fillOval(enemy.getX()-10, 580, 10, 10);
					
					g.setColor(Color.BLACK);
					g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
					g.drawString(enemy.getPoints() + "", enemy.getX(), 570);
					g.setFont(new Font("Aldo the Apache", Font.PLAIN, 80));
					if(enemy.getX() < getWidth())
						g.drawString("Enemy: " + enemy.getPoints(), getWidth()/2, getHeight()/2+40);
				}else{
					g2d.setColor(enemy.getColor());
					g2d.fillRoundRect(enemy.getX(), 570, 80, 80, 10, 10);
					g2d.setColor(new Color(enemy.getColor().getRed()+55, enemy.getColor().getGreen()+55, enemy.getColor().getBlue()+55));
					g2d.fillRoundRect(enemy.getX()+5, 575, 70, 70, 10, 10);
					g2d.drawImage(imagePowerUp, enemy.getX() + 5, 570 + 5, 80 - 10, 80 - 10, null);
				}
			}
			break;
		case NAME:
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.fillRect(50, 50, getWidth()-100, getHeight()-100);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 60));
			g.setColor(Color.BLACK);
			g.drawString("Name Entry", getWidth()/2-120+3, 100+3);
			g.setColor(Color.WHITE);
			g.drawString("Name Entry", getWidth()/2-120, 100);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
			g.setColor(Color.BLACK);
			g.drawString(name+"_", getWidth()/2-((name.toCharArray().length+1)*9), 200);
			g.setColor(Color.WHITE);
			g.drawString(name+"_", getWidth()/2-((name.toCharArray().length+1)*9)-3, 200-3);
			
			g.drawString("Type keys to change the name above", 60, getHeight()-60);
			
			break;
		case SCORE:
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.fillRect(50, 50, getWidth()-100, getHeight()-100);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 60));
			g.setColor(Color.BLACK);
			g.drawString("Leader Board", getWidth()/2-180+3, 100+3);
			g.setColor(Color.WHITE);
			g.drawString("Leader Board", getWidth()/2-180, 100);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
			for(int i = 0; i < 10; i++){
				if(scores[i] != null){
					g.setColor(Color.BLACK);
					g.drawString(scores[i].getName(), 63, 163+50*i);
					g.drawString("" + scores[i].getScore(), 643, 163+50*i);
					g.setColor(Color.WHITE);
					g.drawString(scores[i].getName(), 60, 160+50*i);
					g.drawString("" + scores[i].getScore(), 640, 160+50*i);
				}
			}
			
			break;
		case HELP:
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.fillRect(10, 10, getWidth()-20, getHeight()-20);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 60));
			g.setColor(Color.BLACK);
			g.drawString("help screen!", getWidth()/2-180+3, 70+3);
			g.setColor(Color.WHITE);
			g.drawString("help screen!", getWidth()/2-180, 70);
			
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
			String helpMsgStr = "Welcome to Monster Chomper, my submission for Ludum dare 33 2015. The game"
					+ " is very simple, just use the arrow/wasd keys to move back and forth, and jump."
					+ " During the game, monsters will come at you, and you must jump to dodge them if"
					+ " their size(number above their head) is bigger than yours, and eat them(run"
					+ " into them) if their size is smaller. Press ESC or Enter to exit this message.";
			char[] helpMsg = helpMsgStr.toCharArray();
			
			for(int y = 0; y < 20; y++){
				for(int x = 0; x < 28; x++){
					try{
						g.setColor(Color.BLACK);
						g.drawString("" + helpMsg[x+y*28], 63+30*x, 123+45*y);
						g.setColor(Color.WHITE);
						g.drawString("" + helpMsg[x+y*28], 60+30*x, 120+45*y);
					}catch(ArrayIndexOutOfBoundsException e){
						break;
					}
				}
			}
			break;
		case SHOP:
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.fillRect(10, 50, getWidth()-20, getHeight()-60);
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
			g.setColor(new Color(0x000000));
			g.drawString("Money: " + money, 10+3, 45);
			g.setColor(new Color(0xFFFFFF));
			g.drawString("Money: " + money, 10, 45-3);
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 20));
			for(int i = 0; i < shopItems.length; i++){
				if(shopItemSelected == i){
					g.setColor(new Color(0.2f, 0.2f, 0.4f, 0.8f));
				}else{
					g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
				}
				g.fillRect(20, 60+((getHeight()-40)/4*i)+10*i, getWidth()-40, (getHeight()-70)/4);
				g.setColor(new Color(0x000000));
				g.drawString(shopItems[i].getName(), 30, 105+((getHeight()-70)/4*i) + 10*i);
				g.setColor(new Color(0xFFFFFF));
				g.drawString(shopItems[i].getName(), 30-3, 105-3+((getHeight()-70)/4*i) + 10*i);
				
				g.setColor(new Color(0x000000));
				g.drawString("Price" + shopItems[i].getPrice(), 630, 105+((getHeight()-70)/4*i) + 10*i);
				g.setColor(new Color(0xFFFFFF));
				g.drawString("Price" + shopItems[i].getPrice(), 630-3, 105-3+((getHeight()-70)/4*i) + 10*i);
				
				for(int n = 0; n < shopItems[i].getMaxAmount(); n++){
					g.setColor(new Color(0xFEFEFE));
					g.drawOval(30 + 30*n, 150+((getHeight()-70)/4*i) + 10*i, 25, 25);
					
					if(shopItems[i].getAmount() > n){
						g.fillOval(35 + 30*n, 155+((getHeight()-70)/4*i) + 10*i, 15, 15);
					}
				}
				//TODO: add scrolling/pages, to support lots of upgrades in future
			}
			break;
		}
	}
	
	public void run(){
		while(true){
			tick();
			repaint();
			try{
				Thread.sleep(15);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}



	@Override
	public void keyPressed(KeyEvent e) {
		switch(gamestate){
		case MENU:
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				menuItemSelected--;
				if(menuItemSelected < 0){
					menuItemSelected = menuItems.length-1;
				}
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				menuItemSelected++;
				if(menuItemSelected > menuItems.length-1){
					menuItemSelected = 0;
				}
				break;
			case KeyEvent.VK_ENTER:
				switch(menuItemSelected){
				case 0:
					player = new Player(50*shopItems[0].getAmount());
					enemySpeed = 2.0;
					enemy = null;
					gamestate = Gamestate.GAME;
					break;
				case 1:
					exit();
					break;
				case 2:
					gamestate = Gamestate.HELP;
					break;
				case 3:
					gamestate = Gamestate.SHOP;
					break;
				}
				break;
			}
			break;
		case GAME:
			switch(e.getKeyCode()){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if(player.getY()+80 >= 380){
					player.addYSpeed(-20);
					System.out.println("jumping");
				}
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				rightPressed = true;
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				leftPressed = true;
				break;
			}
			break;
		case NAME:
			switch(e.getKeyCode()){
			case KeyEvent.VK_A:
				name = (name + "a");
				break;
			case KeyEvent.VK_B:
				name = (name + "b");
				break;
			case KeyEvent.VK_C:
				name = (name + "c");
				break;
			case KeyEvent.VK_D:
				name = (name + "d");
				break;
			case KeyEvent.VK_E:
				name = (name + "e");
				break;
			case KeyEvent.VK_F:
				name = (name + "f");
				break;
			case KeyEvent.VK_G:
				name = (name + "g");
				break;
			case KeyEvent.VK_H:
				name = (name + "h");
				break;
			case KeyEvent.VK_I:
				name = (name + "i");
				break;
			case KeyEvent.VK_J:
				name = (name + "j");
				break;
			case KeyEvent.VK_K:
				name = (name + "k");
				break;
			case KeyEvent.VK_L:
				name = (name + "l");
				break;
			case KeyEvent.VK_M:
				name = (name + "m");
				break;
			case KeyEvent.VK_N:
				name = (name + "n");
				break;
			case KeyEvent.VK_O:
				name = (name + "o");
				break;
			case KeyEvent.VK_P:
				name = (name + "p");
				break;
			case KeyEvent.VK_Q:
				name = (name + "q");
				break;
			case KeyEvent.VK_R:
				name = (name + "r");
				break;
			case KeyEvent.VK_S:
				name = (name + "s");
				break;
			case KeyEvent.VK_T:
				name = (name + "t");
				break;
			case KeyEvent.VK_U:
				name = (name + "u");
				break;
			case KeyEvent.VK_V:
				name = (name + "v");
				break;
			case KeyEvent.VK_W:
				name = (name + "w");
				break;
			case KeyEvent.VK_X:
				name = (name + "x");
				break;
			case KeyEvent.VK_Y:
				name = (name + "y");
				break;
			case KeyEvent.VK_Z:
				name = (name + "z");
				break;
			case KeyEvent.VK_SPACE:
				name = (name + " ");
				break;
			case KeyEvent.VK_UNDERSCORE:
				name = (name + "_");
				break;
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
				if(name.length() > 0)
				name = (name.substring(0, name.length()-1));
				break;
			case KeyEvent.VK_ENTER:
				gamestate = Gamestate.SCORE;
			}
			break;
		case SCORE:
			switch(e.getKeyCode()){
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				gamestate = Gamestate.MENU;
				break;
			}
			break;
		case HELP:
			switch(e.getKeyCode()){
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				gamestate = Gamestate.MENU;
				break;
			}
			break;
		case SHOP:
			switch(e.getKeyCode()){
			case KeyEvent.VK_ESCAPE:
				gamestate = Gamestate.MENU;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				shopItemSelected++;
				if(shopItemSelected == shopItems.length){
					shopItemSelected = 0;
				}
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				shopItemSelected--;
				if(shopItemSelected < 0){
					shopItemSelected = shopItems.length-1;
				}
				break;
			case KeyEvent.VK_ENTER:
				switch(shopItemSelected){
				case 0:
					if(money >= shopItems[0].getPrice() && shopItems[0].getAmount() < shopItems[0].getMaxAmount()){
						money-=shopItems[0].getPrice();
						shopItems[0].purchase();
					}
					break;
				case 1:
					if(money >= shopItems[1].getPrice() && shopItems[0].getAmount() < shopItems[0].getMaxAmount()){
						money-=shopItems[1].getPrice();
						shopItems[1].purchase();
					}
					break;
				}
				break;
			}
			break;
		}
	}



	@Override
	public void keyReleased(KeyEvent e) {
		switch(gamestate){
		case GAME:
			switch(e.getKeyCode()){
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				rightPressed = false;
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				leftPressed = false;
				break;
			}
			break;
		case MENU:
			
			break;
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
		
	}
}
