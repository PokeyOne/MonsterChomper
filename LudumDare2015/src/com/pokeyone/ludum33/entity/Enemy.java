package com.pokeyone.ludum33.entity;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

import com.pokeyone.ludum33.Game;

public class Enemy {

	public enum PowerType {
		NONE, PLUS_SIZE, PLUS_SPEED
	}
	
	private Random rand = new Random();
	private int points;
	private double speed = 3;
	
	private boolean isPower = false;
	private PowerType powerType = PowerType.NONE;
	
	private Color color = new Color(rand.nextInt(150), rand.nextInt(150), rand.nextInt(150));
	
	private Point loc = new Point(1200, 570);
	
	public Enemy(int playerpoints, double speed){
		points = rand.nextInt((int)(playerpoints*1.5+1));
		this.speed = speed;
		
		if(rand.nextInt(25) == 1){
			isPower = true;
			
			int n = rand.nextInt(2);
			switch(n){
			case 0:
				powerType = PowerType.PLUS_SIZE;
				break;
			case 1:
				powerType = PowerType.PLUS_SPEED;
				break;
			}
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
	
	public Color getColor(){
		return color;
	}
	
	public boolean isPowerUp(){
		return isPower;
	}
	
	public PowerType getPowerType(){
		return powerType;
	}
}
