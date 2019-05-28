package com.grabber.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONObject;

import com.grabber.IO.IO;
import com.grabber.models.AccessToken;
import com.grabber.models.TimeOffer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.owlike.genson.Genson;

@Path("/")
public class Endpoints {
	Genson gen = new Genson();
	

	IO io = new IO();

	@GET
	@Path("/GetItemPriceHistory")
	@Consumes(MediaType.WILDCARD)
	@Produces("application/json")
	public List<TimeOffer> getPriceHistory(@QueryParam("itemName") String itemName, @Context HttpServletRequest req) {
		
		ArrayList<TimeOffer> err = new ArrayList<TimeOffer>();
		try {
			return io.getItemPriceHistory(itemName);

		} catch (Exception e) {
			TimeOffer t = new TimeOffer();
			t.setName("Unable to complete request:"+e.getMessage());
			System.out.println(gen.serialize(t));
			err.add(t);
			e.printStackTrace();
			return err;
			
		}
	}
	@GET
	@Path("/resource")
	@Produces("application/json")
	public Object get(@Context HttpServletRequest req) {
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://api.classy.org/2.0/campaigns/211176/overview")
							 .header("Authorization", req.getHeader("Authorization"))
							  .header("Content-Type", "application/x-www-form-urlencoded")
							  .header("User-Agent", "PostmanRuntime/7.11.0")
							  .header("Accept", "*/*")
							  .header("Cache-Control", "no-cache")
							  .header("Postman-Token", "4f1391d2-07b9-424e-be78-cf8116225341,dbc40c3a-54e2-4f78-93fa-61b0e7d24865")
							  .header("Host", "api.classy.org")
							  .header("accept-encoding", "gzip, deflate")
							  
							  .header("Connection", "keep-alive")
							  .header("cache-control", "no-cache")
							  .asJson();
			System.out.println(response.getBody().getObject().toString());
			return  response.getBody().getObject().toString();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	@POST
	@Path("/auth")
	@Consumes(MediaType.WILDCARD)
	@Produces("application/json")
	public AccessToken authorize(@QueryParam("grant_type") String grantType,@QueryParam("client_id") String clientId, @QueryParam("client_secret") String clientSecret,@Context HttpServletRequest req) {
		AccessToken token = new AccessToken();
		try {
			HttpResponse<JsonNode> response = Unirest.post("https://api.classy.org/oauth2/auth?grant_type="+grantType+"&client_id="+clientId+"&client_secret="+clientSecret)
							  .header("Content-Type", "application/x-www-form-urlencoded")
							  .header("User-Agent", "PostmanRuntime/7.11.0")
							  .header("Accept", "*/*")
							  .header("Cache-Control", "no-cache")
							  .header("Postman-Token", "4f1391d2-07b9-424e-be78-cf8116225341,dbc40c3a-54e2-4f78-93fa-61b0e7d24865")
							  .header("Host", "api.classy.org")
							  .header("accept-encoding", "gzip, deflate")
							 
							  .header("Connection", "keep-alive")
							  .header("cache-control", "no-cache")
							  .asJson();
			
			System.out.println(response.getBody().getObject().toString());
			token.token = response.getBody().getObject().getString("access_token");
			token.expires = response.getBody().getObject().getInt("expires_in");
			token.bearer= response.getBody().getObject().getString("token_type");
			
			return token;
		} catch (UnirestException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return token;
		
	}

}	