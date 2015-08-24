package com.pokeyone.ludum33.entity;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

import com.pokeyone.ludum33.Game;

public class Enemy {

	Random rand = new Random();
	int points;
	double speed = 1;
	
	private Color color = new Color(rand.nextInt(150), rand.nextInt(150), rand.nextInt(150));
	
	private Point loc = new Point(1200, 570);
	
	public Enemy(int playerpoints, double speed){
		points = rand.nextInt(playerpoints+20);
		this.speed = speed;
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
	
	public Color getColor(){
		return color;
	}
}
