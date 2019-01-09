package com.wbhackathon.friendfinder;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MarkPOI extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	double latitude = 0.0; 
	double longitude = 0.0;
	boolean isPrivate = false;
	private String poiName = "";

	private EditText editText;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			latitude = extras.getDouble("latitude");
			longitude = extras.getDouble("longitude");
		}
		setContentView(R.layout.create_poi_layout);

		CheckBox isPrivateCB = (CheckBox) findViewById(R.id.makePOIPrivate);
		if (isPrivateCB.isChecked())
			isPrivate = true;
		else
			isPrivate = false;
		editText = (EditText) findViewById(R.id.poiName);

	}

	public void savePOI(View view) {
		poiName = editText.getText().toString();
		Pin p = new Pin(poiName, "", latitude, longitude, isPrivate);
		RequestParams params = new RequestParams();
		JSONObject o = new JSONObject();

		try {
			//TODO fix
			//o.put("sender", FriendFinderUserData.getInstance().getLoggedInUser().name);
			o.put("sender", "norris");
			o.put("hash", "12345690");
			o.put("mac", "1BAC22FF");
			o.put("address", "192.168.1.1");
			o.put("timestamp", System.currentTimeMillis());
			o.put("action_type", "register_poi");
			o.put("action", p.createPOIJSON());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.put("", o.toString());
		System.out.println(o.toString());

		FriendFinderCommunicator.post("", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject arg0) {
				System.out.println(arg0);
				finishActivity(RESULT_OK);
				//done();
			}
		});


	}


	protected void done(){
		this.onBackPressed();
	}


	public void onCancelSavingPOI(Bundle savedInstanceState) {
		setContentView(R.layout.activity_map);

	}

}