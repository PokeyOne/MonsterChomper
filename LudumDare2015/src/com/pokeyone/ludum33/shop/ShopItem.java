package com.pokeyone.ludum33.shop;

public class ShopItem {
	
	private int amountMax;
	private int amount;
	private int price;
	private String name;
	private int scale;
	
	public ShopItem(int amountMax, int price, int scale, String name){
		this.amountMax = amountMax;
		this.price = price;
		this.name = name;
		this.scale = scale;
	}
}
