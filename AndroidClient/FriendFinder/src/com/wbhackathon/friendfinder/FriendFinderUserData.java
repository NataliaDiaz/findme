package com.wbhackathon.friendfinder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendFinderUserData extends Observable {
	private ArrayList<User> onlineUsers;
	private ArrayList<User> usersToDraw;
	private Vector<Pin> pois;
	private User loggedInUser;


	private static volatile FriendFinderUserData instance = null;
    private FriendFinderUserData() { }

    public static FriendFinderUserData getInstance() {
            if (instance == null) {
                    synchronized (FriendFinderUserData .class){
                            if (instance == null) {
                                    instance = new FriendFinderUserData ();
                                    
                            }
                  }
            }
            return instance;
    }
    
	//{"action":[{"timestamp":21,"coords":[0.42,1.04],"floor":1,"user":"norris","accuracy":1.23}]
	public void populatePositionData(JSONArray object){
		
		if(onlineUsers == null)
			onlineUsers = new ArrayList<User>();
		if(usersToDraw == null)
			usersToDraw = new ArrayList<User>();
		usersToDraw.clear();
		for( int i = 0; i < object.length(); i++){
			try {
				usersToDraw.add(new User(object.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers();
	}
	
	
	public Vector<Pin> getPois() {
		if(pois == null)
			pois = new Vector<Pin>();
		return pois;
	}

	//{"action":{"poi":[{"img_url":"http:\/\/i.imgur.com\/PlnFq8E.jpg","coords":[42.42,11.11],"floor":1,"user":"norris","name":"Test point"}]},"timestamp":"42","sender":"root","mac":"123456FF","action_type":"poi_request","hash":"root"}
	public void setPois(JSONArray objects) {
		if(pois == null)
			pois = new Vector<Pin>();
		
		pois.clear();
		for(int i = 0; i < objects.length(); i++){
			try {
				pois.add(new Pin(objects.getJSONObject(i)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers();
	}

	public void setUser(User u){
		loggedInUser = u;
	}

	public ArrayList<User> getOnlineUsers() {
		return onlineUsers;
	}

	public void setOnlineUsers(ArrayList<User> onlineUsers) {
		this.onlineUsers = onlineUsers;
	}

	public ArrayList<User> getUsersToDraw() {
		return usersToDraw;
	}

	public void setUsersToDraw(ArrayList<User> usersToDraw) {
		this.usersToDraw = usersToDraw;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}