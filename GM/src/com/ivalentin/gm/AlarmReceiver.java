package com.ivalentin.gm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Manages alarms to perform actions in the background, such as sync and receive notifications.
 * 
 * @author Iñigo Valentin
 *
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
	
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    /**
     * Actions to be performed when an alarm is received. 
     * Performs a full sync, and gets the pending notifications.
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        
    	//Synchronize database.
    	new Sync(context).execute();

		//The received web page, line by line.
		List<String> output = null;
		
		//Boolean that will prevent the process to go on if something goes wrong.
		boolean success = true;
		    	
		//Open the preferences to be available several times later.
		SharedPreferences settings = context.getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		
		FetchURL fu;
		
		//Check if the user wants to receive notifications
		if (settings.getInt(GM.PREF_NOTIFICATION , 1) == 1){
			success = true;
			
			//Get the file
			try{
				fu = new FetchURL();
				fu.Run("http://inigovalentin.com/gm/app/notification.php"); 
				//All the info
				output = fu.getOutput();
			}
			catch(Exception ex){
				Log.e("Notification error", "Error fetching remote file: " + ex.toString());
				success = false;
			}
			if (success){
				
				//Get the notification manager ready
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				//Variables to create intents for each notification
				Intent resultIntent;
				TaskStackBuilder stackBuilder;
				
				//Parse the contents of the page
				try{
					for(int i = 0; i < output.size(); i++){
						String line = output.get(i);
						if(line.length() > 14){
							if(line.substring(0, 14).equals("<notification>")){
								String id = line.substring(line.indexOf("<id>") + 4, line.indexOf("</id>"));
								String type = line.substring(line.indexOf("<type>") + 6, line.indexOf("</type>"));
								String title = line.substring(line.indexOf("<title>") + 7, line.indexOf("</title>"));
								String text = line.substring(line.indexOf("<text>") + 6, line.indexOf("</text>"));
								String gm = line.substring(line.indexOf("<gm>") + 4, line.indexOf("</gm>"));
								//Send notification
								//The notification has already been red?
								if (settings.getInt(GM.NOTIFICATION_SEEN + id, 0) == 0){
									//Does the user wants GM notifications?
									if (gm.equals('1') && settings.getInt(GM.PREF_NOTIFICATION_GM , 1) == 0){
										//Don't send notification.
									}
									else{
										//Send the notification.
										NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
								    		.setSmallIcon(R.drawable.ic_notification)
								    		.setContentTitle(title)
								    		.setAutoCancel(true)
								    		.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
								    		.setVibrate((new long[] { 400, 400, 400}))
								    		.setColor(context.getResources().getColor(R.color.background_notification))
								    		.setSubText(context.getString(R.string.app_name))
								    		.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
								    		.setContentText(text);
										
								    	// Creates an intent for an Activity to be launched from the notification.
								    	resultIntent = new Intent(context, MainActivity.class);
								    	resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
								    		
								    	//Set extras depending on type.
								    	resultIntent.putExtra(GM.EXTRA_TEXT, text);
								    	resultIntent.putExtra(GM.EXTRA_TITLE, title);
								    	resultIntent.putExtra(GM.EXTRA_ACTION, type);
								    	
								    	//Add the intent to the notification.
								    	stackBuilder = TaskStackBuilder.create(context);
								    	stackBuilder.addParentStack(MainActivity.class);
								    	
								    	// Adds the Intent that starts the Activity to the top of the stack.
								    	stackBuilder.addNextIntent(resultIntent);
								    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
								    	mBuilder.setContentIntent(resultPendingIntent);
								    	
								    	//Actually send the notification.
								    	mNotificationManager.notify(Integer.parseInt(id), mBuilder.build());
								    	
								    	//Mark as notified
								    	SharedPreferences.Editor editor = settings.edit();
								    	editor.putInt(GM.NOTIFICATION_SEEN + id, 1);
								    	editor.commit();
									}
								}
							}
						}
					}
				}
				catch(Exception ex){
					Log.e("Notification error", "Error parsing remote info: " + ex.toString());
					success = false;
				}
			}
    	}
		
		//Get location
		boolean result = false;
		fu = new FetchURL();
		fu.Run("http://inigovalentin.com/gm/app/location.php");
		String lat = "", lon = "";
		output = fu.getOutput();
		for(int i = 0; i < output.size(); i++){
			if (output.get(i).length() >= 25){
				if (output.get(i).substring(0, 25).equals("<location>none</location>"))
					result = false;
			}
			if (output.get(i).length() >= 6){
				if (output.get(i).substring(0, 5).equals("<lat>")){
					lat = output.get(i).substring(5, output.get(i).length()-6);
					result = true;
				}
				if (output.get(i).substring(0, 5).equals("<lon>")){
					lon = output.get(i).substring(5, output.get(i).length()-6);
					result = true;
				}
			}
		}
		if (result){
			SharedPreferences.Editor editor = settings.edit();
	    	editor.putString(GM.PREF_GM_LATITUDE, lat);
	    	editor.putString(GM.PREF_GM_LONGITUDE, lon);
	    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	    	Date date = new Date();
	    	editor.putString(GM.PREF_GM_LOCATION, dateFormat.format(date));
	    	editor.commit();
		}
    }

    /**
     * Sets a repeating alarm. 
     * When the alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context The context of the app
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        
        //Set the alarm cycle.
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, GM.PERIOD_SYNC, GM.PERIOD_SYNC, alarmIntent);
        
        // Enable SampleBootReceiver to automatically restart the alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);           
    }

    /**
     * Cancels the alarm.
     * @param context
     */
    public void cancelAlarm(Context context) {
    	
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) 
            alarmMgr.cancel(alarmIntent);
        
        //Disable SampleBootReceiver so that it doesn't automatically restart the alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    
}
