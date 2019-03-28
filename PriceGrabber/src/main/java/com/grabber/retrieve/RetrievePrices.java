package com.grabber.retrieve;

import java.util.ArrayList;
import java.util.Random;

import com.grabber.IO.IO;
import com.grabber.models.Item;

public class RetrievePrices extends Thread{
	
	public void run(){
		
		
		while(true){
			
			try {BuildPrices.loadBlacklist("C:\\tmp\\blacklist.txt");}catch (Exception e){e.printStackTrace();} 
			System.out.println("[INFO] Loaded Item Blacklist");
			ArrayList<Item> items = BuildPrices.getPrices();
			if(items==null){
				System.exit(0);
			}
			
			try {
				//CreateTables.Create(items);
				IO.updatePricesNew(items);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try{
				Random r = new Random();
				int wait = r.nextInt(100000 - 10000) + 300000;
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
