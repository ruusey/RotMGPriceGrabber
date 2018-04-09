package com.grabber.models;

public class Item {
	public int id;
	public double valueDef;
	public String name;
	public boolean potion;
	public Item(int id, double valueDef, String name, boolean potion){
		this.id=id;
		this.valueDef=valueDef;
		this.name=name;
		this.potion=potion;
	}
	
}
