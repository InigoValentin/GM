package com.ivalentin.gm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * AsyncTask that synchronizes the online database to the device.
 * Is run every time the app is started, and in the bacground.
 * 
 * @author Inigo Valentin
 *
 */
public class Sync extends AsyncTask<Void, Void, Void> {
	
	private Context myContextRef;
	private ProgressBar pbSync;
	private Dialog dialog;
	private MainActivity activity;
	private boolean fg;
	
	
	/**
	 * Things to do before sync. Namely, displaying a spinning progress bar.
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute(){
		if (pbSync != null)
			pbSync.setVisibility(View.VISIBLE);
		if (dialog != null){
			dialog.show();
		}
		Log.d("Sync", "Starting full sync");
	}
	
	/**
	 * Things to do after sync. Namely, hiddng the spinning progress bar.
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPostExecute(Void v){
		if (pbSync != null)
			pbSync.setVisibility(View.INVISIBLE);
		if (dialog != null){
			dialog.dismiss();
			//Check db version agan
			SharedPreferences preferences = myContextRef.getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
			if (preferences.getInt(GM.PREF_DB_VERSION, GM.DEFAULT_PREF_DB_VERSION) == GM.DEFAULT_PREF_DB_VERSION){
				//Create a dialog
				final Dialog dial = new Dialog(activity);
				dial.setCancelable(false);
				
				//Set up the window
				dial.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dial.setContentView(R.layout.dialog_sync_failed);
				
				//Set button
				Button btClose = (Button) dial.findViewById(R.id.bt_dialog_sync_failed_close);
				btClose.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						dial.dismiss();
						activity.finish();
					}
				});
				
				//Show the dialog
				dial.show();
			}
			else{
				activity.loadSection(GM.SECTION_HOME, false);
			}
		}
		Log.d("Sync", "Full sync finished");
	}
	
    /**
     * Called when the AsyncTask is created.
     * 
     * @param myContextRef The Context of the calling activity.
     * @param pb The progress bar that will be shown while the sync goes on.
     */
    public Sync(Activity myContextRef, ProgressBar pb) {
        this.myContextRef = myContextRef;
        dialog = null;
        pbSync = pb;
        fg = true;
    }
    
    /**
     * Called when the AsyncTask is created. 
     * This constructor is intended to use only in the first sync, 
     * because a dialog will block the UI.
     * 
     * @param myContextRef The Context of the calling activity.
     * @param d Dialog of the initial sync
     * @param pb The progress bar that will be shown while the sync goes on.
     * @param activity The calling MainActvity
     */
    public Sync(Activity myContextRef, ProgressBar pb, Dialog d, MainActivity activity) {
    	this.dialog = d;
    	this.activity = activity;
        this.myContextRef = myContextRef;
        pbSync = pb;
        fg = true;
    }
    
    /**
     * Called when the AsyncTask is created.
     * 
     * @param myContextRef The Context of the calling activity.
     * @param pb The progress bar that will be shown while the sync goes on.
     */
    public Sync(Context context) {
        this.myContextRef = context;
        pbSync = null;
        fg = false;
    }
	
	/**
	 * The sweet stuff. Actually performs the sync. 
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		
		//Gets the remote page.
    	FetchURL fu;
    	
    	//Will contain the remote database version.
		int version = 0;
		boolean versionSet = false;
		
		//The lines of the received web page.
		List<String> o = null;
		
		//List of SQL queries to be performed, as received from the web page.
		List<String> query = new ArrayList<String>();
		
		//Preferences.
		SharedPreferences preferences = myContextRef.getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		
		//Boolean that will prevent the process to go on if something goes wrong.
		boolean success = true;
		
		//Get the file
		try{
			String user = preferences.getString(GM.USER_NAME, "");
			String code = preferences.getString(GM.USER_CODE, "");
			fu = new FetchURL();
			fu.Run(GM.SERVER + "app/sync.php?user=" + user + "&code=" + code + "&fg=" + fg); 
			//All the info
			o = fu.getOutput();
		}
		catch(Exception ex){
			Log.e("Sync error", "Error fetching remote file: " + ex.toString());
			success = false;
		}
		if (success){
			
			//Parse the contents of the page
			try{
				for(int i = 0; i < o.size(); i++){
					if (o.get(i).length() >= 9){
						if (o.get(i).substring(0, 9).equals("<version>")){
							version = Integer.parseInt(o.get(i).substring(9, o.get(i).length() - 10));
							versionSet = true;
							Log.d("Remote database version", Integer.toString(version));
						}
						if (o.get(i).substring(0, 7).equals("<query>")){
							query.add(o.get(i).substring(7, o.get(i).length() - 8));
						}
					}
				}
			}
			catch(Exception ex){
				Log.e("Sync error", "Error parsing remote info: " + ex.toString());
				success = false;
			}
		}
		if (success){
			
			//If the database is out dated, update it.
			if (versionSet && version > preferences.getInt(GM.PREF_DB_VERSION, GM.DEFAULT_PREF_DB_VERSION)){
				try{
					SQLiteDatabase db = myContextRef.openOrCreateDatabase(GM.DB_NAME, Activity.MODE_PRIVATE, null);
					Log.d("Sync query", "DELETE FROM event;");
					db.execSQL("DELETE FROM event;");
					db.delete("event", null, null);
					Log.d("Sync query", "DELETE FROM people;");
					db.execSQL("DELETE FROM people;");
					db.delete("event", null, null);
					Log.d("Sync query", "DELETE FROM place;");
					db.execSQL("DELETE FROM place;");
					db.delete("event", null, null);
					Log.d("Sync query", "DELETE FROM day;");
					db.execSQL("DELETE FROM day;");
					db.delete("event", null, null);
					Log.d("Sync query", "DELETE FROM offer;");
					db.execSQL("DELETE FROM offer;");
					for(int i = 0; i < query.size(); i++){
						Log.d("Sync query", query.get(i));
						db.execSQL(query.get(i));
					}
					db.close();
					
					//Set current database version in preferences.
					SharedPreferences.Editor prefEditor = preferences.edit();
			        prefEditor.putInt(GM.PREF_DB_VERSION, version);
			        prefEditor.commit();
				}
				catch(Exception ex){
					Log.e("Sync error", "Error updating info: " + ex.toString());
					success = false;
				}
			}
		}
		return null;
    	
	}
}
