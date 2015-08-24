package com.pokeyone.ludum33;

import javax.swing.JFrame;

public class Frame extends JFrame{

	Game game = new Game();
	
	public Frame(){
		add(game);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Monster Chomper");
		setVisible(true);
	}
	
	public static void main(String[] args){
		new Frame();
	}
}
