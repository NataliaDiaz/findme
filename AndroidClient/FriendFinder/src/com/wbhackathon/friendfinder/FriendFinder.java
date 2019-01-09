package com.wbhackathon.friendfinder;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.walkbase.location.WBLocation;
import com.walkbase.location.WBLocationManager;
import com.wbhackathon.friendfinder.radar.RadarView;

public class FriendFinder extends Activity {


	TextView noKnownPOIs;
	TextView noOnlineFriends;
	TextView accuracy;
	ToggleButton scanButton;
	ToggleButton showPOIs;
	Button selectStalkeesButton;
	Button createPOIButton;

	FriendFinderApplication a;
	PositionUpdateHandler positionHandler;
	private static SensorManager sensorService;
	private RadarView radarView;
	private Sensor sensor;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_finder);
		radarView = (RadarView) findViewById(R.id.radar_view);


		noKnownPOIs = (TextView) findViewById(R.id.noKnownPois);
		noOnlineFriends = (TextView) findViewById(R.id.noOnlineFriends);
		accuracy = (TextView) findViewById(R.id.acc_data);

		scanButton = (ToggleButton) findViewById(R.id.ScanButton);
		showPOIs = (ToggleButton) findViewById(R.id.POIButton);
		selectStalkeesButton = (Button) findViewById(R.id.selectStalkees);
		createPOIButton = (Button) findViewById(R.id.createPOIButton);

		positionHandler = new PositionUpdateHandler();
		MyLocation loc = (MyLocation)WBLocationManager.getWBLocationManager().getWBLocationListener();
		loc.addObserver(positionHandler);
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (sensor != null) {
			sensorService.registerListener(mySensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			//Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");

		} else {
			//Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
			//Toast.makeText(this, "ORIENTATION Sensor not found",
			//    Toast.LENGTH_LONG).show();
			finish();
		}

		radarView.startSweep();

		// Test
		Vector<MapPoint> mapPoints = new Vector<MapPoint>();
		mapPoints.add(new MapPoint(60.120, 22.5));
		mapPoints.add(new MapPoint(60.1305, 22.3));
		mapPoints.add(new MapPoint(59.90001, 22.50001));

		radarView.mMyLocationLat = 60.120;
		radarView.mMyLocationLon = 22.5;
		radarView.addMapPoints(mapPoints);

		Location location = new Location("test");
		location.setLatitude(60.120);
		location.setLongitude(22.5);
		radarView.onLocationChanged(location);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_friend_finder, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		radarView.startSweep();
	}


	public void createPOI(View view){
		Intent intent = new Intent(this, MarkPOI.class);
		intent.putExtra("latitude", radarView.mMyLocationLat);
		intent.putExtra("longitude", radarView.mMyLocationLon);
        //startActivity(intent);
        startActivityForResult(intent, RESULT_OK);
	}


	

	public void onToggleClicked(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		FriendFinderApplication a = (FriendFinderApplication)getApplication();
		if (on) {
			a.startLocationUpdates();
		} else {
			a.stopLocationUpdates();
		}
	}

	public class PositionUpdateHandler implements Observer{

		@Override
		public void update(Observable arg0, Object arg1) {


			double lon = ((WBLocation)arg1).getLongitude();
			double lat = ((WBLocation)arg1).getLatitude();
			double acc = ((WBLocation)arg1).getAccuracy();
			noKnownPOIs.setText(String.valueOf(lon));
			noOnlineFriends.setText(String.valueOf(lat));
			accuracy.setText(String.valueOf(acc));

			// Quick and dirty
			radarView.mMyLocationLat = lat;
			radarView.mMyLocationLon = lon;
			Location location = new Location("test");
			location.setLatitude(lat);
			location.setLongitude(lon);
			radarView.onLocationChanged(location);

		}


	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// angle between the magnetic north directio
			// 0=North, 90=East, 180=South, 270=West
			float azimuth = event.values[0];

			// Fuck it, quick and dirty
			radarView.onSensorChanged(0, event.values);

		}
	};
	private EditText description;
	private CheckBox makePrivate;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensor != null) {
			sensorService.unregisterListener(mySensorEventListener);
		}
	}


}
