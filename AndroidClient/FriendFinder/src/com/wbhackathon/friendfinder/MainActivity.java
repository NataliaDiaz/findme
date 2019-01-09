package com.wbhackathon.friendfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;



public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    Intent intent = new Intent(this, DisplayWelcomeMessageActivity.class);
	    EditText editText = (EditText) findViewById(R.id.username_edit_message);
	    EditText editText2 = (EditText) findViewById(R.id.password_edit_message);
	    String username = editText.getText().toString();
	    String password = editText2.getText().toString();
	    intent.putExtra(EXTRA_MESSAGE, username);
	    intent.putExtra(EXTRA_MESSAGE, password);
	    startActivity(intent);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        
        return true;
    }
    
}
