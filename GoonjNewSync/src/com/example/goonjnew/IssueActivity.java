package com.example.goonjnew;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
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

public class IssueActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
		
		//String[] issue_list = getResources().getStringArray(R.array.issues);

		ArrayList<Set> il = GlobalData.dh.getObjects("serversync.Issue");

		String[] issue_list = new String[il.size()];

		for(int i=0;i<il.size();i++)
		{
			Issue l = (Issue)il.get(i).obj;
			issue_list[i] =  l.issue_name;
		}
		
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_issue, R.id.issue, issue_list));
		
		ListView lv = getListView();
        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
 
              // selected item
              String product = ((TextView) view).getText().toString();
              
              System.out.println("ISSUE SELECTED IS:"+ product);
              
              //UPDATE get name to number mapping for the location
              int num = 0;
      		  //GlobalData.fetched.location_id = num;
      		  //UPDATE make this available somewhere
              
              //update location field
              
 
          }
        }); 
        
        //num is assigned the issue id from the name to issue mapping
        int num = 0;
        
        
        //if chosen
        GlobalData.issue_assigned = true;
        IssueStoryRelation isr = new IssueStoryRelation();
        isr.is_id = Integer.parseInt(getValue(PK_ISSUE)) ;
        int m = isr.is_id++;
        putValue(PK_ISSUE, "" + m);
        isr.issue_id = num;
        isr.story_id = GlobalData.fetched.story_id;
        
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        
        File f = new File(extStorageDirectory + "/Goonj/data/issue");
        try
        {
            if(!f.exists())
            	f.createNewFile();
        	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
        	oos.writeObject(isr);
        	oos.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        GlobalData.fetched.issueStoryPath = extStorageDirectory + "/Goonj/data/issue" ;
        System.out.println("ISSUE PATH IS:"+ GlobalData.fetched.issueStoryPath);
 		
		//setContentView(R.layout.activity_location);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//tap what clicked and update db
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_issue, menu);
		return true;
	}
	
	String ACCOUNT_PREFS_NAME = "prefs";
	String PK_ISSUE = "issue";
	
	 private String getValue(String key)
	    {
	        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
	0);
	        String value = prefs.getString(key, "1");
	        //String secret = prefs.getString(ACCESS_SECRET_NAME, null);
//	        if (key != null && secret != null)
//	        {
//	                String[] ret = new String[2];
//	                ret[0] = key;
//	                ret[1] = secret;
//	                return ret;
//	        }
//	        else
//	        {
//	                return null;
//	        }
	        return value;
	    }

	    /**
	     * Shows keeping the access keys returned from Trusted Authenticator
	in a local
	     * store, rather than storing user name & password, and
	re-authenticating each
	     * time (which is not to be done, ever).
	     */
	    private void putValue(String key, String value)
	    {
	        // Save the access key for later use
	        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
	0);
	        Editor edit = prefs.edit();
	        edit.putString(key, value);
	        //edit.putString(ACCESS_SECRET_NAME, secret);
	        edit.commit();
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
