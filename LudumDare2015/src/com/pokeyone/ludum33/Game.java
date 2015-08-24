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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.pokeyone.ludum33.data.Score;
import com.pokeyone.ludum33.entity.Enemy;
import com.pokeyone.ludum33.entity.Player;

public class Game extends JPanel implements Runnable, KeyListener{

	Thread thread = new Thread(this);
	
	private enum Gamestate {
		MENU, GAME, SCORE, NAME, HELP
	}
	private Gamestate gamestate = Gamestate.NAME;
	
	private String[] menuItems = {"Play", "Quit", "Help"};
	private int menuItemSelected = 0;
	
	private Score[] scores = new Score[10];
	
	private Random random = new Random();
	
	private Image imageBackground;
	private Image imageGround;
	private Image imageButton;
	private Image imageButtonSelected;
	private Image imagePlayer;
	
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
			
			imageBackground = new ImageIcon("res/Background.jpg").getImage();
			imageButton = new ImageIcon("res/Button.png").getImage();
			imageButtonSelected = new ImageIcon("res/ButtonSelected.png").getImage();
			
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Game.class.getClassLoader().getResourceAsStream("/AldotheApache.ttf")));
		}catch(NullPointerException e){
			e.printStackTrace();
			System.exit(404);
		}catch(IOException e){
			e.printStackTrace();
			System.exit(404);
		}catch(FontFormatException e){
			e.printStackTrace();
			System.exit(404);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		thread.start();
	}
	
	private void loadScores() throws URISyntaxException, IOException{
		File file = new File("res/scores.save");
		FileReader fr = new FileReader(file);
		BufferedReader bfr = new BufferedReader(fr);
		
		for(int i = 0; i < 10; i++){
			String str = bfr.readLine();
			if(str == null || str == " " || str == "\n" || str == ""){
				break;
			}else{
				System.out.println("line " + i + " is equal to " + str);
			}
			String[] strings = str.split(" ", 2);
			
			try{
				scores[i] = new Score(Integer.valueOf(strings[0]), strings[1]);
				System.out.println("loaded score of: " + scores[i].getScore() + " From the player: " + scores[i].getName());
			}catch(NumberFormatException e){
				
			}
		}
		
		bfr.close();
		
	}
	
	private void newScore(String name, int amo){
		Score score = new Score(amo, name);
		
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
	}
	
	public void exit(){
		try {
			saveScores();
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	private void tick(){
		if(gamestate == Gamestate.GAME){
			if(rightPressed){
				if(player.getX() < getWidth())
					player.addX(3);
			}
			if(leftPressed){
				if(player.getX() > 0)
					player.addX(-3);
			}
			if(player.getDestY()+80 < 400){
				player.addDestY(2);
			}
			if(player.getDestY() < player.getY()){
				player.addY(-3);
			}else if(player.getDestY() > player.getY()){
				player.addY(2);
			}
			
			if(enemy == null){
				enemySpeed = Math.floor(player.getEnemiesDefeated()/50.0)+2;
				enemy = new Enemy(player.getPoints(), enemySpeed);
				System.out.println("speed is: " + enemySpeed);
			}
			
			if(enemy.getX() < -80){
				enemy = null;
			}
			
			if(enemy != null && player.getX()+90 > enemy.getX() && player.getX()+90 < enemy.getX()+80
					&& player.getY()+90 > 330 && player.getY()+90 < 411){
				if(player.getPoints() > enemy.getPoints()){
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
			for(int i = 0; i < menuItems.length; i++){
				if(menuItemSelected != i){
					g.drawImage(imageButton, getWidth()/2-128*2, getHeight()/2-50*2+(70*i*2), 256*2, 64*2, null);
					g.setColor(new Color(100, 100, 100));
					g.drawString(menuItems[i], getWidth()/2-128*2+42, getHeight()/2-50*2+(70*i*2)+57*2);
					g.setColor(new Color(250, 250, 250));
					g.drawString(menuItems[i], getWidth()/2-128*2+40, getHeight()/2-50*2+(70*i*2)+55*2);
				}else{
					g.drawImage(imageButtonSelected, getWidth()/2-133*2, getHeight()/2-50*2+(70*i*2), 256*2+20, 64*2, null);
					g.setColor(new Color(100, 100, 100));
					g.drawString(menuItems[i], getWidth()/2-128*2+42, getHeight()/2-50*2+(70*i*2)+57*2);
					g.setColor(new Color(250, 250, 250));
					g.drawString(menuItems[i], getWidth()/2-128*2+40, getHeight()/2-50*2+(70*i*2)+55*2);
				}
			}
			break;
		case GAME:
			g.setFont(new Font("Aldo the Apache", Font.PLAIN, 60));
			g.drawImage(imageGround, 0, getHeight()-80, getWidth(), 80, null);
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
		}
	}
	
	public void run(){
		while(true){
			tick();
			repaint();
			try{
				Thread.sleep(5);
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
					player = new Player();
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
				}
				break;
			}
			break;
		case GAME:
			switch(e.getKeyCode()){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if(player.getDestY()+80 >= 400)
					player.addDestY(-250);
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
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_DELETE:
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
