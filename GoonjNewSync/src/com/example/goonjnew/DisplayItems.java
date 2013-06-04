package com.example.goonjnew;


import serversync.*;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
//import android.util.Pair;
import android.view.MenuItem;
import android.view.View;


public class DisplayItems extends Activity {
  
	//static Story fetched = new Story();
	
    @Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		setContentView(R.layout.activity_display_items);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

    /*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_items, menu);
		return true;
	}
	*/

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
	
	public void moderateItems(View view)
	{
		//UPDATE call saPair<F, S>m's api here
		System.out.println("ENTERING THE FUNCTION");
		ArrayList<Set> st = GlobalData.dh.getObjects("serversync.Story");
		System.out.println("FETCHED THE LIST OF STORIES");
		if(st == null)
			System.out.println("ST is NULL!!");
		else
			System.out.println("SIZE OF ST " + st.size());
		int i=0,min_index = 0;
		if(st.size()>0)
		{
			Story s = new Story();
			Long l1,l2;
			l1 = Long.MAX_VALUE;
			boolean flag = false;
			
			System.out.println("ENTERING LOOP NOW");
			for (i=0; i< st.size();i++)
			{
				Story temp = (Story)st.get(i).obj;
				if(temp==null)
					System.out.println("TEMP IS NULL");
				else if(temp.status_tag2.equals("Assigned"));
				{
					if(!flag)
					{
						s = (Story)st.get(i).obj;
						l1 = Long.parseLong(s.time_assigned);
						flag = true;
						min_index = i;
					}
					else 
					{
						l2 = Long.parseLong(temp.time_assigned);
						if(l2<l1)
						{
							l1 = l2;
							min_index = i;
						}
					}
						
	
				}
							
			}
			
			GlobalData.fetched = (Story)st.get(min_index).obj;
		}
		System.out.println("EXXITING LOOP NOW");
		System.out.println("STORY ID " + GlobalData.fetched.story_id + "FETCHED");
		
		GlobalData.groupid = st.get(min_index).groupid;
		
		GlobalData.namespace = st.get(min_index).namespace;
		System.out.println("NAMESPACE IS:"+GlobalData.namespace);
		//GlobalData.fetched = new Story();
		Intent intent = new Intent(this, ModerateActivity.class);
		startActivity(intent);
	}

}
