package com.pokeyone.ludum33;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.pokeyone.ludum33.data.Score;
import com.pokeyone.ludum33.entity.Enemy;
import com.pokeyone.ludum33.entity.Player;

public class Game extends JPanel implements Runnable, KeyListener{

	Thread thread = new Thread(this);
	private int ticks = 0;
	
	private enum Gamestate {
		MENU, GAME, SCORE
	}
	private Gamestate gamestate = Gamestate.MENU;
	
	private String[] menuItems = {"Play", "Quit"};
	private int menuItemSelected = 0;
	
	private Score[] scores = new Score[10];
	
	private Image imageBackground;
	private Image imageGround;
	private Image imageButton;
	private Image imageButtonSelected;
	private Image imagePlayer;
	
	private boolean rightPressed = false;
	private boolean leftPressed = false;
	
	private Player player = new Player();
	private Enemy enemy;
	private double enemySpeed;
	
	private Font font;
	
	public Game(){
		setPreferredSize(new Dimension((int)(640*1.5), (int)(480*1.5)));
		setFocusable(true);
		addKeyListener(this);
		
		try{
			loadScores();
			
			imageBackground = new ImageIcon(Game.class.getResource("/Background.jpg")).getImage();
			imageGround = new ImageIcon(Game.class.getResource("/ground.jpg")).getImage();
			imageButton = new ImageIcon(Game.class.getResource("/Button.png")).getImage();
			imageButtonSelected = new ImageIcon(Game.class.getResource("/ButtonSelected.png")).getImage();
			imagePlayer = new ImageIcon(Game.class.getResource("/Character.png")).getImage();
			
			font = Font.createFont(Font.TRUETYPE_FONT, Game.class.getResourceAsStream("/AldotheApache.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Game.class.getResourceAsStream("/AldotheApache.ttf")));
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
		File file = new File(Game.class.getResource("/scores.save").toURI());
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
				}
			}else{
				scores[i] = score;
				break;
			}
		}
	}
	
	private void saveScores() throws IOException, URISyntaxException {
		File file = new File(Game.class.getResource("/scores.save").toURI());
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
			if(player.getDestY()+70 < 400){
				player.addDestY(1);
			}
			if(player.getDestY() < player.getY()){
				player.addY(-2);
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
			
			if(enemy != null && player.getX()+80 > enemy.getX() && player.getX()+80 < enemy.getX()+80
					&& player.getY()+80 > 330 && player.getY()+80 < 411){
				if(player.getPoints() > enemy.getPoints()){
					enemy = null;
					player.killedEnemy();
					player.addPoints((int)Math.floor(player.getEnemiesDefeated()/10.0)+1);
				}else{
					newScore("AlphaTester", player.getPoints());
					gamestate = Gamestate.SCORE;
				}
			}
			
			if(enemy != null)
				enemy.tick();
		}
	}
	
	public void paint(Graphics g){
		g.drawImage(imageBackground, 0, 0, getWidth(), getHeight(), null);
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
			g.drawImage(imagePlayer, player.getX(), player.getY()+240, 80, 80, null);
			g.setColor(Color.WHITE);
			g.drawString("Size: " + player.getPoints() + "        Killed: " + player.getEnemiesDefeated(), 13, 73);
			g.setColor(Color.BLACK);
			g.drawString("Size: " + player.getPoints() + "        Killed: " + player.getEnemiesDefeated(), 10, 70);
			
			try{
				g.drawImage(enemy.getImage(), enemy.getX(), 570, 80, 80, null);
				g.setFont(new Font("Aldo the Apache", Font.PLAIN, 40));
				g.drawString(enemy.getPoints() + "", enemy.getX(), 570);
			}catch(NullPointerException e){
				
			}
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
					gamestate = Gamestate.GAME;
					break;
				case 1:
					exit();
					break;
				}
				break;
			}
			break;
		case GAME:
			switch(e.getKeyCode()){
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if(player.getDestY()+70 >= 400)
					player.addDestY(-200);
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
		case SCORE:
			switch(e.getKeyCode()){
			case KeyEvent.VK_ENTER:
			case KeyEvent.VK_ESCAPE:
				exit();
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
