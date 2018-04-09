package com.grabber.models;

import java.sql.Timestamp;

public class TimeOffer {
	public Timestamp timestamp;
	public double valueDef;
	public String name;
	
	public TimeOffer(Timestamp t, double valueDef, String name){
		
	}
	public TimeOffer(){
		
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp t) {
		this.timestamp = t;
	}
	public double getValueDef() {
		return valueDef;
	}
	public void setValueDef(double valueDef) {
		this.valueDef = valueDef;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
