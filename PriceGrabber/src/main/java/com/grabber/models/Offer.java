package com.grabber.models;

public class Offer {
	public int id;
	public int idQuantity;
	public int otherID;
	public int otherIDQuantity;

	public Offer(int id, int idQuantity, int otherID, int otherIDQuantity){
		this.id=id;
		this.idQuantity=idQuantity;
		this.otherID=otherID;
		this.otherIDQuantity=otherIDQuantity;
	}
}
