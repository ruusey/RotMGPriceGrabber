package com.grabber.retrieve;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.*;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.grabber.models.Item;
import com.grabber.models.Offer;
import com.owlike.genson.Genson;

public class BuildPrices {
	
	//Author ruusey
	//Average Potion Price Grabber for Realmeye.com
	//Item Implication Coming Soon
	
	//Where the offers to load are
	public static String offerURL = "https://www.realmeye.com/current-offers" ;
	//Url For selling offers
	public static String sellURL = "https://www.realmeye.com/offers-to/sell/" ;
	//Url for buying offers
	public static String buyURL = "https://www.realmeye.com/offers-to/buy/" ;
	//necessary array lists
	public static ArrayList<String> log = null;
	public static ArrayList<String> blacklist = new ArrayList<String>();
	public static ArrayList<Item> potions = new ArrayList<Item>();
	public static ArrayList<Item> items = new ArrayList<Item>();
	public static ArrayList<Integer> potionIds = new ArrayList<Integer>();
	public static ArrayList<Double> potionPrices = new ArrayList<Double>();
	//IDs for pots
	public static int defId = 2592;
	public static int attId = 2591;
	public static int spdId = 2593;
	public static int dexId = 2636;
	public static int wisId = 2613;
	public static int vitId = 2612;
	public static int manaId = 2794;
	public static int lifeId = 2793;
	
	//Hashtable to convert item ids to name String if you need to
	public static Hashtable<Integer, String> idToName = new Hashtable<Integer, String>();
	public static Hashtable<String, Integer> nameToId = new Hashtable<String, Integer>();
	
	public static void loadBlacklist(String path) throws FileNotFoundException, IOException{
		
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
		    String line;
		    System.out.println("[INFO] Loading Item Blacklist...");
		    while ((line = br.readLine()) != null) {
		    	System.out.println("[INFO] Loaded Blacklisted Item: "+line);
		       blacklist.add(line);
		    }
		    System.out.println("Done");
		    br.close();
		}
	}
	public static ArrayList<Item> buildItems(){
		
			potionIds.add(defId);potionIds.add(attId);potionIds.add(spdId);potionIds.add(dexId);
			potionIds.add(wisId);potionIds.add(vitId);potionIds.add(manaId);potionIds.add(lifeId);
		
		
		ArrayList<Item> res = new ArrayList<Item>();
		Document doc = null;
		try {
			doc = Jsoup.connect(offerURL).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0").header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("upgrade-insecure-requests", "1").get();
		} catch (IOException e) {
			e.printStackTrace();
			return res;
		}
		
		Elements items = doc.getElementsByClass("item-wrapper");
		System.out.println("got: "+items.size());
		for(Element e:items){
			if(e.hasClass("disabled")) continue;
			Elements children = e.children();
			
			Element spanTitle = null;
			try {
				spanTitle = children.get(0).firstElementSibling().child(0);
			} catch (Exception e2) {
				break;
			}
			String itemName = spanTitle.attr("title");
			System.out.println(itemName);
			String hrefId = children.get(1).attr("href");
			
			int itemIdInt = -1;
			try {
				String itemId = hrefId.split("/")[3];
				itemIdInt = Integer.parseInt(itemId);
			}catch(Exception e1) {
				System.out.println("Could not parse itemId for item: "+itemName);
				continue;
			}
			if(itemIdInt!=-1) {
				nameToId.put(itemName, itemIdInt);
				idToName.put(itemIdInt, itemName);
				if (potionIds.contains(itemIdInt) && !blacklist.contains(itemName)) {
					Item i = new Item(itemIdInt, -1.0, itemName, true);
					res.add(i);
					potions.add(i);
				} else if (!blacklist.contains(itemName)) {
					Item i = new Item(itemIdInt, -1.0, itemName, false);
					res.add(i);
				}
			}
		}
		
		return res;
	}
	
	public static ArrayList<Offer> sellPrice(Item i){
		ArrayList<Offer> res = new ArrayList<Offer>();
		int id = 0;
		int idQuantity = 0;
		int otherID = 0;
		int otherIDQuantity = 0;
		Document doc = null;
		try {
			doc = Jsoup.connect(sellURL+i.id+"/"+defId).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0").header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("upgrade-insecure-requests", "1").get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Elements offers = doc.getElementsByTag("tr");
		
		for(Element e : offers){
			for( Element l : e.getElementsByTag("td")){
				if((l.nextElementSibling().select(".item-static").size()>1) || l.select(".item-static").size()>1 ){
					break;
				}else{
					id = Integer.parseInt(l.select("span.item").attr("data-item"));
					idQuantity = Integer.parseInt(l.select(".item-quantity-static").text().substring(1));
					otherID = Integer.parseInt(l.nextElementSibling().select("span.item").attr("data-item"));
					otherIDQuantity = Integer.parseInt(l.nextElementSibling().select(".item-quantity-static").text().substring(1));
					Offer o = new Offer(id,idQuantity,otherID,otherIDQuantity);
					res.add(o);
				}
				break;
			}
		}
		return res;
		
	}
	public static ArrayList<Offer> sellPriceItem(Item i) {
		ArrayList<Offer> res = new ArrayList<Offer>();
		int id = 0;
		int idQuantity = 0;
		int otherID = 0;
		int otherIDQuantity = 0;
		Document doc = null;
		try {
			doc = Jsoup.connect(sellURL+i.id+"/pots").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0").header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("upgrade-insecure-requests", "1").get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Elements offers = doc.getElementsByTag("tr");
		
		for(Element e : offers){
			for( Element l : e.getElementsByTag("td")){
				if((l.nextElementSibling().select(".item-static").size()>1) || l.select(".item-static").size()>1 ){
					break;
				}else{
					id = Integer.parseInt(l.select("span.item").attr("data-item"));
					idQuantity = Integer.parseInt(l.select(".item-quantity-static").text().substring(1));
					otherID = Integer.parseInt(l.nextElementSibling().select("span.item").attr("data-item"));
					otherIDQuantity = Integer.parseInt(l.nextElementSibling().select(".item-quantity-static").text().substring(1));
					Offer o = new Offer(id,idQuantity,otherID,otherIDQuantity);
					res.add(o);
				}
				break;
			}
		}
		return res;
		
	}
	public static ArrayList<Offer> buyPrice(Item i){
		ArrayList<Offer> res = new ArrayList<Offer>();
		int id = 0;
		int idQuantity = 0;
		int otherID = 0;
		int otherIDQuantity = 0;
		Document doc = null;
		try {
			doc = Jsoup.connect(buyURL+i.id+"/"+defId).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0").header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("upgrade-insecure-requests", "1").get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Elements offers = doc.getElementsByTag("tr");
		
		for(Element e : offers){
			for( Element l : e.getElementsByTag("td")){
				if((l.nextElementSibling().select(".item-static").size()>1) || l.select(".item-static").size()>1 ){
					break;
				}else{
					id = Integer.parseInt(l.select("span.item").attr("data-item"));
					idQuantity = Integer.parseInt(l.select(".item-quantity-static").text().substring(1));
					otherID = Integer.parseInt(l.nextElementSibling().select("span.item").attr("data-item"));
					otherIDQuantity = Integer.parseInt(l.nextElementSibling().select(".item-quantity-static").text().substring(1));
					Offer o = new Offer(id,idQuantity,otherID,otherIDQuantity);
					res.add(o);
				}
				break;
			}
		}
		return res;
	}
	public static ArrayList<Offer> buyPriceItem(Item i){
		ArrayList<Offer> res = new ArrayList<Offer>();
		int id = 0;
		int idQuantity = 0;
		int otherID = 0;
		int otherIDQuantity = 0;
		Document doc = null;
		try {
			doc = Jsoup.connect(buyURL+i.id+"/pots").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0").header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").header("upgrade-insecure-requests", "1").get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Elements offers = doc.getElementsByTag("tr");
		
		for(Element e : offers){
			for( Element l : e.getElementsByTag("td")){
				if((l.nextElementSibling().select(".item-static").size()>1) || l.select(".item-static").size()>1 ){
					break;
				}else{
					id = Integer.parseInt(l.select("span.item").attr("data-item"));
					idQuantity = Integer.parseInt(l.select(".item-quantity-static").text().substring(1));
					otherID = Integer.parseInt(l.nextElementSibling().select("span.item").attr("data-item"));
					otherIDQuantity = Integer.parseInt(l.nextElementSibling().select(".item-quantity-static").text().substring(1));
					Offer o = new Offer(id,idQuantity,otherID,otherIDQuantity);
					res.add(o);
				}
				break;
			}
		}
		return res;
	}
	public static double avgPrice(ArrayList<Offer> list){
		if(list==null){
			return -1.0;
		}
		
		//Average the Prices based on number of def being sold/bought for the item
		double res = 0.0;
		for(Offer o:list){
			if(o.id==defId){
				res+= ((double)o.idQuantity/(double)o.otherIDQuantity);
			}else{
				res+= ((double)o.otherIDQuantity/(double)o.idQuantity);
			}
		}
		return res/list.size();
	}
	public static double avgPriceDirty(ArrayList<Offer> list){
		if(list==null){
			return -1.0;
		}
		//Calculate the average with no outliers removed
		//Average the Prices based on number of def being sold/bought for the item
		double res = 0.0;
		for(Offer o:list){
			if(o.id==defId){
				res+= ((double)o.idQuantity/(double)o.otherIDQuantity);
			}else{
				res+= ((double)o.otherIDQuantity/(double)o.idQuantity);
			}
		}
		return res/list.size();
	}
	
	public static double[] toDoubleArray(ArrayList<Offer> list){
		if(list==null) return null;
		double[] res = new double[list.size()];
		for(int x = 0; x < list.size(); x++){
			if(list.get(x).id==defId){
				res[x]=((double)list.get(x).idQuantity/(double)list.get(x).otherIDQuantity);
			}else{
				res[x]=((double)list.get(x).otherIDQuantity/(double)list.get(x).idQuantity);	
			}
		}
		return res;
	}
	public static void writeReport(ArrayList<String> contents){
		try{
			PrintWriter writer = new PrintWriter("report@"+System.nanoTime()+".txt", "UTF-8");
			writer.write("RotMG Price Builder Report, All Numbers In Terms Of DEF");
			writer.println();
			for(String s:contents){
				writer.write(s);
				writer.println();
			}
			System.out.println("Done, Closing Writer");
			writer.close();
		}catch(Exception e){
			
		}
	}
	public static ArrayList<Offer> potionsOnlyBuy(Item i){
		return null;
		
	}
	public static ArrayList<Offer> potionsOnlySell(Item i){
		return null;
		
	}
	public static ArrayList<Double> potionsOnly(){
		StandardDeviation d = new StandardDeviation();
		Mean m = new Mean();
		ArrayList<Double> res = new ArrayList<Double>();
//		if(potions.get(0).id==defId){
//			potions.remove(0);
//		}
		
		for(Item i:potions){
			ArrayList<Offer> buy = buyPrice(i);
			ArrayList<Offer> sell = sellPrice(i);
			
				double[] buyList = toDoubleArray(buy);
				double[] sellList = toDoubleArray(sell);
				
					double buySTD = d.evaluate(buyList);
					double sellSTD = d.evaluate(sellList);
					
						double buyMean = m.evaluate(buyList);
						double sellMean = m.evaluate(sellList);
							
						ArrayList<Double> buyClean=removeSTDVAR(buyList, buySTD, buyMean);
						ArrayList<Double> sellClean=removeSTDVAR(sellList, sellSTD, sellMean);
						
						res.add((average(buyClean)+average(sellClean))/2);
		}
		return res;
	}
	public static double complexPrice(Offer o){
		
			if(potionIds.contains(o.id)){
				String pot = idToName.get(o.id);
				if(pot.equals("Potion of Attack")){
					return ((double)o.idQuantity*(double)potionPrices.get(0))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Speed")){
					return ((double)o.idQuantity*(double)potionPrices.get(1))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Dexterity")){
					return ((double)o.idQuantity*(double)potionPrices.get(2))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Wisdom")){
					return ((double)o.idQuantity*(double)potionPrices.get(3))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Vitality")){
					return ((double)o.idQuantity*(double)potionPrices.get(4))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Mana")){
					return ((double)o.idQuantity*(double)potionPrices.get(5))/(double)o.otherIDQuantity;
				}
				if(pot.equals("Potion of Life")){
					return ((double)o.idQuantity*(double)potionPrices.get(6))/(double)o.otherIDQuantity;
				}
			}
			if(potionIds.contains(o.otherID)){
				String pot = idToName.get(o.otherID);
				if(pot.equals("Potion of Attack")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(0))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Speed")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(1))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Dexterity")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(2))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Wisdom")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(3))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Vitality")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(4))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Mana")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(5))/(double)o.idQuantity;
				}
				if(pot.equals("Potion of Life")){
					return ((double)o.otherIDQuantity*(double)potionPrices.get(6))/(double)o.idQuantity;
				}
				

			}
			
		
		return 0.0;
	}
	
	public static ArrayList<Double> removeSTDVAR(double[] list, double std, double mean){
		ArrayList<Double> clean = new ArrayList<Double>();
		for(int x = 0; x<list.length; x++){
			if(Math.abs(list[x]-mean)<=std){
				clean.add(list[x]);
			}
		}
		return clean;
		
	}
	public static double[] complexPricesList(ArrayList<Offer> list){
		double[] d = new double[list.size()];
		for(Offer o:list){
			if(o.id==defId || o.otherID==defId){
				if(o.id==defId){
					d[list.indexOf(o)]= ((double)o.idQuantity/(double)o.otherIDQuantity);
				}else{
					d[list.indexOf(o)]= ((double)o.otherIDQuantity/(double)o.idQuantity);
				}
			}else{
				d[list.indexOf(o)]=complexPrice(o);
			}
		}
		return d;
	}
	
	
	public static double average(ArrayList<Double> list){
		double res = 0.0;
		for(Double d:list){
			res+=d;
		}
		return res/(double)list.size();
	}
	public static double[] convertDoubles(List<Double> doubles)
	{
	    double[] ret = new double[doubles.size()];
	   for(int x = 0; x<doubles.size();x++){
		   ret[x]=doubles.get(x);
	   }
	    return ret;
	}
	public static void main(String[] args){
		try {loadBlacklist("C:\\tmp\\blacklist.txt");}catch (Exception e){e.printStackTrace();} 
		try {
			enableSSLSocket();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		items = buildItems();
		//remove def;
		items.remove(0);
		Genson gen = new Genson();
		for(Item item : items) {
			System.out.println(gen.serialize(item));
		}
//		try {
//			//CreateTables.Create(items);
//			getPrices();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
 
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
 
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
 
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
	public static ArrayList<Item> getPrices(){
		//nullify all existing lists and hashes
		if(items.size()==0){
			items=buildItems();
		}
		potions.clear();
		items.clear();
		potionPrices.clear();
		potionIds.clear();
		idToName.clear();
		nameToId.clear();
		
		items = buildItems();
		if(items==null){
			return null;
		}
		log = new ArrayList<String>();
		//items.remove(0);
		
		StandardDeviation d = new StandardDeviation();
		Mean m = new Mean();
		ArrayList<Item> failed = new ArrayList<Item>();
		for(Item i:items){
			if(i.potion){
				if(i.id==defId) {
					potionPrices.add(1.0);
					continue;
				}
				log.add("[INFO] Retrieving Offers For "+i.name);
				System.out.println("[INFO] Retrieving Offers For "+i.name);
				ArrayList<Offer> buy = buyPrice(i);
				ArrayList<Offer> sell = sellPrice(i);
				if(buy == null || sell == null){
					//If unable to retrieve price data do not update.
					failed.add(i);
					continue;
				}
				
					double[] buyList = toDoubleArray(buy);
					double[] sellList = toDoubleArray(sell);
					
						double buySTD = d.evaluate(buyList);
						double sellSTD = d.evaluate(sellList);
						
							double buyMean = m.evaluate(buyList);
							double sellMean = m.evaluate(sellList);
								
							ArrayList<Double> buyClean=removeSTDVAR(buyList, buySTD, buyMean);
							ArrayList<Double> sellClean=removeSTDVAR(sellList, sellSTD, sellMean);
							double sellAVG = average(sellClean);
							double buyAVG = average(buyClean);
							if(Double.isNaN(sellAVG)) {
								sellAVG=buyAVG/2.0;
							}
							if(Double.isNaN(buyAVG)) {
								buyAVG=sellAVG/2.0;
							}
							double buySellAVG =(sellAVG+buyAVG)/2; 
							i.valueDef=buySellAVG;
							potionPrices.add(i.valueDef);
							log.add("[INFO] "+i.name+ " B>: "+buyAVG+", S>: "+sellAVG+" B/S: "+buySellAVG);
							System.out.println("[INFO] "+i.name+ " B>: "+buyAVG+", S>: "+sellAVG+" B/S: "+buySellAVG);

			}else{
				log.add("[INFO] Retrieving Offers For "+i.name);
				System.out.println("[INFO] Retrieving Offers For "+i.name);
				ArrayList<Offer> buy = buyPriceItem(i);
				ArrayList<Offer> sell = sellPriceItem(i);
				if(buy == null || sell == null){
					//If unable to retrieve price data do not update.
					failed.add(i);
					continue;
				}
				if(buy.size()<2 && sell.size()<2) {
					failed.add(i);
					continue;
				}
				ArrayList<Double> buyAdj = new ArrayList<Double>();
				ArrayList<Double> sellAdj = new ArrayList<Double>();
				
				for(Offer o: buy){
					buyAdj.add(complexPrice(o));
				}
				for(Offer o: sell){
					sellAdj.add(complexPrice(o));
				}
					double[] buyList = convertDoubles(buyAdj);
					double[] sellList = convertDoubles(sellAdj);
					
						double buySTD = d.evaluate(buyList);
						double sellSTD = d.evaluate(sellList);
						
							double buyMean = m.evaluate(buyList);
							double sellMean = m.evaluate(sellList);
								
							ArrayList<Double> buyClean=removeSTDVAR(buyList, buySTD, buyMean);
							ArrayList<Double> sellClean=removeSTDVAR(sellList, sellSTD, sellMean);
							
							
							double sellAVG = average(sellClean);
							double buyAVG = average(buyClean);
							double buySellAVG =(sellAVG+buyAVG)/2; 
							i.valueDef=buySellAVG;
							log.add("[INFO] "+i.name+ " B>: "+buyAVG+", S>: "+sellAVG+" B/S: "+buySellAVG);
							System.out.println("[INFO] "+i.name+ " B>: "+buyAVG+", S>: "+sellAVG+" B/S: "+buySellAVG);
							
							if(Double.isNaN(i.valueDef)){
								failed.add(i);
								i.valueDef=-1;
							}
			}
			
		}
		for(Item i: failed){
			log.add("[ERROR] Failed To Retrieve An Accurate Price For "+i.name);
			System.out.println("[ERROR] Failed To Retrieve An Accurate Price For "+i.name);
			items.remove(i);
		}
		//writeReport(log);
		return items;
	}
	
}