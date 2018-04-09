package com.emailgrab;

import java.io.IOException;
import java.util.ArrayList;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

;

public class GrabEmail {
	public ArrayList<String> grabEmail(String URL) throws Exception{
		if(URL.endsWith("/")){
			URL=URL.substring(0,URL.length()-1);
		}
		Document doc = null;
		try{
			enableSSLSocket();
			doc = getHTML(URL);
			ArrayList<String> contactLinks = (getLinks(doc));
			return getContactInfo(contactLinks);
		}catch(Exception e){
			throw e;
		}
		
	}
	
	public static Document getHTML(String url){
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}
	public static ArrayList<String> getLinks(Document home){
		ArrayList<String> contactLinks = new ArrayList<String>();
		Elements links = home.getElementsByTag("a");
		for(Element e : links){

			String htmlText = e.text().toLowerCase();
			String url = e.attr("href");
			if(url.length()<2){
				url=e.attr("ng-href");
			}
			if(url.startsWith("/")){
				url = home.baseUri()+url;
			}
			if(isContactLink(htmlText)){
				
				Document contact = getHTML(url);
				if(!contactLinks.contains(contact.location())){
					System.out.println("Found Contact Link "+htmlText+ "--> "+contact.location());
					contactLinks.add(contact.location());
				}
				
			
			}
		}
		return contactLinks;
	}
	public static ArrayList<String> getContactInfo(ArrayList<String> contactLinks){
		ArrayList<String> res = new ArrayList<String>();
		
		for(String s : contactLinks){
			Document doc = null;
			try {
				doc = Jsoup.connect(s).get();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			System.out.println(doc.location() + " "+ doc.title());
			Elements mails = doc.getElementsByTag("a");
			for(Element e : mails){
				Elements href = e.getElementsByAttribute("href");
				if(href.toString().contains("@")){
					res.add(href.text());
				}
			}
			
		}
		return res;
		
	}
	public static boolean isContactLink(String data){
		if(data.contains("info") || data.contains("contact")){
			return true;
		}else{
			return false;
		}
	}
	public static ArrayList<Document> removeDuplicates(ArrayList<Document> docs){
		ArrayList<String> URIs = new ArrayList<String>();
		ArrayList<Document> toRemove = new ArrayList<Document>();
		for(Document d : docs){
			if(URIs.contains(d.location())){
				toRemove.add(d);
			}else{
				URIs.add(d.location());
			}
		}
		docs.remove(toRemove);
		return docs;
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
}
