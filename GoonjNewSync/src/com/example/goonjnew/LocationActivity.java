package com.example.goonjnew;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import serversync.*;

public class LocationActivity extends ListActivity {

	Story fetched;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//String[] location_list = getResources().getStringArray(R.array.locations);
		
		ArrayList<Set> ll = GlobalData.dh.getObjects("serversync.Loc_district");
		
		String[] location_list = new String[ll.size()];
		
		for(int i=0;i<ll.size();i++)
		{
			Loc_district l = (Loc_district)ll.get(i).obj;
			location_list[i] =  l.name;
		}
		
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_location, R.id.location, location_list));
		
		ListView lv = getListView();
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
 
              // selected item
              String product = ((TextView) view).getText().toString();
              
              System.out.println("LOCATION SELECTED IS :" + product);
              
              //UPDATE get name to number mapping for the location
              int num = 0;
      		  GlobalData.fetched.location_id = num;
      		  //UPDATE make this available somewhere
              
              //update location field
              
 
          }
        }); 
		//setContentView(R.layout.activity_location);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//tap what clicked and update db
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
