package com.pokeyone.ludum33.entity;

import java.awt.Point;

public class Player {

	private int points = 20;
	private int enemiesDefeated = 0;
	
	private Point loc = new Point(0, 0);
	private int ySpeed = 0;
	
	private String name = "BetaTester";
	
	public Player(int startBonus){
		points+=startBonus;
	}
	
	public Player(){
		
	}
	
	public int getPoints(){
		return points;
	}
	
	public void addPoints(int points){
		this.points += points;
	}
	
	public int getX(){
		return loc.x;
	}
	
	public int getY(){
		return loc.y;
	}
	
	public void addX(int amo){
		loc.x+=amo;
	}
	
	public void addY(int amo){
		loc.y+=amo;
	}
	
	public void setX(int amo){
		loc.x=amo;
	}
	
	public void setY(int amo){
		loc.y=amo;
	}
	
	public int getYSpeed(){
		return ySpeed;
	}
	
	public void addYSpeed(int amount){
		ySpeed += amount;
	}
	
	public void setYSpeed(int amount){
		ySpeed = amount;
	}
	
	public void killedEnemy(){
		enemiesDefeated++;
	}
	
	public int getEnemiesDefeated(){
		return enemiesDefeated;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void addKills(int amo){
		enemiesDefeated += amo;
	}
}
