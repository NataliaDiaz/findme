package com.wbhackathon.friendfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class User extends MapPoint{
	String name;
	String real_name;
	String hash;
	String address;
	double accuracy;
	long timestamp;
	int floor;
	
	public User( String name,  String real_name,  
			String password,  String address,  
			double latitude,  double longitude) {
	    
		super(latitude, longitude);
		
	    this.name = name;
	    this.real_name = real_name;
	    this.hash = password;
		this.address = address;

	}
	
	public User(JSONObject object) {
	    this.name = object.optString("user", "Incognito");
	    
		try {
			JSONArray pos = object.getJSONArray("coords");
			this.latitude = (Double) pos.get(0);
		    this.longitude = (Double) pos.get(1);
		    this.accuracy = object.getDouble("accuracy");
		    this.timestamp = object.getLong("timestamp");
		    this.floor = object.getInt("floor");
		} catch (JSONException e) {
			Log.e("User.java", object.toString());
			e.printStackTrace();
		}
	}	
}
