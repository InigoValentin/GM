package com.ivalentin.gmmaster;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;

/**
 * The main Activity of the app. 
 * 
 * If the user is not yet authorized to use it, it will show a form to apply or a text indicating to wait. 
 * If he is authorized, it will show the location reporting options and a button to send notifications.
 * 
 * @author Iñigo Valentin
 *
 */
public class MainActivity extends Activity {

	//The four screens of the activity. Only one of them will be shown at a time
	private LinearLayout llForm, llLocation, llButtons;
	private TextView tvNoadmin;
	
	//The user code that will be generated
	private String newCode;
	
	//Input fields
	private EditText etUser, etPhone;
	
	//elements in the main screen
	private Button btLocationReport;
	private TextView tvLocationUser;
	
	//Controls if the user is currently reporting location
	private boolean reporting;
	
	/**
	 * Run when the activity is launched. 
	 * 
	 * Identifies if the user is authorized and displays options accordingly. 
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("TrulyRandom") //I don't care if I get "potentially insecure" numbers here.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//Set content
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Assign layouts from the main screen
		llForm = (LinearLayout) findViewById(R.id.ll_form);
		llLocation = (LinearLayout) findViewById(R.id.ll_location);
		llButtons = (LinearLayout) findViewById(R.id.ll_buttons);
		tvNoadmin = (TextView) findViewById(R.id.tv_noadmin);
		btLocationReport = (Button) findViewById(R.id.bt_button_report);
		tvLocationUser = (TextView) findViewById(R.id.tv_location_name);
		
		//Show and hide sections according to account status
		final SharedPreferences preferences = getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		int accountStatus = preferences.getInt(GM.ACCOUNT_STATUS, GM.ACCOUNT_UNSUBMITTED);

		List<String> lines;
		FetchURL fetch;
		String name, code;
		
		switch (accountStatus){
		
			case GM.ACCOUNT_UNSUBMITTED:
				llForm.setVisibility(View.VISIBLE);
				tvNoadmin.setVisibility(View.GONE);
				llLocation.setVisibility(View.GONE);
				llButtons.setVisibility(View.GONE);
				//Generate new code
				SecureRandom random = new SecureRandom();
				newCode = new BigInteger(130, random).toString(32).substring(0, 8);
				
				//Assign form views
				Button btSend = (Button) findViewById(R.id.bt_form_send);
				etUser = (EditText) findViewById(R.id.et_form_uname);
				etPhone = (EditText) findViewById(R.id.et_form_phone);
				
				btSend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (uploadUser(newCode, etUser.getText().toString(), etPhone.getText().toString())){
							llForm.setVisibility(View.GONE);
							tvNoadmin.setVisibility(View.VISIBLE);
							SharedPreferences.Editor editor = preferences.edit();
						    editor.putInt(GM.ACCOUNT_STATUS, GM.ACCOUNT_PENDING);
						    editor.commit();
						}
					}
				});
				break;
				
			case GM.ACCOUNT_PENDING:
				llForm.setVisibility(View.GONE);
				tvNoadmin.setVisibility(View.VISIBLE);
				llLocation.setVisibility(View.GONE);
				llButtons.setVisibility(View.GONE);
				//Fetches the remote URL, triggering the admin insertion in the database.
				fetch = new FetchURL();
				name = preferences.getString(GM.USER_NAME, "");
				code = preferences.getString(GM.USER_CODE, "");
				fetch.Run("http://inigovalentin.com/gm/app/upload/adminstatus.php?name=" + name + "&code=" + code);
				
				//Read the output to see if the submission was done.
				lines = fetch.getOutput();
				for(int i = 0; i < lines.size(); i++){
					if (lines.get(i).length() >= 19){
						if (lines.get(i).substring(0, 19).equals("<status>-1</status>")){
						    llForm.setVisibility(View.VISIBLE);
							tvNoadmin.setVisibility(View.GONE);
							llLocation.setVisibility(View.GONE);
							llButtons.setVisibility(View.GONE);
						}
					}
					else{
						if (lines.get(i).length() >= 18){
							if (lines.get(i).substring(0, 18).equals("<status>0</status>")){
								Toast.makeText(getApplicationContext(), getString(R.string.toast_not_validated_yet), Toast.LENGTH_LONG).show();
							}
							else if (lines.get(i).substring(0, 18).equals("<status>1</status>")){
								SharedPreferences.Editor editor = preferences.edit();
							    editor.putInt(GM.ACCOUNT_STATUS, GM.ACCOUNT_ACTIVE);
							    editor.commit();
							    llForm.setVisibility(View.GONE);
								tvNoadmin.setVisibility(View.GONE);
								llLocation.setVisibility(View.VISIBLE);
								llButtons.setVisibility(View.VISIBLE);
								Toast.makeText(getApplicationContext(), getString(R.string.toast_validated), Toast.LENGTH_LONG).show();
							}
						}
					}
				}
				break;
			case GM.ACCOUNT_ACTIVE:
				llForm.setVisibility(View.GONE);
				tvNoadmin.setVisibility(View.GONE);
				llLocation.setVisibility(View.VISIBLE);
				llButtons.setVisibility(View.VISIBLE);
				
				//Get database info
				boolean result = false;
				String reportingUser = preferences.getString(GM.USER_NAME, "");
				fetch = new FetchURL();
				fetch.Run("http://inigovalentin.com/gm/app/location.php");
				lines = fetch.getOutput();
				for(int i = 0; i < lines.size(); i++){
					if (lines.get(i).length() >= 25){
						if (lines.get(i).substring(0, 25).equals("<location>none</location>"))
							result = false;
					}
					if (lines.get(i).length() >= 6){
						if (lines.get(i).substring(0, 6).equals("<user>")){
							reportingUser = lines.get(i).substring(6, lines.get(i).length()-7);
							result = true;
						}
					}
				}
				if (result){
					//Change location board
					tvLocationUser.setText(reportingUser);
					name = preferences.getString(GM.USER_NAME, "");
					if (name.equals(reportingUser)){
						llLocation.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_green));
						reporting = true;
						btLocationReport.setText(getApplicationContext().getString(R.string.button_location_stop));
					}
					else{
						llLocation.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_yellow));
						reporting = false;
					}
					
					Log.d("ReportingUser", reportingUser);
				}
				else{
					reporting = false;
				}
				
				//Assign buttons
				Button btNotification = (Button) findViewById(R.id.bt_button_notification);
				btLocationReport.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (reporting == false){
							//Report initial location
							//Call server
							boolean result = false;
							SharedPreferences preferences = getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
							String user = preferences.getString(GM.USER_NAME, "");
							String code = preferences.getString(GM.USER_CODE, "");
							//Fetches the remote URL, triggering the admin insertion in the database.
							FetchURL fetch = new FetchURL();
							fetch.Run("http://inigovalentin.com/gm/app/upload/location.php?user=" + user + "&code=" + code + "&lat=0&lon=0&manual=1");
							//Read the output to see if the submission was done.
							List<String> lines = fetch.getOutput();
							for(int i = 0; i < lines.size(); i++){
								if (lines.get(i).length() >= 19){
									if (lines.get(i).substring(0, 21).equals("<status>sent</status>"))
										result = true;
									}
							}
							if (result){
								//Start the location service
								v.getContext().startService(new Intent(v.getContext(), LocationService.class));
								Intent intentToFire = new Intent(GM.ACTION_REFRESH_SCHEDULE_ALARM);
							    intentToFire.putExtra(GM.ALARM_COMMAND, GM.ALARM_ACT_DOCUMENT);
							    sendBroadcast(intentToFire);
							    OnNewLocationListener onNewLocationListener = new OnNewLocationListener() {
							        @Override
							        public void onNewLocationReceived(Location location) {
							            //Use your new location here then stop listening
							            LocationAlarm.clearOnNewLocationListener(this);
							        }
							    };
							    //Start listening for new location
							    LocationAlarm.setOnNewLocationListener(onNewLocationListener);
								
								//Show notification
								NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(v.getContext())
						    		.setSmallIcon(R.drawable.pinpoint)
						    		.setContentTitle(getString(R.string.notif_reporting_title))
						    		.setOngoing(true)
						    		//.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pinpoint))
						    		.setVibrate((new long[] {200, 200, 200}))
						    		.setSubText(getString(R.string.app_name))
						    		.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						    		.setContentText(getString(R.string.notif_reporting));
						    	// Creates an explicit intent for an Activity in your app
						    	Intent resultIntent = new Intent(v.getContext(), MainActivity.class);
						    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(v.getContext());
						    	stackBuilder.addParentStack(MainActivity.class);
						    	// Adds the Intent that starts the Activity to the top of the stack
						    	stackBuilder.addNextIntent(resultIntent);
						    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
						    	mBuilder.setContentIntent(resultPendingIntent);
						    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						    	// mId allows you to update the notification later on.
						    	mNotificationManager.notify(GM.NOTIFICATION_ID_REPORTING, mBuilder.build());
								
								//Change location panel and button text
								llLocation.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_green));
								
								//Set reporting status
								reporting = true;
								
								//Change button text
								btLocationReport.setText(getApplicationContext().getString(R.string.button_location_stop));
								tvLocationUser.setText(user);
							}
							else{
								//TODO: Change toast
								Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_not_submitted), Toast.LENGTH_LONG).show();
							}
						}
						else{
							//Cancel alarm
							Intent intent = new Intent(getApplicationContext(), LocationAlarm.class);
				            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1253, intent, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
							AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
						    alarmManager.cancel(pendingIntent);
						    //Cancel notification
						    if (Context.NOTIFICATION_SERVICE!=null) {
						        String ns = Context.NOTIFICATION_SERVICE;
						        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
						        nMgr.cancel(GM.NOTIFICATION_ID_REPORTING);
						    }
							
						    //Send another notification
						    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(v.getContext())
				    		.setSmallIcon(R.drawable.pinpoint_cancel)
				    		.setContentTitle(getString(R.string.notif_reporting_stop_title))
				    		.setAutoCancel(true)
				    		//.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pinpoint))
				    		.setVibrate((new long[] {2000, 2000, 2000}))
				    		.setSubText(getString(R.string.app_name))
				    		.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				    		.setContentText(getString(R.string.notif_reporting_stop));
					    	// Creates an explicit intent for an Activity in your app
					    	Intent resultIntent = new Intent(v.getContext(), MainActivity.class);
					    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(v.getContext());
					    	stackBuilder.addParentStack(MainActivity.class);
					    	// Adds the Intent that starts the Activity to the top of the stack
					    	stackBuilder.addNextIntent(resultIntent);
					    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					    	mBuilder.setContentIntent(resultPendingIntent);
					    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					    	// mId allows you to update the notification later on.
					    	mNotificationManager.notify(GM.NOTIFICATION_ID_REPORTING_STOP, mBuilder.build());
						    
							llLocation.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.light_red));
							
							//Set reporting status
							reporting = false;
							
							//Change button text
							tvLocationUser.setText(getApplicationContext().getString(R.string.location_nobody));
							btLocationReport.setText(getApplicationContext().getString(R.string.button_location));
						}
					}
				});
				btNotification.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
		                startActivity(intent);
					}
				});
				
				break;
		}
	}
	
	/**
	 * Submits a request to the server for a new user. 
	 * If it works, also sets a local preference indicating the user code.
	 * 
	 * @param code The random code assigned to the user.
	 * @param name The user name.
	 * @param phone The user phone.
	 * @return true if the submission was done, false otherwise.
	 */
	private boolean uploadUser(String code, String name, String phone){
		boolean result = false;
		
		//Check if all the fields are OK
		if (phone.equals("") == false && name.equals("") == false && code.equals("") == false){
			
			//Fetches the remote URL, triggering the admin insertion in the database.
			FetchURL fetch = new FetchURL();
			fetch.Run("http://inigovalentin.com/gm/app/upload/admin.php?name=" + name + "&code=" + code + "&phone=" + phone);
			
			//Read the output to see if the submission was done.
			List<String> lines = fetch.getOutput();
			for(int i = 0; i < lines.size(); i++)
				if (lines.get(i).length() >= 9){
					if (lines.get(i).substring(0, 6).equals("<user>"))
						result = true;
					}
			if (result){
				Toast.makeText(getApplicationContext(), getString(R.string.toast_submitted), Toast.LENGTH_LONG).show();
				//Set the preference
				SharedPreferences preferences = getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
			    editor.putString(GM.USER_CODE, code);
			    editor.putString(GM.USER_NAME, name);
			    editor.putInt(GM.ACCOUNT_STATUS, GM.ACCOUNT_PENDING);
			    editor.commit();
			}
			else{
				Toast.makeText(getApplicationContext(), getString(R.string.toast_not_submitted), Toast.LENGTH_LONG).show();
			}
			return result;
		}
		else
			return false;
	}
}
