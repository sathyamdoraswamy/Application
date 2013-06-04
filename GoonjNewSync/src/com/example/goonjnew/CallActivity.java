package com.example.goonjnew;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;

public class CallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_call, menu);
		return true;
	}
	
	public void call(View v)
	{
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:9818844417"));
		startActivity(callIntent);
		//record time of call here
		//update time field
	}
	
	public void onYNRadioButtonClicked(View v)
	{
	    // Is the button now checked?
	    boolean checked = ((RadioButton) v).isChecked();
	    
	    // Check which radio button was clicked
	    switch(v.getId()) {
	        case R.id.yes:
	            if (checked)
	            {
	            	//do Nothing. End of Moderation
	            	GlobalData.fetched.status_tag1 = "guidance call given";
	            	//UPDATE put story item back
	            	GlobalData.fetched.status_tag2 = "Moderated";
	            	GlobalData.dh.updateObject(GlobalData.groupid, GlobalData.fetched, GlobalData.namespace);
	            	this.finish();
	            }
	            break;
	        case R.id.no:
	            if (checked)
	            {
	            	//UPDATE put the story in ur db
	            	//UPDATE add to guidance call items list
	            	GlobalData.dh.updateObject(GlobalData.groupid, GlobalData.fetched, GlobalData.namespace);
	            	this.finish();
	            }

	               
	     }
	}

}
