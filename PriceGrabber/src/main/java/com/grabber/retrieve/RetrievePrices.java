package com.grabber.retrieve;

import java.util.ArrayList;
import java.util.Random;

import com.grabber.IO.IO;
import com.grabber.models.Item;

public class RetrievePrices extends Thread{
	
	public void run(){
		
		IO io = new IO();
		while(true){
			
			try {BuildPrices.loadBlacklist("C:\\deploys\\RotMGPriceGrabber\\blacklist.txt");}catch (Exception e){e.printStackTrace();} 
			System.out.println("[INFO] Loaded Item Blacklist");
			ArrayList<Item> items = BuildPrices.getPrices();
			if(items==null){
				System.exit(0);
			}
			
			try {
				//CreateTables.Create(items);
				io.updatePricesNew(items);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				Random r = new Random();
				int wait = r.nextInt(10000 - 1000) + 1000;
				System.out.println("[INFO] Sleeping For "+wait+" millis (random)");
				Thread.sleep(wait);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args){
		RetrievePrices r = new RetrievePrices();
		r.run();
	}
}
