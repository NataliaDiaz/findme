package com.wbhackathon.friendfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class DisplayWelcomeMessageActivity extends Activity {

	public final static String FRIENDS_MESSAGE = "com.example.myfirstapp.FRIENDS_MESSAGE";

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_welcome_message);
        
        // Get the message from the intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        System.out.println(message);
        
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("Welcome ".concat(message));

        // Set the text view as the activity layout
        //setContentView(textView);
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
    
	/** Called when the user clicks the Send button */
	public void ShowInMap(View view) {
	    //Intent intent = new Intent(this, MapActivity.class);
	    CheckBox checkb0 = (CheckBox) findViewById(R.id.checkBox1);
	    CheckBox checkb1 = (CheckBox) findViewById(R.id.checkBox2);
	    CheckBox checkb2 = (CheckBox) findViewById(R.id.checkBox3);
	    
	    
	 // Get the selected online friends from the intent
        //String message = intentFriends.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //Friend friend0 = onlineFriends.get(checkb0.getText().toString());
	    //Friend friend1 = onlineFriends.get(checkb1.getText().toString());
	    //Friend friend2 = onlineFriends.get(checkb2.getText().toString());
        
        //TO-DO: GET ONLINE FRIENDS FROM SERVER AND SAVE INTO USERS TO DRAW
        
        String friend0 = checkb0.getText().toString();
	    String friend1 = checkb1.getText().toString();
	    String friend2 = checkb2.getText().toString();
	    
	    Float latitude = new Float(5.0);
	    Float longitude = new Float(7);
	    
	   
	    FriendFinderApplication a = (FriendFinderApplication)getApplication();
		if(checkb0.isChecked()){
	    	User f0 = new User(friend0, "Norris P.", "12345", "Turku", latitude, longitude);
	    	a.data.getUsersToDraw().add(f0);
	    }
	    	
	    if(checkb1.isChecked()){
	    	User f1 = new User(friend1, "Segal P.", "12345", "Turku", latitude, longitude);
    		a.data.getUsersToDraw().add(f1);
	    }
	    //if(checkb2.isChecked())
	    	//friendsToDraw.add(friend2);
	    //for(int i=0; i<friends.size(); ++i){
	    	
	    //}
	    //intentFriends.putExtra(FRIENDS_MESSAGE, friendsToDraw);
	    //startActivity(intent);
	}
}