package com.wbhackathon.friendfinder;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.walkbase.location.WBLocation;
import com.walkbase.location.listeners.WBLocationListener;


public class MyLocation extends Observable implements WBLocationListener {
	public WBLocation location;
	
	

	@Override
	public void errorFetchingLastKnownLocation(String arg0, int arg1) {
		Log.v("MyLocation.jaga", arg0 + " Error Fetching Last Known Location: " + arg1);

	}

	@Override
	public void errorFetchingLiveLocationFeed(String arg0, int arg1) {
		Log.v("MyLocation.jaga", arg0 + " Error fetching Live Location: " + arg1);

	}

	@Override
	public void lastKnownLocationWasRetrieved(WBLocation arg0) {
		Log.v("MyLocation.jaga", "Successfully retrieved last known location.");
		location = arg0;
		updatePosition();
		setChanged();
		notifyObservers(location);
	}

	@Override
	public void liveLocationFeedWasClosed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void liveLocationWasUpdated(WBLocation arg0) {
		location = arg0;
		setChanged();
		notifyObservers(location);
	}

	@Override
	public void successfullyStartedTheLiveLocationFeed() {
		Log.v("MyLocation.java", "Successfully started live location feed.");
	}
	


	public void updatePosition(){

		RequestParams params = new RequestParams();

		try {
			Log.v("MyLocation.java", createPositionJSON());
			params.put("", createPositionJSON());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}

		FriendFinderCommunicator.post("", params, new JsonHttpResponseHandler(){

			//TODO parse response
			@Override
			public void onSuccess(JSONObject arg0) {
				try {
					JSONArray users = arg0.getJSONArray("action");
					System.out.println(users.length());

					if(users.length() < 1)
						//No users in the return...
						return;

					//Populate datastructures
					FriendFinderUserData.getInstance().populatePositionData(users);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.v("SUCCESS", arg0.toString());
			}

			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				Log.v("FUCK", "IT DIDN'T WORK AT ALL...");
				super.onFailure(arg0, arg1);
			}			
		});
	}

	private String createPositionJSON() throws JSONException{
		/*
		   json_message = '''{ "sender" : "norris",
	                      "hash" : "12345690",
	                      "mac" : "1BAC22FF",
	                      "address" : "192.168.1.1",
	                      "timestamp" : "01012014133700",
	                      "action_type" : "update",
	                      "action" : { "updater" : "Mr. Coffee",
	                                   "coords" : [1.04, 0.42],
	                                   "floor" : "1",
	                                   "accuracy" : "1.23"
	                                 } }'''
		 */


		JSONObject object = new JSONObject();
		JSONObject positionObject = new JSONObject();
		JSONArray coords = new JSONArray();

		coords.put(location.getLatitude());
		coords.put(location.getLongitude());
		positionObject.put("updater", "Mr. Coffee");
		positionObject.put("coords", coords);
		positionObject.put("floor", location.getFloor());
		positionObject.put("accuracy", location.getAccuracy());

		object.put("timestamp", location.getTimestamp());
		object.put("action_type", "update");
		object.put("action", positionObject);
		//TODO correct user
		object.put("sender", "ford");
		object.put("hash", "12345690");
		object.put("mac", "1BAC22FF");//Not really used, have to be here anyway

		return object.toString();

	}

}
