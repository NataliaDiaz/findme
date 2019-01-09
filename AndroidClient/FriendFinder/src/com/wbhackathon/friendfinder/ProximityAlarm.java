package com.wbhackathon.friendfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.content.Context;
import android.widget.Toast;

import com.walkbase.location.WBLocation;
import com.wbhackathon.friendfinder.radar.GeoUtils;

public class ProximityAlarm implements Observer {

	public static final double THRESHOLD = 0.015; // KM
	
	Context context;
	
	
	public ProximityAlarm(Context context) {
		this.context = context;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		WBLocation location = (WBLocation)arg1;
		
		FriendFinderUserData userData = FriendFinderUserData.getInstance();
		ArrayList<User> usersToDraw = userData.getUsersToDraw();
		Vector<Pin> pois = userData.getPois();
		
		if (usersToDraw == null) {
			System.out.println("FFFFFFUUUUUU!!!!!!");
			return;
		}
		
		ArrayList<User> nearUsers = new ArrayList<User>();
		HashMap<User, ArrayList<Pin> > nearPois = new HashMap<User, ArrayList<Pin> >();
		
		for (User u : usersToDraw) {
			
			//60.4500787821,22.2932704895
			double lat = u.getLatitude();
			double lon = u.getLongitude();
			
			double lat2 = location.getLatitude();
			double lon2 = location.getLongitude();
			
			double distance = GeoUtils.distanceKm(lat, lon, lat2, lon2);
			
			//System.out.println("Pos: " + lat + "," + lon + "  TARGET: " +lat2 + "," + lon2 + " Distance: "+distance+ " Threshold:" + THRESHOLD);
			if (distance < THRESHOLD) {
				nearUsers.add(u);
			}
			
			for (Pin p : pois) {
				
				double lat1b = p.latitude;
				double lon1b = p.longitude;
				
				
				double distance2 = GeoUtils.distanceKm(lat, lon, lat1b, lon1b);
				
				if (distance2 < THRESHOLD) {
					
					ArrayList<Pin> arr = nearPois.get(u);
					arr.add(p);
					
				}
				
			}
			
			
			
		}
		
		
		
		String toastMsg = "";
		
		if (nearUsers.size() > 0) {
			
			toastMsg += "Users nearby: ";
			for (int i=0; i<nearUsers.size()-1; i++) {
				User u = nearUsers.get(i);
				toastMsg += u.name + ", ";
			}
			User u = nearUsers.get(nearUsers.size()-1);
			toastMsg += u.name + "\n";
			
			
			
			// Get instance of Vibrator from current Context
			//Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			 
			// Vibrate for 300 milliseconds
			//v.vibrate(300);
			
		}
		/*
		if (nearPois.size() > 0) {
			toastMsg += "Alerts: ";
			for (int i=0; i<nearPois.size()-1; i++) {
				User u = nearUsers.get(i);
				toastMsg += u.name + ", ";
			}
			User u = nearUsers.get(nearUsers.size()-1);
			toastMsg += u.name ;
		}*/
		
		if (nearUsers.size() > 0) {
		Toast toast = Toast.makeText(context, toastMsg, Toast.LENGTH_LONG);
		toast.show();
		}
		
	}

	
}
