package com.wbhackathon.friendfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Pin extends MapPoint {


	private String description;
	private int floor;
	private String imgUrl;
	private boolean isPrivate; // For notifying when someone is near
	private String user;
	
	public Pin() {
		latitude = 0;
		longitude = 0;
		description = "";
		imgUrl = "";
		isPrivate= false;
	}
	
	public Pin(String description, String imgUrl,
			   double latitude, double longitude,
			   boolean isPrivate) {
		super(latitude, longitude);
		this.description = description;
		this.imgUrl = imgUrl;
		this.isPrivate = isPrivate;
	}
	
	//"poi":[{"img_url":"http:\/\/i.imgur.com\/PlnFq8E.jpg","coords":[42.42,11.11],"floor":1,"user":"norris","name":"Test point"}]}
	public Pin(JSONObject jsonObject) {
		try {
			this.imgUrl = jsonObject.getString("img_url");
			System.out.println("Img_url: " + imgUrl);
			
			JSONArray coord = jsonObject.getJSONArray("coords");
			latitude = (Double)coord.get(0);
			longitude = (Double)coord.get(1);
			floor = jsonObject.optInt("floor", 0);
			description = jsonObject.optString("name");
			user = jsonObject.optString("user", "Incognito");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	String getName() {
		return description;
	}
	
	String getImgUrl() {
		return imgUrl;
	}
	
	
	boolean isPrivate() {
		return isPrivate;
	}
	
	public JSONObject createPOIJSON() throws JSONException{
		/*
		 * { "sender" : "norris",
                      "hash" : "12345690",
                      "mac" : "1BAC22FF",
                      "address" : "192.168.1.1",
                      "timestamp" : "01012014133700",
                      "action_type" : "register_poi",
                      "action" : { "name" : "Test point",
                                   "coords" : [42.42, 11.11],
                                   "floor" : "1",
                                   "img_url" : "http://i.imgur.com/PlnFq8E.jpg"
                                 }
                      }'''
		 */
		JSONObject object = new JSONObject();
		JSONArray coords = new JSONArray();
		coords.put(0, latitude);
		coords.put(1, longitude);
		object.put("name", description);
		object.put("coords", coords);
		object.put("floor", floor);
		object.put("img_url", imgUrl);
		
		return object;
	}
	
}
