package com.wbhackathon.friendfinder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.text.format.Time;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.walkbase.location.WBLocationManager;



public class FriendFinderApplication extends Application {
	FriendFinderUserData data;
	private WBLocationManager manager;
	protected MyLocation location;
	private PeriodicFetcher fetcher;
	private POIFetcher poiFetcher;
	private ExecutorService executor;
	
	
	public FriendFinderApplication() {
		super.onCreate();
		this.manager = WBLocationManager.getWBLocationManager();
		location = new MyLocation();
		data = FriendFinderUserData.getInstance();
		manager.setApiKey("9ew2ucuohe67381nbwbfbw9sbb9");
		manager.setWBLocationListener(location);
		executor = Executors.newFixedThreadPool(2);
	}
	
	/**
	 * Asks Walkbase for a feed of location updates.
	 */
	public void startLocationUpdates(){
		if(fetcher == null || !fetcher.running){
			fetcher = new PeriodicFetcher(manager, 2);
			executor.submit(fetcher);
		}
		if(poiFetcher == null || !poiFetcher.running){
			poiFetcher = new POIFetcher();
			executor.submit(poiFetcher);
		}
	}
	
	
	/**
	 * Stops Walkbase location updates
	 */
	public void stopLocationUpdates(){
		if(fetcher != null)
			fetcher.stop();
		if(poiFetcher != null)
			poiFetcher.stop();
	}
	
	
	class POIFetcher implements Runnable{
		int sleep = 60*1000;
		boolean running = false;
		
		private JSONObject createParam(){
			
			JSONObject json = new JSONObject();
			try {
				//TODO correct user
				json.put("sender", "ford");
				json.put("hash", "12345690");
				json.put("mac", "1BAC22FF");//Not really used, have to be here anyway
				json.put("timestamp", System.currentTimeMillis());
				json.put("action_type", "poi_request");
				json.put("action", new JSONObject());
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return json;
		}
		
		@Override
		public void run() {
			running = true;
			while(running == true){
				RequestParams params = new RequestParams();
				params.put("", createParam().toString());
				FriendFinderCommunicator.post("", params, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONObject arg0) {
						
						JSONArray array = null;
						try {
							array = arg0.getJSONObject("action").getJSONArray("poi");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						FriendFinderUserData.getInstance().setPois(array);
						
					}
				});

				Log.v("FriendFinderApplication.java", "Sent POI request.");
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
			
		}
		
		public void stop(){
			running = false;
		}
		
	}
	
	
	class PeriodicFetcher implements Runnable{
		boolean running = false;
		int sleep;
		WBLocationManager wbmanager;
		
		public PeriodicFetcher(WBLocationManager man, int period) {
			sleep = period;
			wbmanager = man;
		}
		
		@Override
		public void run() {
			running = true;
			while(running == true){
				wbmanager.fetchLastKnownUserLocation(getApplicationContext());
				Log.v("FriendFinderApplication.java", "Sent location request.");
				try {
					Thread.sleep(sleep*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return;
		}
		
		public void stop(){
			running = false;
		}
		
	}
	
}
