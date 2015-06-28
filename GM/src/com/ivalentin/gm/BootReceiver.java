package com.ivalentin.gm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that receives the BOOT_COMPLETED event and sets an alarm.
 * 
 * @author Iñigo Valentin
 *
 */
public class BootReceiver extends BroadcastReceiver {
	
	//The AlarmReceiver
	private AlarmReceiver alarm = new AlarmReceiver();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//If the event is BOOT_COMPLETED...
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
			//... set the alarm.
			alarm.setAlarm(context);
	}
}