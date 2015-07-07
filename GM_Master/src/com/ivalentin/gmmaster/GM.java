package com.ivalentin.gmmaster;

/**
 * Final class that contains useful static values to be used across the app.
 * 
 * @author Iñigo Valentin
 *
 */
public final class GM {
	
	/**
	 * Address where the web server is
	 */
	static final String SERVER = "http://inigovalentin.com/gm/";
	
	/**
	 * String with the name of the preference that indicates if the user is reporting.
	 */
	static final String CURRENTLY_REPORTING = "currentlyReporting";
	
	/**
	 * Default value of the CURRENTLY_REPORTING preference.
	 */
	static final boolean DEFAULT_CURRENTLY_REPORTING = false;
	
	/**
	 * Name of the preference group for the app.
	 */
	static final String PREF = "gmmasterpreferences";
	
	/**
	 * Name of the preference to store the user code with.
	 */
	static final String USER_CODE = "prefcode";
	
	/**
	 * Name of the preference to store the user name with.
	 */
	static final String USER_NAME = "prefname";
	
	/**
	 * Name of the preference to store the state of the user account.
	 */
	static final String ACCOUNT_STATUS = "accountstatus";
	
	/**
	 * The account hasn't been created yet.
	 */
	static final int ACCOUNT_UNSUBMITTED = 0;

	/**
	 * The account has been created but it's not activated.
	 */
	static final int ACCOUNT_PENDING = 1;
	
	/**
	 * The account has been activated.
	 */
	static final int ACCOUNT_ACTIVE = 2;
	
	/**
	 * Name of the preference to store the database version with.
	 */
	static final String PREF_USER_NAME = "prefname";

	/**
	 * Indicates that a notification will be shown as text.
	 */
	static final String NOTIFICATION_TYPE_TEXT = "text";
	
	/**
	 * Indicates that a notification will open the GM schedule.
	 */
	static final String NOTIFICATION_TYPE_GM = "gm";
	
	/**
	 * Indicates that a notification will open the official schedule.
	 */
	static final String NOTIFICATION_TYPE_SCHEDULE = "schedule";
	
	/**
	 * Id for notification "Currently reporting"
	 */
	static final int NOTIFICATION_ID_REPORTING = 0;
	
	/**
	 * Id for notification "Reporting stopped".
	 */
	static final int NOTIFICATION_ID_REPORTING_STOP = 1;
	
	/**
	 * Id for notification "Another user is reporting".
	 */
	static final int NOTIFICATION_ID_REPORTING_OVERRIDE  = 2;
	
	/**
	 * Id for notification "No GPS".
	 */
	static final int NOTIFICATION_ID_REPORTING_GPS  = 3;
	
	/**
	 * Command for the location alarm.
	 */
	static final String ALARM_COMMAND = "SENDER";
	
	/**
	 * Document received by activity from location alarm.
	 */
	static final int ALARM_ACT_DOCUMENT = 0;
	
	/**
	 * Action to be performed by the location alarm.
	 */
	static final int ALARM_ACTION = 1;
	
	/**
	 * Minimum time, in seconds, between location updates.
	 */
	static final int LOCATION_MIN_TIME_REQUEST = 30 * 1000;
	
	/**
	 * Interval, in seconds, between location updates are requested and sent to the server.
	 */
	static final int LOCATION_UPDATE_INTERVAL = 3 * 60 * 1000;
	
	/**
	 * Minimum distance, in meters, between location updates.
	 */
	static final int LOCATION_MIN_DISTANCE_REQUEST = 10;
	
	/**
	 * Location alarm action.
	 */
	public static final String ACTION_REFRESH_SCHEDULE_ALARM = "com.ivalentin.gmmaster.ACTION_REFRESH_SCHEDULE_ALARM";
}
