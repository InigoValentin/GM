package com.ivalentin.gmmaster;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Activity that allows the user to send notifications that will be received by the client apps.
 * 
 * @author Iñigo Valentin
 *
 */
public class NotificationActivity extends Activity{
	
	/**
	 * Run when the activity is launched. 
	 * 
	 * Assigns elements and click listeners.
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//Set content
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);
		
		//Assign element
		final EditText etTitle = (EditText) findViewById(R.id.et_notification_title);
		final EditText etText = (EditText) findViewById(R.id.et_notification_text);
		final EditText etHour = (EditText) findViewById(R.id.et_notification_h);
		final EditText etMinute = (EditText) findViewById(R.id.et_notification_m);
		final RadioButton[] rbScope = new RadioButton[2];
		rbScope[0] = (RadioButton) findViewById(R.id.rb_notification_scope_all);
		rbScope[1] = (RadioButton) findViewById(R.id.rb_notification_scope_gm);
		final RadioButton[] rbType = new RadioButton[3];
		rbType[0] = (RadioButton) findViewById(R.id.rb_notification_type_text);
		rbType[1] = (RadioButton) findViewById(R.id.rb_notification_type_gm);
		rbType[2] = (RadioButton) findViewById(R.id.rb_notification_type_schedule);
		Button btSend = (Button) findViewById(R.id.bt_notification_send);
		Button btBack = (Button) findViewById(R.id.bt_notification_back);
		
		btBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { finish(); }
		});
		
		btSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = etTitle.getText().toString();
				String text = etText.getText().toString();
				int duration = Integer.parseInt(etHour.getText().toString()) * 60 + Integer.parseInt(etMinute.getText().toString());
				String type = "";
				int gm = 0;
				if (rbType[0].isChecked())
					type = GM.NOTIFICATION_TYPE_TEXT;
				else if (rbType[1].isChecked())
					type = GM.NOTIFICATION_TYPE_GM;
				else if (rbType[2].isChecked())
					type = GM.NOTIFICATION_TYPE_SCHEDULE;
				if (rbScope[0].isChecked())
					gm = 1;
				send(title, text, duration, type, gm);
				
			}
		});
	}
	
	/**
	 * Uploads a notification to the server, after checking that every field is OK.
	 * 
	 * If a field is wrong, a Toast will be shown and it will stop.
	 * 
	 * @param title Title of the notification.
	 * @param text Text of the notifications.
	 * @param duration Minutes within the notification can be received by clients.
	 * @param type Action to perform when the notification is red.
	 * @param gm Indicates if the notification is intended only for members of Gasteizko Margolariak.
	 * @return
	 */
	private boolean send(String title, String text, int duration, String type, int gm){
		
		//Check fields
		if (title.equals("")){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_error_title), Toast.LENGTH_LONG).show();
			return false;
		}
		if (text.equals("")){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_error_text), Toast.LENGTH_LONG).show();
			return false;
		}
		if (duration < 15 || duration > 2880){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_error_duration), Toast.LENGTH_LONG).show();
			return false;
		}
		SharedPreferences preferences = getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		String code = preferences.getString(GM.USER_CODE, "");
		if (code.equals("")){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_error_unauthorized), Toast.LENGTH_LONG).show();
			return false;
		}
		
		String user = preferences.getString(GM.USER_NAME, "");
		if (user.equals("")){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_error_unauthorized), Toast.LENGTH_LONG).show();
			return false;
		}
		
		title = title.replace(" ", "%20");
		text = text.replace(" ", "%20");
		
		//Call server
		boolean result = false;
		//Fetches the remote URL, triggering the admin insertion in the database.
		FetchURL fetch = new FetchURL();
		fetch.Run(GM.SERVER + "app/upload/notification.php?user=" + user + "&code=" + code + "&title=" + title + "&text=" + text + "&type=" + type + "&gm=" + gm + "&duration=" + duration);
		//Read the output to see if the submission was done.
		List<String> lines = fetch.getOutput();
		for(int i = 0; i < lines.size(); i++)
			if (lines.get(i).length() >= 19){
				if (lines.get(i).substring(0, 19).equals("<status>ok</status>"))
					result = true;
				}
		if (result){
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_submitted), Toast.LENGTH_LONG).show();
			finish();
		}
		else{
			Toast.makeText(getApplicationContext(), getString(R.string.toast_notification_not_submitted), Toast.LENGTH_LONG).show();
		}
		return result;
	}
}
