package com.ivalentin.gmmaster;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Manages alarms to perform actions in the background, such as sync and receive notifications.
 * 
 * @author Iñigo Valentin
 *
 */
public class LocationAlarm extends BroadcastReceiver {
	
	//The current location.
	private static Location currentLocation;
	
	//Last known location.
	private static Location prevLocation;
	
	//Location provider
	private String provider = LocationManager.GPS_PROVIDER;
	
	//The location manager
	private static LocationManager locationManager;
	
	//A location listener
	private static LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras){
			try {
				String strStatus = "";
				switch (status) {
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					strStatus = "GPS_EVENT_FIRST_FIX";
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					strStatus = "GPS_EVENT_SATELLITE_STATUS";
					break;
				case GpsStatus.GPS_EVENT_STARTED:
					strStatus = "GPS_EVENT_STARTED";
					break;
				case GpsStatus.GPS_EVENT_STOPPED:
					strStatus = "GPS_EVENT_STOPPED";
					break;
				default:
					strStatus = String.valueOf(status);
					break;
				}
				Log.d("GPS status", strStatus);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onLocationChanged(Location location) {
			try {
				Log.d("Location changed", location.getLatitude() + ", " + location.getLongitude());
				gotLocation(location);
			}
			catch (Exception e) {
			}
		}
	};

	/**
     * Actions to be performed when an alarm is received. 
     * Upload the location.
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d("Location request", "New request received by receiver");
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, GM.LOCATION_MIN_TIME_REQUEST, GM.LOCATION_MIN_DISTANCE_REQUEST, locationListener); //TODO: 0 -> 5
			Location gotLoc = locationManager.getLastKnownLocation(provider);
			gotLocation(gotLoc);
			
			//Upload location
			SharedPreferences preferences = context.getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
			String user = preferences.getString(GM.USER_NAME, "");
			String code = preferences.getString(GM.USER_CODE, "");
			//Fetches the remote URL, triggering the location insertion in the database.
			FetchURL fetch = new FetchURL();
			fetch.Run("http://inigovalentin.com/gm/app/upload/location.php?user=" + user + "&code=" + code + "&lat=" + gotLoc.getLatitude() + "&lon=" + gotLoc.getLongitude() + "&manual=0");
			//Read the output to see if the submission was done.
			List<String> lines = fetch.getOutput();
			for(int i = 0; i < lines.size(); i++)
				if (lines.get(i).length() >= 21){
					if (lines.get(i).substring(0, 21).equals("<status>sent</status>")){
						Log.d("Location uploaded", gotLoc.getLatitude() + ", " + gotLoc.getLongitude());
					}
					if (lines.get(i).substring(0, 21).equals("<status>stop</status>")){
						Log.d("Location report stop", "Location is now being reported by other admin");
						//Cancel alarm
						Intent in = new Intent(context, LocationAlarm.class);
			            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1253, in, PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);
						AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					    alarmManager.cancel(pendingIntent);
					    //Cancel notification
				        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				        nMgr.cancel(GM.NOTIFICATION_ID_REPORTING);
				        nMgr.cancelAll();
						//TODO: Show new notification
					}
				}
				
			
		} else {
			//TODO: Stop reporting and show notification
			//Toast t = Toast.makeText(context, "please turn on GPS",	Toast.LENGTH_LONG);
			//t.setGravity(Gravity.CENTER, 0, 0);
			//t.show();
			Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(settingsIntent);
		}
	}

	/**
	 * Run when the location is determined.
	 * 
	 * @param location The new locaton.
	 */
	private static void gotLocation(Location location) {
		prevLocation = currentLocation == null ? null : new Location(currentLocation);
		currentLocation = location;
		if (isLocationNew()) {
			OnNewLocationReceived(location);
			Log.d("Location aquired", location.getLatitude() + ", " + location.getLongitude());
			//stopLocationListener(); //TODO: Maybe uncomment
		}
	}

	/**
	 * Checks if the location is the same as the previous one, or a new one.
	 * 
	 * @return true if it is a new location, false otherwise.
	 */
	private static boolean isLocationNew() {
		if (currentLocation == null) {
			return false;
		} else if (prevLocation == null) {
			return true;
		} else if (currentLocation.getTime() == prevLocation.getTime()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Stops the location listener.
	 */
	public static void stopLocationListener() {
		locationManager.removeUpdates(locationListener);
		Log.d("Location provider", "Provider stopped");
	}

	// listener ----------------------------------------------------
	static ArrayList<OnNewLocationListener> arrOnNewLocationListener = new ArrayList<OnNewLocationListener>();

	/**
	 * Allows the user to set a OnNewLocationListener outside of this class 
	 * and react to the event.
	 * 
	 * @param listener The OnNewLocationListener to be set.
	 */
	public static void setOnNewLocationListener(OnNewLocationListener listener) {
		arrOnNewLocationListener.add(listener);
	}

	/**
	 * Clears the location listener.
	 * 
	 * @param listener The OnNewLocationListener to be unset.
	 */
	public static void clearOnNewLocationListener(OnNewLocationListener listener) {
		arrOnNewLocationListener.remove(listener);
	}

	/**
	 * Called after the new location is received.
	 * 
	 * @param location The new location.
	 */
	private static void OnNewLocationReceived(Location location) {
		// Check if the Listener was set, otherwise we'll get an Exception
		// when we try to call it
		if (arrOnNewLocationListener != null) {
			// Only trigger the event, when we have any listener
			for (int i = arrOnNewLocationListener.size() - 1; i >= 0; i--) {
				arrOnNewLocationListener.get(i).onNewLocationReceived(location);
			}
		}
	}
	
}
