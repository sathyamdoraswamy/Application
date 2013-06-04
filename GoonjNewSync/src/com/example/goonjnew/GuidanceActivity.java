package com.example.goonjnew;



import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GuidanceActivity extends Activity {
	private static final String TAG = "AudioDemo";
	private static final String isPlaying = "Media is Playing"; 
	private static final String notPlaying = "Media has stopped Playing"; 
	
	MediaPlayer player;
	Button playerButton;


		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.activity_guidance) ;
	    
	    setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    
	    Intent i = getIntent();
        // getting attached intent data
        String product = i.getStringExtra(DisplayGuidanceItems.EXTRA_MESSAGE);
        TextView txtProduct = (TextView) findViewById(R.id.story);
        txtProduct.setText(product);
        

	      /* 
	        TextView txtProduct = (TextView) findViewById(R.id.product_label);
	 
	        Intent i = getIntent();
	        // getting attached intent data
	        String product = i.getStringExtra(DisplayGuidanceItems.EXTRA_MESSAGE);
	        // displaying selected product name
	        txtProduct.setText(product);
	        */
	/*        Intent intent = getIntent();
		    String message = intent.getStringExtra(DisplayGuidanceItems.EXTRA_MESSAGE);

		    // Create the text view
		    TextView textView = new TextView(this);
		    textView.setTextSize(40);
		    textView.setText(message);
			setContentView(textView); */
	        //setContentView(R.layout.activity_guidance);
		
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_guidance, menu);
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

	/*@Override
	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.make_call:
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:9818844417"));
				startActivity(callIntent);
				break;
			case R.id.play_audio:
				MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.song);
				mediaPlayer.start();
				break;
		}
	}*/
	

	
	public void play(View v)
	{
		//String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		//String path = extStorageDirectory + "/Goonj/song.mp3";
		
		String path = GlobalData.fetched.audioPath;
		System.out.println("PATH IS :"+path);
		player = MediaPlayer.create(getApplicationContext(), Uri.parse(path));  

	    final Button test = (Button)this.findViewById(R.id.play_audio);
	    
	    player.start();
	    test.setText("स्टॉप");
	    
	    test.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            if (player.isPlaying()) {
	                player.stop();
	                test.setText("प्ले ऑडियो");
	            } else {
	                player.start();
	            }
	        }
	    });
		
		
		/* old code
		player = MediaPlayer.create(getApplicationContext(), R.raw.song);
		//player.start();
		player.setLooping(false); // Set looping

		// Get the button from the view
		playerButton = (Button) this.findViewById(R.id.play_audio);
		playerButton.setText("Stop");
		playerButton.setOnClickListener(this);

		// Begin playing selected media
		//demoPlay();
		player.start();
		// Release media instance to system
		player.release(); */
	}
	
	public void call(View v)
	{
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:9818844417"));
		startActivity(callIntent);
		
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		player.pause();
	}

	// Initiate media player pause
	private void demoPause(){
            player.pause();
            playerButton.setText("Play");
            Toast.makeText(this, notPlaying, Toast.LENGTH_LONG).show();
            Log.d(TAG, notPlaying);
	}
	
	// Initiate playing the media player
	private void demoPlay(){
            player.start();
            playerButton.setText("Stop");
            Toast.makeText(this, isPlaying, Toast.LENGTH_LONG).show();
            Log.d(TAG, isPlaying);
    }
	
	// Toggle between the play and pause
	private void playPause() {
		if(player.isPlaying()) {
		  demoPause();
		} else {
		  demoPlay();
		}	
	}

/*	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onClick: " + v);
		if (v.getId() == R.id.play_audio) {
			playPause();
		}
	}   */
	
	
}


