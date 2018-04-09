package com.grabber.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.grabber.IO.IO;
import com.grabber.models.TimeOffer;
import com.owlike.genson.Genson;

@Path("/PriceGrabber")
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

}	