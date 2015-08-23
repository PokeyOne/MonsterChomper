package com.pokeyone.ludum33.entity;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

import com.pokeyone.ludum33.Game;

public class Enemy {

	Random rand = new Random();
	int textureID = rand.nextInt(1);
	int points;
	double speed = 1;
	
	private Image imageEnemy;
	
	private Point loc = new Point(1200, 570);
	
	public Enemy(int playerpoints, double speed){
		points = rand.nextInt(playerpoints+20);
		this.speed = speed;
		
		try{
			switch(textureID){
			case 0:
				imageEnemy = new ImageIcon(Game.class.getResource("/Enemy1.png")).getImage();
				break;
			case 1:
				imageEnemy = new ImageIcon(Game.class.getResource("/Enemy1.png")).getImage();
				break;
			case 2:
				imageEnemy = new ImageIcon(Game.class.getResource("/Enemy1.png")).getImage();
				break;
			case 3:
				imageEnemy = new ImageIcon(Game.class.getResource("/Enemy1.png")).getImage();
				break;
			}
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	
	public void tick(){
		loc.x -= speed;
	}
	
	public int getX(){
		return loc.x;
	}
	
	public int getPoints(){
		return points;
	}
	
	public Image getImage(){
		return imageEnemy;
	}
}
