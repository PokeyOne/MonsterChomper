package com.pokeyone.ludum33.shop;

public class ShopItem {
	
	private int amountMax;
	private int amount;
	private int price;
	private String name;
	private double scale;
	
	public ShopItem(int amountMax, int price, double scale, String name){
		this.amountMax = amountMax;
		this.price = price;
		this.name = name;
		this.scale = scale;
	}
	
	public int getAmount(){
		return amount;
	}
	
	public void setAmount(int amo){
		amount = amo;
	}
	
	public int getMaxAmount(){
		return amountMax;
	}
	
	public int getPrice(){
		return price;
	}
	
	public void setPrice(int money){
		price = money;
	}
	
	public String getName(){
		return name;
	}
	
	public void purchase(){
		amount++;
		price*=scale;
	}
}
