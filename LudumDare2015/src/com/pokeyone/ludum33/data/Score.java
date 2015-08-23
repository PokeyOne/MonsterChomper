package com.pokeyone.ludum33.data;

public class Score {

	private int score;
	private String name;
	
	public Score(int score, String name){
		this.score = score;
		this.name = name;
	}
	
	public int getScore(){
		return score;
	}
	
	public String getName(){
		return name;
	}
	
	public String getSaveString(){
		return score + " " + name + " \n";
	}
}
