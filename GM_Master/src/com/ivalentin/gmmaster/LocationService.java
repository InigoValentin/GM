package com.ivalentin.gmmaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class LocationService extends Service {

	// An alarm for rising in special times to fire the pendingIntentPositioning
	private AlarmManager alarmManagerPositioning;
	
	// A PendingIntent for calling a receiver in special times
	public PendingIntent pendingIntentPositioning;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//Set the alarm
		alarmManagerPositioning = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intentToFire = new Intent(GM.ACTION_REFRESH_SCHEDULE_ALARM);
		intentToFire.putExtra(GM.ALARM_COMMAND, GM.ALARM_ACTION);
		pendingIntentPositioning = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		try {
			long interval = GM.LOCATION_UPDATE_INTERVAL;
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long timetoRefresh = SystemClock.elapsedRealtime();
			alarmManagerPositioning.setInexactRepeating(alarmType, timetoRefresh, interval, pendingIntentPositioning);
		}
		catch (NumberFormatException e) {
			Log.e("Error running service: ", e.getMessage().toString());
		}
		catch (Exception e) {
			Log.e("Error running service: ", e.getMessage().toString());
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		this.alarmManagerPositioning.cancel(pendingIntentPositioning);
		LocationAlarm.stopLocationListener();
	}
}