package com.ivalentin.gm;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * The main Activity of the app. It's actually the only activity, and it loads other fragments.
 * 
 * @author Iñigo Valentin
 *
 */
public class MainActivity extends ActionBarActivity implements LocationListener{
		
	//Set of GPS coordinates
	private double[] coordinates = new double[2];
	
	//The main layout of the app
	private MainLayout mLayout;
	
	//The location manager to get GPS coordinates
	private LocationManager locationManager;
	
	//GPS coordinates provider
	private String provider;
		
	/**
	 * Loads a section in the main screen.
	 * 
	 * @param section Section identifier {@see GM}
	 * @param fromSliderMenu Indicates if the call has been made from the slider menu, so it can be closed.
	 */
	public void loadSection (byte section, boolean fromSliderMenu){
		FragmentManager fm = MainActivity.this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = null;
		String title = "";
		Bundle bundle = new Bundle();
		switch (section){
			case GM.SECTION_HOME:
				fragment = new HomeLayout();
				//Pass current GPS coordinates
				bundle.putDouble("lat", coordinates[0]);
				bundle.putDouble("lon", coordinates[1]);
				fragment.setArguments(bundle);
				title = getString(R.string.menu_home);
				break;
			case GM.SECTION_SCHEDULE:
				fragment = new ScheduleLayout();
				bundle.putInt(GM.SCHEDULE, GM.SECTION_SCHEDULE);
				fragment.setArguments(bundle);
				title = getString(R.string.menu_schedule);
				break;
			case GM.SECTION_GM_SCHEDULE:
				fragment = new ScheduleLayout();
				bundle.putInt(GM.SCHEDULE, GM.SECTION_GM_SCHEDULE);
				fragment.setArguments(bundle);
				title = getString(R.string.menu_gm_schedule);
				break;
			case GM.SECTION_AROUND:
				fragment = new AroundLayout();
				//Pass current GPS coordinates
				bundle.putDouble("lat", coordinates[0]);
				bundle.putDouble("lon", coordinates[1]);
				fragment.setArguments(bundle);
				title = getString(R.string.menu_around);
				break;
			case GM.SECTION_LOCATION:
				fragment = new LocationLayout();
				//Pass current GPS coordinates
				bundle.putDouble("lat", coordinates[0]);
				bundle.putDouble("lon", coordinates[1]);
				fragment.setArguments(bundle);
				title = getString(R.string.menu_location);
				break;
			case GM.SECTION_SETTINGS:
				fragment = new SettingsLayout();
				title = getString(R.string.menu_settings);
				break;
			case GM.SECTION_ABOUT:
				fragment = new AboutLayout();
				title = getString(R.string.menu_about);
				break;
		}
		
		//Replace the fragment.
		ft.replace(R.id.activity_main_content_fragment, fragment);
		ft.addToBackStack(title);
		ft.commit();
		setSectionTitle(title);
		
		//If calling from the menu, close it.
		if (fromSliderMenu)
			mLayout.toggleMenu();
	}
	
	
	/**
	 * Sets the title of the current section.
	 * 
	 * @param title The title of the current section.
	 */
	public void setSectionTitle(String title){
		TextView tvTitle = (TextView) findViewById(R.id.activity_main_content_title); 
		tvTitle.setText(title);
	}
	
	/** 
	 * run when the app resumes. It's extended to request
	 * location updates when the app is resumed.
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, GM.LOCATION_ACCURACY, 1, this);
	}

	/**
	 * Run when the app is paused.Extended to remove the location
	 * listener updates when the Activity is paused.
	 *  
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	/**
	 * Called when the location is updated.
	 * 
	 * @param locaton The updated location.
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 * 
	 */
	@Override
	public void onLocationChanged(Location location) {
		coordinates[0] = location.getLatitude();
		coordinates[1] = location.getLongitude();
	}

	/**
	 * Extended for nothing. Needed because the class implements LocationListener.
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	/**
	 * Extended for nothing. Needed because the class implements LocationListener.
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {}

	/**
	 * Extended for nothing. Needed because the class implements LocationListener.
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {}
	  
	  
	/**
	 * Runs when the activity is created
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("TrulyRandom") //I don't care about getting "potentially unsecured numbers" here
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// Get the location manager.
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, this);
	    // Define the criteria how to select the location provider: use default.
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    //Get intent extras
	    String action = getIntent().getStringExtra(GM.EXTRA_ACTION);
	    String actionText = getIntent().getStringExtra(GM.EXTRA_TEXT);
	    String actionTitle = getIntent().getStringExtra(GM.EXTRA_TITLE);
	    
	    // Initialize the location fields.
	    if (location != null){
	    	System.out.println("Provider " + provider + " has been selected.");
	    	onLocationChanged(location);
	    }
	    else{
	    	Log.e("Location", "Location not available");
	    }

		//Set an alarm for notifications..
	    AlarmReceiver alarm = new AlarmReceiver();
		alarm.setAlarm(this);
		
		//Remove title bar.
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    
	    //Set layout.
		setContentView(R.layout.activity_main);
		mLayout = (MainLayout) findViewById(R.id.main_layout);
		
		//Assign menu button
		ImageButton btMenu = (ImageButton) findViewById(R.id.bt_menu); 
		btMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { mLayout.toggleMenu(); }
		});
		
		//Assign menu items and icons
		TextView menuItem[] = new TextView[7];
		Drawable menuIcon[] = new Drawable[7];
		menuItem[0] = (TextView) findViewById(R.id.menu_home);
		menuItem[1] = (TextView) findViewById(R.id.menu_schedule);
		menuItem[2] = (TextView) findViewById(R.id.menu_gm_schedule);
		menuItem[3] = (TextView) findViewById(R.id.menu_around);
		menuItem[4] = (TextView) findViewById(R.id.menu_location);
		menuItem[5] = (TextView) findViewById(R.id.menu_settings);
		menuItem[6] = (TextView) findViewById(R.id.menu_about);
		menuIcon[0] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_home, null);
		menuIcon[1] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_program, null);
		menuIcon[2] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_gm, null);
		menuIcon[3] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_around, null);
		menuIcon[4] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_location, null);
		menuIcon[5] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_settings, null);
		menuIcon[6] = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_about, null);
		for (int i = 0; i < 7; i ++){
			//menuIcon[i].setBounds(0, 0, 140, 140);
			menuIcon[i].setBounds(0, 0, (int) (menuItem[i].getTextSize() * 2.2), (int) (menuItem[i].getTextSize() * 2.2));
			menuItem[i].setCompoundDrawables(menuIcon[i], null, null, null);
			menuItem[i].setCompoundDrawablePadding(20);
		}
		
		//Set click listers for menu items
		menuItem[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_HOME, true); }
		});
		menuItem[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_SCHEDULE, true); }
		});
		menuItem[2].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_GM_SCHEDULE, true); }
		});
		menuItem[3].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_AROUND, true); }
		});
		menuItem[4].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_LOCATION, true); }
		});
		menuItem[5].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_SETTINGS, true); }
		});
		menuItem[6].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { loadSection(GM.SECTION_ABOUT, true); }
		});
		
		//If the user code is not set, generate one
		SharedPreferences preferences = getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		if (preferences.getString(GM.USER_CODE, "").length() == 0){
			SecureRandom random = new SecureRandom();
			String newCode = new BigInteger(130, random).toString(32).substring(0, 8);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(GM.USER_CODE, newCode);
			editor.commit();
		}
		
		//If the database doesn't exist, create it.
		if (databaseExists() == false){
        	Log.i("DB status", "Database not found. Creating it...");
        	createDatabase();
        }
				
		//Sync db
		sync();
		
		//Load initial section
		loadSection(GM.SECTION_HOME, false);
		
		//If the intent had extras, do something
		if (actionText != null){
			TextView tvDialogTitle, tvDialogText;
			Button btDialogClose, btDialogAction;
			Drawable dialogIcon;
			if (actionTitle != null){
				
				final Dialog dialog = new Dialog(this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_notification);
				
				//Set title
				tvDialogTitle = (TextView) dialog.findViewById(R.id.tv_dialog_notification_title);
				tvDialogTitle.setText(actionTitle);
				
				
				//Set text
				tvDialogText = (TextView) dialog.findViewById(R.id.tv_dialog_notification_text);
				tvDialogText.setText(actionText);
				
				//Set close button
				btDialogClose = (Button) dialog.findViewById(R.id.bt_dialog_notification_close);
				btDialogClose.setOnClickListener(new OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				dialog.dismiss();
	    			}
	    		});
				
				//Set the action button
				btDialogAction = (Button) dialog.findViewById(R.id.bt_dialog_notification_action);
				if (action != null){
					if (action.equals(GM.EXTRA_ACTION_GM)){
						dialogIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_gm, null);
						dialogIcon.setBounds(0, 0, (int) (tvDialogTitle.getTextSize() * 1.4), (int) (tvDialogTitle.getTextSize() * 1.4));
						tvDialogTitle.setCompoundDrawables(dialogIcon, null, null, null);
						tvDialogTitle.setCompoundDrawablePadding(20);
						btDialogAction.setVisibility(View.VISIBLE);
						btDialogAction.setText(this.getApplicationContext().getString(R.string.notification_action_gm));
						btDialogAction.setOnClickListener(new OnClickListener() {
			    			@Override
			    			public void onClick(View v) {
			    				dialog.dismiss();
			    				loadSection(GM.SECTION_GM_SCHEDULE, false);
			    			}
			    		});
					}
					else if (action.equals(GM.EXTRA_ACTION_SCHEDULE)){
						dialogIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_program, null);
						dialogIcon.setBounds(0, 0, (int) (tvDialogTitle.getTextSize() * 1.4), (int) (tvDialogTitle.getTextSize() * 1.4));
						tvDialogTitle.setCompoundDrawables(dialogIcon, null, null, null);
						tvDialogTitle.setCompoundDrawablePadding(20);
						btDialogAction.setVisibility(View.VISIBLE);
						btDialogAction.setText(this.getApplicationContext().getString(R.string.notification_action_schedule));
						btDialogAction.setOnClickListener(new OnClickListener() {
			    			@Override
			    			public void onClick(View v) {
			    				dialog.dismiss();
			    				loadSection(GM.SECTION_SCHEDULE, false);
			    			}
			    		});
					}
					else{
						dialogIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_about, null);
						dialogIcon.setBounds(0, 0, (int) (tvDialogTitle.getTextSize() * 1.4), (int) (tvDialogTitle.getTextSize() * 1.4));
						tvDialogTitle.setCompoundDrawables(dialogIcon, null, null, null);
						tvDialogTitle.setCompoundDrawablePadding(20);
						btDialogAction.setVisibility(View.GONE);
					}
				}
				else{
					btDialogAction.setVisibility(View.GONE);
				}

				//Show the dialog
				dialog.show();
			}
		}
	}
	
	/**
	 * Checks if the app database exists.
	 * 
	 * @return True if the database exists, false otherwise.
	 */
	private boolean databaseExists(){
    	File database = getApplicationContext().getDatabasePath(GM.DB_NAME);
    	if (database.exists())
    		return true;
    	else
    		return false;
    }
    
    /**
     * Creates the app database and fills it with the hard coded, default data.
     */
    private void createDatabase(){
    	SQLiteDatabase db = null;
    	try {
    		//Create database
    		db = this.openOrCreateDatabase(GM.DB_NAME, MODE_PRIVATE, null);
		 
    		//Create tables in the Database.
    		db.execSQL("CREATE TABLE IF NOT EXISTS " + GM.DB_EVENT +" (" + GM.DB_EVENT_ID + " INT, " + GM.DB_EVENT_SCHEDULE + " INT, " + GM.DB_EVENT_GM + " INT, " + GM.DB_EVENT_NAME + " VARCHAR, " + GM.DB_EVENT_DESCRIPTION + " VARCHAR, " + GM.DB_EVENT_HOST + " INT, " + GM.DB_EVENT_PLACE + " INT, " + GM.DB_EVENT_START + " DATETIME, " + GM.DB_EVENT_END + " DATETIME);");
    		db.execSQL("CREATE TABLE IF NOT EXISTS " + GM.DB_PEOPLE +" (" + GM.DB_PEOPLE_ID + " INT, " + GM.DB_PEOPLE_NAME + " VARCHAR, " + GM.DB_PEOPLE_LINK + " VARCHAR);");
    		db.execSQL("CREATE TABLE IF NOT EXISTS " + GM.DB_PLACE +" (" + GM.DB_PLACE_ID + " INT, " + GM.DB_PLACE_NAME + " VARCHAR, " + GM.DB_PLACE_ADDRESS + " VARCHAR, " + GM.DB_PLACE_CP + " VARCHAR, " + GM.DB_PLACE_LATITUDE + " FLOAT, " + GM.DB_PLACE_LONGITUDE + " FLOAT);");
    		db.execSQL("CREATE TABLE IF NOT EXISTS day (id INT, name VARCHAR, price INT);");
    		db.execSQL("CREATE TABLE IF NOT EXISTS offer (id INT, name VARCHAR, days INT, price INT);");
    		db.execSQL("CREATE INDEX id ON event(id)");
    		db.execSQL("CREATE INDEX id ON place(id)");
    		db.execSQL("CREATE INDEX id ON people(id)");
    		db.execSQL("CREATE INDEX id ON day(id)");
    		db.execSQL("CREATE INDEX id ON offer(id)");
    	}
    	catch (Exception ex){
    		Log.e("Error creating database", ex.toString());
    	}
    }

	/**
	 * Performs a full sync against the remote database.
	 */
	public void sync(){
		ProgressBar pbSync = (ProgressBar) findViewById(R.id.pb_sync);
		new Sync(this, pbSync).execute();
	}
	
	/**
	 * Overrides onBackPressed(). 
	 * Used to show the previous fragment instead of fnishing the app.
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed(){
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 1){
			fm.popBackStack();
		}
		else{
			super.onBackPressed();
		}
		
	}
	
}
