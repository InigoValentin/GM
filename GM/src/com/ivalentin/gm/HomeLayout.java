package com.ivalentin.gm;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnShowListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Section that will be seen when the app is started. 
 * Contains info from almost every other section.
 * 
 * @author Iñigo Valentin
 * 
 * @see Fragment
 *
 */
public class HomeLayout extends Fragment implements LocationListener, OnMapReadyCallback{

	//The location manager
	LocationManager locationManager;
	Location listener;
	
	//The location of the user
	private double[] coordinates = new double[2];
	
	//Layouts for each section
	private LinearLayout llSchedule, llGm, llAround, llLocation;
	private LinearLayout llScheduleContent, llGmContent, llAroundContent;
	private LinearLayout llScheduleLink, llGmLink, llAroundLink, llLocationLink, llJoinLink;
	
	//TextView showing the distance of GM
	private TextView tvLocation;
	
	//The main view
	private View v;
	
	//Map stuff for the dialog
	private MapView mapView;
	private Bundle bund;
	private GoogleMap map;
	private LatLng location;
	
	/**
	 * Run when the fragment is inflated.
	 * Assigns views, gets the date and does the first call to the {@link populate function}.
	 * 
	 * @param inflater A LayoutInflater to handle the views
	 * @param container The parent View
	 * @param sanvedInstanceState Bundle with the saved state
	 * 
	 * @return The fragment view
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@SuppressLint("InflateParams") //Throws unknown error when done properly.
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
		
		//Database
		SQLiteDatabase db;
		Cursor cursor;
		
		//Layouts to be adding rows
		LinearLayout entry;
		
		//An inflater
        LayoutInflater factory = LayoutInflater.from(getActivity());
		
		//Load the layout.
		final View view = inflater.inflate(R.layout.fragment_layout_home, null);
		this.v = view;
				
		//Set Location manager
		locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, this);
		
		//Set the title
		((MainActivity) getActivity()).setSectionTitle(view.getContext().getString(R.string.menu_home));
	    
		//Get the location passed from MainActivity so we dont have to wait for it to be aquired.
		Bundle bundle = this.getArguments();
		coordinates[0] = bundle.getDouble("lat", 0);
		coordinates[1] = bundle.getDouble("lon", 0);
		
		//Get current date
		Calendar cal;
		Date maxDate;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd-", Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
		Date date = new Date();
		
		//Assign the layouts
		llSchedule = (LinearLayout) view.findViewById(R.id.ll_home_section_schedule);
		llGm = (LinearLayout) view.findViewById(R.id.ll_home_section_gm);
		llAround = (LinearLayout) view.findViewById(R.id.ll_home_section_around);
		llLocation = (LinearLayout) view.findViewById(R.id.ll_home_section_location);
		llScheduleContent = (LinearLayout) view.findViewById(R.id.ll_home_section_schedule_content);
		llGmContent = (LinearLayout) view.findViewById(R.id.ll_home_section_gm_content);
		llAroundContent = (LinearLayout) view.findViewById(R.id.ll_home_section_around_content);
		llScheduleLink = (LinearLayout) view.findViewById(R.id.ll_home_section_schedule_link);
		llGmLink = (LinearLayout) view.findViewById(R.id.ll_home_section_gm_link);
		llAroundLink = (LinearLayout) view.findViewById(R.id.ll_home_section_around_link);
		llLocationLink = (LinearLayout) view.findViewById(R.id.ll_home_section_location_link);
		llJoinLink = (LinearLayout) view.findViewById(R.id.ll_home_section_join_link);
		
		//Set onClick events for links
		llScheduleLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).loadSection(GM.SECTION_SCHEDULE, false);
			}
		});
		
		llGmLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).loadSection(GM.SECTION_GM_SCHEDULE, false);
			}
		});
		
		llAroundLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).loadSection(GM.SECTION_AROUND, false);
			}
		});
		
		llLocationLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).loadSection(GM.SECTION_LOCATION, false);
			}
		});
		
		llJoinLink.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				
				//Here, we are going to show a dialog with all info related to the prices
				
				//Create the dialog
				final Dialog dialog = new Dialog(getActivity());
				
				//Set window
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_prices);
		 
				//Assign dialog views
				TextView[] tvDayName = new TextView[6];
				TextView[] tvDayPrice = new TextView[6];
				TextView[] tvOfferName = new TextView[3];
				TextView[] tvOfferPrice = new TextView[3];
				CheckBox[] cbDayName = new CheckBox[6];
				tvDayName[0] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_0);
				tvDayName[1] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_1);
				tvDayName[2] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_2);
				tvDayName[3] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_3);
				tvDayName[4] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_4);
				tvDayName[5] = (TextView) dialog.findViewById(R.id.tv_prices_day_name_5);
				tvDayPrice[0] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_0);
				tvDayPrice[1] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_1);
				tvDayPrice[2] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_2);
				tvDayPrice[3] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_3);
				tvDayPrice[4] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_4);
				tvDayPrice[5] = (TextView) dialog.findViewById(R.id.tv_prices_day_price_5);
				tvOfferName[0] = (TextView) dialog.findViewById(R.id.tv_prices_offer_name_0);
				tvOfferName[1] = (TextView) dialog.findViewById(R.id.tv_prices_offer_name_1);
				tvOfferName[2] = (TextView) dialog.findViewById(R.id.tv_prices_offer_name_2);
				tvOfferPrice[0] = (TextView) dialog.findViewById(R.id.tv_prices_offer_price_0);
				tvOfferPrice[1] = (TextView) dialog.findViewById(R.id.tv_prices_offer_price_1);
				tvOfferPrice[2] = (TextView) dialog.findViewById(R.id.tv_prices_offer_price_2);
				cbDayName[0] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_0);
				cbDayName[1] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_1);
				cbDayName[2] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_2);
				cbDayName[3] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_3);
				cbDayName[4] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_4);
				cbDayName[5] = (CheckBox) dialog.findViewById(R.id.cb_dialog_prices_5);
				Button btClose = (Button) dialog.findViewById(R.id.bt_dialog_prices_close);
				Button btContact = (Button) dialog.findViewById(R.id.bt_dialog_prices_contact);
				
				//Listener for the contact dialog
				btContact.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						//TODO: test this 
						//Mail intent
						Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", v.getContext().getString(R.string.app_email), null));
						intent.putExtra(Intent.EXTRA_SUBJECT, v.getContext().getString(R.string.prices_contact_subject));
						startActivity(Intent.createChooser(intent, null));
					}
				});
				
				//Open database
				SQLiteDatabase db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
				Cursor cursor;
				
				//Set up icons
				Drawable dayIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.price_day, null);
				dayIcon.setBounds(0, 0, (int) (tvDayName[0].getTextSize() * 1.5), (int) (tvDayName[0].getTextSize() * 1.5));
				Drawable offerIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.price_offer, null);
				offerIcon.setBounds(0, 0, (int) (tvOfferName[0].getTextSize() * 1.5), (int) (tvOfferName[0].getTextSize() * 1.5));
				
				//Set TextViews for days
				int i = 0;
				cursor = db.rawQuery("SELECT name, price FROM day ORDER BY id;", null);
				while (cursor.moveToNext() && i < 6){
					tvDayName[i].setText(cursor.getString(0));
					tvDayName[i].setCompoundDrawables(dayIcon, null, null, null);
					tvDayName[i].setCompoundDrawablePadding(5);
					tvDayPrice[i].setText(cursor.getString(1) + " " + getResources().getString(R.string.eur));
					
					//Items for the price calculator
					cbDayName[i].setText(cursor.getString(0));
					cbDayName[i].setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) { calculatePrice(dialog); }				
					});
					
					i ++;
				}
				
				//Set TextViews for offers
				i = 0;
				cursor = db.rawQuery("SELECT name, price FROM offer ORDER BY id;", null);
				while (cursor.moveToNext() && i < 3){
					tvOfferName[i].setText(cursor.getString(0));
					tvOfferName[i].setCompoundDrawables(offerIcon, null, null, null);
					tvOfferName[i].setCompoundDrawablePadding(5);
					tvOfferPrice[i].setText(cursor.getString(1) + " " + getResources().getString(R.string.eur));
					i ++;
				}
				
				//Set close button			
	        	btClose.setOnClickListener(new OnClickListener() {
	    			@Override
	    			public void onClick(View v) {
	    				dialog.dismiss();
	    			}
	    		});
				
				//Show the dialog
				dialog.show();
					
			}
		});
		
		//Populate the schedule section
		db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.HOUR_OF_DAY, 24); 
	    maxDate = cal.getTime();
		cursor = db.rawQuery("SELECT event.id, event.name, event.description, event.place, place.id, place.name, event.start FROM event, place WHERE schedule = 1 AND place.id = event.place AND start BETWEEN '" + dateFormat.format(date) + "' AND '" + dateFormat.format(maxDate) + "' ORDER BY start LIMIT 2;", null);
		if (cursor.getCount() == 0){
			llSchedule.setVisibility(View.GONE);
		}
		else{
			TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime, tvRowId;
			while (cursor.moveToNext()){
				
				//Create a new row
	        	entry = (LinearLayout) factory.inflate(R.layout.row_home_schedule, null);
	        	
	        	//Set id
	        	tvRowId = (TextView) entry.findViewById(R.id.tv_row_home_schedule_id);
	        	tvRowId.setText(Integer.toString(cursor.getInt(0)));
	        	
	        	//Set title
	        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_schedule_title);
	        	tvRowName.setText(cursor.getString(1));
	        	
	        	//Set description
	        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_schedule_description);
	        	tvRowDescription.setText(cursor.getString(2));
	        	
	        	//Set place
	        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_schedule_place);
	        	tvRowPlace.setText(cursor.getString(5));
	        	
	        	//Set time
	        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_schedule_time);
	        	Date tm;
				try {
					tm = dateFormat.parse(cursor.getString(6));
					if (dayFormat.format(tm).equals(dayFormat.format(date)))
		        		tvRowTime.setText(view.getContext().getString(R.string.today) + " " + timeFormat.format(tm));
		        	else
		        		tvRowTime.setText(view.getContext().getString(R.string.tomorrow) + " " + timeFormat.format(tm));
				}
				catch (ParseException e) {
					Log.e("Date error", e.toString());
				}
				
				//Set touch event
				entry.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
		            	TextView tvId = (TextView) v.findViewById(R.id.tv_row_home_schedule_id);
		            	int id = Integer.parseInt(tvId.getText().toString());
						showScheduleDialog(id);
					}
	        	});
	        	
				
				//Add the view
	        	llScheduleContent.addView(entry);
			}
			cursor.close();
		}
		
		//Populate the GM schedule section
		db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.HOUR_OF_DAY, 24); 
	    maxDate = cal.getTime();
		cursor = db.rawQuery("SELECT event.id, event.name, event.description, event.place, place.id, place.name, event.start FROM event, place WHERE gm = 1 AND place.id = event.place AND start BETWEEN '" + dateFormat.format(date) + "' AND '" + dateFormat.format(maxDate) + "' ORDER BY start LIMIT 2;", null);
		if (cursor.getCount() == 0){
			llGm.setVisibility(View.GONE);
		}
		else{
			TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime, tvRowId;
			while (cursor.moveToNext()){
				
				//Create a new row
	        	entry = (LinearLayout) factory.inflate(R.layout.row_home_schedule, null);
	        	
	        	//Set id
	        	tvRowId = (TextView) entry.findViewById(R.id.tv_row_home_schedule_id);
	        	tvRowId.setText(Integer.toString(cursor.getInt(0)));
	        	
	        	//Set name
	        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_schedule_title);
	        	tvRowName.setText(cursor.getString(1));
	        	
	        	//Set description
	        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_schedule_description);
	        	tvRowDescription.setText(cursor.getString(2));
	        	
	        	//Set place
	        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_schedule_place);
	        	tvRowPlace.setText(cursor.getString(5));
	        	
	        	//Set time
	        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_schedule_time);
	        	Date tm;
				try {
					tm = dateFormat.parse(cursor.getString(6));
					if (dayFormat.format(tm).equals(dayFormat.format(date)))
		        		tvRowTime.setText(view.getContext().getString(R.string.today) + " " + timeFormat.format(tm));
		        	else
		        		tvRowTime.setText(view.getContext().getString(R.string.tomorrow) + " " + timeFormat.format(tm));
				}
				catch (ParseException e) {
					Log.e("Date error", e.toString());
				}
				
				//Set touch event
				entry.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
		            	TextView tvId = (TextView) v.findViewById(R.id.tv_row_home_schedule_id);
		            	int id = Integer.parseInt(tvId.getText().toString());
						showScheduleDialog(id);
					}
	        	});
	        	
				//Add to the list
	        	llGmContent.addView(entry);
			}
			cursor.close();
		}

		//Populate the around section
		populateAround();
		
		//Populate the location section
		updateLocation();
		
		//Assign buttons in social section
		ImageView ivSocialW = (ImageView) view.findViewById(R.id.iv_social_web);
		ImageView ivSocialM = (ImageView) view.findViewById(R.id.iv_social_mail);
		ImageView ivSocialWA = (ImageView) view.findViewById(R.id.iv_social_whatsapp);
		ImageView ivSocialF = (ImageView) view.findViewById(R.id.iv_social_facebook);
		ImageView ivSocialT = (ImageView) view.findViewById(R.id.iv_social_twitter);
		ImageView ivSocialG = (ImageView) view.findViewById(R.id.iv_social_googleplus);
		ImageView ivSocialY = (ImageView) view.findViewById(R.id.iv_social_youtube);
		ImageView ivSocialI = (ImageView) view.findViewById(R.id.iv_social_instagram);
		
		//Set click listener for the social buttons
		ivSocialW.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_web_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialM.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_mail_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialWA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try{
            		Uri mUri = Uri.parse(getString(R.string.social_whatsapp_link));
            		Intent mIntent = new Intent(Intent.ACTION_SENDTO, mUri);
            		mIntent.setPackage(getString(R.string.social_whatsapp_package));
            		//mIntent.putExtra("sms_body", "");
            		mIntent.putExtra(getString(R.string.social_whatsapp_chat),true);
            		startActivity(mIntent);
            	}
            	catch (Exception e){
            		Toast.makeText(v.getContext(), getString(R.string.social_whatsapp_error), Toast.LENGTH_LONG).show();
            	}
            }
        });
		
		
		
		ivSocialF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_facebook_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialT.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_twitter_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialG.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_googleplus_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialY.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_youtube_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
		ivSocialI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = view.getContext().getString(R.string.social_instagram_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
		
	    //Return the view itself.
		return view;
	}
	
	/**
	 * Calculates the total prize in the prices dialog and shows the total.
	 * 
	 * @param v A view to be able to get a context.
	 * 
	 * @return The total price
	 */
	private int calculatePrice(Dialog v){
		
		//Locate views
		CheckBox[] cbDayName = new CheckBox[6];
		cbDayName[0] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_0);
		cbDayName[1] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_1);
		cbDayName[2] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_2);
		cbDayName[3] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_3);
		cbDayName[4] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_4);
		cbDayName[5] = (CheckBox) v.findViewById(R.id.cb_dialog_prices_5);
		TextView tvTotal = (TextView) v.findViewById(R.id.tv_dialog_prices_total);
		
		//Open database
		SQLiteDatabase db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("SELECT price FROM day ORDER BY id;", null);
		
		//Loop simultaneously the db entries and the checkboxes
		int i = 0;
		int total = 0;
		int selected = 0;
		while (cursor.moveToNext() && i < 6){
			if (cbDayName[i].isChecked()){
				selected ++;
				total = total + cursor.getInt(0);
			}
			i ++;
		}
		
		//Get offers
		cursor = db.rawQuery("SELECT price, days FROM offer ORDER BY id;", null);
		boolean offerApplied = false;
		while (cursor.moveToNext()){
			
			//If selected days equal the days in the ofer, fix the price
			if (cursor.getInt(1) == selected){
				total = cursor.getInt(0);
				offerApplied = true;
			}
		}
		/*if (offerApplied == false){
			Cursor closestOffer = db.rawQuery("SELECT price, days FROM offer WHERE days < " + Integer.toString(selected) + " ORDER BY days DESC LIMIT 1;", null);
			if (closestOffer.getCount() == 1){
				closestOffer.moveToNext();
				total = closestOffer.getInt(0);
				while (cursor.moveToNext() && i < 6){
					if (cbDayName[i].isChecked()){
						selected ++;
						total = total + cursor.getInt(0);
					}
					i ++;
				}
			}
			//TODO: Look for the closest inmediatly lower offer, apply and calculate difference
		}*/
		
		//Set text
		tvTotal.setText(v.getContext().getResources().getString(R.string.prices_total) + " " + total + v.getContext().getResources().getString(R.string.eur));
		
		//Return the total price
		return total;
	}

	/**
	 * Handles the location section of this screen. Its updated every time the location changes. 
	 * 
	 * Shows or hides the location panel depending on if there is a recent location report, and sets the distance and time text.
	 */
	private void updateLocation(){
		
		//Elements to format, parse, and operate with dates
		Calendar cal;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date date = new Date();
		
		//Preferences
		SharedPreferences preferences = v.getContext().getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		
		//Text view in the location section that will change
		tvLocation = (TextView) v.findViewById(R.id.tv_home_location_1);
		
		try {
			//Get the date of the last saved location report
			Date locationDate = dateFormat.parse(preferences.getString(GM.PREF_GM_LOCATION, "1970-01-01 00:00:00"));
			cal = Calendar.getInstance();
		    cal.setTime(date);
		    cal.add(Calendar.MINUTE, -10);
		    
		    //If the last location report was saved in the last 10 minutes
		    if (cal.getTime().before(locationDate)){
		    	//Get the location of GM from preferences
		    	double gmLat = Double.parseDouble(preferences.getString(GM.PREF_GM_LATITUDE, "0"));
		    	double gmLon = Double.parseDouble(preferences.getString(GM.PREF_GM_LONGITUDE, "0"));
		    	
		    	//Calculate the distance to the saved location
		    	Double distance = Distance.calculateDistance(coordinates[0], coordinates[1], gmLat, gmLon, 'K');
		    	distance = (double) Math.round(1000 * distance);
		    	
		    	//Write the distance
		    	if (distance > 1000d)
		    		tvLocation.setText(String.format(v.getContext().getString(R.string.home_section_location_text_1), String.format("%.2f", distance / 1000)  + " " + v.getContext().getString(R.string.kilometers), Math.round(distance * 0.012)));
		    	else
		    		tvLocation.setText(String.format(v.getContext().getString(R.string.home_section_location_text_1), distance.intValue()  + " " + v.getContext().getString(R.string.meters), Math.round(distance * 0.012)));

		    }
		    
		    //If the location report is older than 10 minutes
		    else{
		    	llLocation.setVisibility(View.GONE);
		    }
			
		} catch (ParseException e) {
			llLocation.setVisibility(View.GONE);
			Log.e("Error getting location date", e.toString());
		} 
	}
	
	/**
	 * Populates the around section with events, or hides it if none
	 */
	@SuppressLint("InflateParams") //Rows are added in a loop.
	private void populateAround(){
		
		//Each element in the list
		LinearLayout entry;
		
		//An inflater
		LayoutInflater factory = LayoutInflater.from(v.getContext());
		
		//Elements to format, parse and operate with dates
		Calendar cal;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd-", Locale.US);
		Date startMinus30, endMinus15, startPlus30;
		Date date = new Date();
		
		//Database elements
		SQLiteDatabase db;
		Cursor cursor;
		
		//Array of events
	    ArrayList<Event> eventList = new ArrayList<Event>();
		Event event;
		
		//Check GPS status
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		
			//Get data from the database
			db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
			cursor = db.rawQuery("SELECT schedule, gm, event.name, event.description, place.name, address, lat, lon, start, end, host, event.id FROM event, place WHERE schedule = 1 AND event.place = place.id;", null);
			
			//For each entry
			while (cursor.moveToNext()){
				try{
					//Set limit dates to consider the element close in time.
					cal = Calendar.getInstance();
				    cal.setTime(dateFormat.parse(cursor.getString(8)));
				    cal.add(Calendar.MINUTE, -30);
				    startMinus30 = cal.getTime();
				    cal = Calendar.getInstance();
				    cal.setTime(dateFormat.parse(cursor.getString(8)));
				    cal.add(Calendar.MINUTE, 30);
				    startPlus30 = cal.getTime();
				    
				    //Events with end date
				    if (cursor.getString(9) != null){
				    	cal = Calendar.getInstance();
					    cal.setTime(dateFormat.parse(cursor.getString(9)));
					    cal.add(Calendar.MINUTE, -15);
					    endMinus15 = cal.getTime();
					    
					    //If in range, add to the list
					    if (date.after(startMinus30) && date.before(endMinus15)){
					    	event = new Event(cursor.getInt(11), cursor.getString(2), cursor.getString(3), cursor.getInt(1), cursor.getInt(0), cursor.getString(4), cursor.getString(10), new double[] {cursor.getDouble(6), cursor.getDouble(7)}, cursor.getString(8), cursor.getString(9));
				        	eventList.add(event);
					    }
				    }
				    //Events without end time
				    else{
				    	//If in range, add to the list
				    	if (date.after(startMinus30) && date.before(startPlus30)){
				    		event = new Event(cursor.getInt(11), cursor.getString(2), cursor.getString(3), cursor.getInt(1), cursor.getInt(0), cursor.getString(4), cursor.getString(10), new double[] {cursor.getDouble(6), cursor.getDouble(7)}, cursor.getString(8), cursor.getString(9));
				        	eventList.add(event);
				    	}
				    }
					
				}
				catch (ParseException e){
					Log.e("Error parsing date for around event", e.toString());
				}
			}
			
			//Close the cursor
			cursor.close();
			
			//If no events close in time, don't show any
			if (eventList.size() == 0)
				llAround.setVisibility(View.GONE);
			
			//If there are events
			else{
				TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime, tvRowDistance, tvRowId;
				int distance;
				DecimalFormat df = new DecimalFormat("#.#"); 
				
				//Sort the list
				Collections.sort(eventList);
				
				//Empty the list
				llAroundContent.removeAllViews();
				
				//For each item
				for(int i = 0; i < eventList.size() && i < 2; i++){
		        	
		        	//Create a new row
		        	entry = (LinearLayout) factory.inflate(R.layout.row_home_around, null);
		        	
		        	//Set id
		        	tvRowId = (TextView) entry.findViewById(R.id.tv_row_home_around_id);
		        	tvRowId.setText(Integer.toString(eventList.get(i).getId()));
		        	
		        	//Set name
		        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_around_title);
		        	tvRowName.setText(eventList.get(i).getName());
		        	
		        	//Set description
		        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_around_description);
		        	tvRowDescription.setText(eventList.get(i).getDescription());
		        	
		        	//Set place
		        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_around_address);
		        	tvRowPlace.setText(eventList.get(i).getPlace());
		        	
		        	//Set distance
		        	tvRowDistance = (TextView) entry.findViewById(R.id.tv_row_home_around_distance);
		        	distance = eventList.get(i).getDistance(coordinates);
		        	if (distance < 1000)
		        		tvRowDistance.setText(String.format(getResources().getString(R.string.around_distance), Math.round(distance), getResources().getString(R.string.meters), Math.round(distance * 0.012)));
		        	else
		        		tvRowDistance.setText(String.format(getResources().getString(R.string.around_distance), df.format(distance / 1000), getResources().getString(R.string.kilometers), Math.round(distance * 0.012)));
		        	
		        	//Set time
		        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_around_time);
		        	Date tm;
					try {
						tm = eventList.get(i).getStart();
						if (dayFormat.format(tm).equals(dayFormat.format(date)))
			        		tvRowTime.setText(v.getContext().getString(R.string.today) + " " + timeFormat.format(tm));
			        	else
			        		tvRowTime.setText(v.getContext().getString(R.string.tomorrow) + " " + timeFormat.format(tm));
					}
					catch (Exception e) {
						Log.e("Date error", e.toString());
					}
		        	
					//Set on click event to show a dialog.
					entry.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
			            	TextView tvId = (TextView) v.findViewById(R.id.tv_row_home_around_id);
			            	int id = Integer.parseInt(tvId.getText().toString());
							showAroundDialog(id);
						}
		        	});
					
		        	//Add the entry to the list.
		        	llAroundContent.addView(entry);
				}
			}
		}
		
		//If no GPS
		else{
			llAround.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Shows a dialog with info about an event from the around list
	 * 
	 * @param id The id of the event
	 */
	private void showAroundDialog(int id){
		
		//Create the dialog
		final Dialog dialog = new Dialog(getActivity());
		
		//Set up the window
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_around);
		
		//Date formatters
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd-", Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
 
		//Set the custom dialog components - text, image and button
		TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_dialog_around_title);
		TextView tvDescription = (TextView) dialog.findViewById(R.id.tv_dialog_around_description);
		TextView tvDate = (TextView) dialog.findViewById(R.id.tv_dialog_around_date);
		TextView tvTime = (TextView) dialog.findViewById(R.id.tv_dialog_around_time);
		TextView tvPlace = (TextView) dialog.findViewById(R.id.tv_dialog_around_place);
		TextView tvAddress = (TextView) dialog.findViewById(R.id.tv_dialog_around_address);
		Button btClose = (Button) dialog.findViewById(R.id.bt_around_close);
		
		//Get info about the event
		SQLiteDatabase db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("SELECT event.id, event.name, description, start, end, place.name, address, lat, lon FROM event, place WHERE place.id = event.place AND event.id = " + id + ";", null);
		if (cursor.getCount() > 0){
			cursor.moveToNext();
		
			//Set title
			tvTitle.setText(cursor.getString(1));
			
			//Set description
			tvDescription.setText(cursor.getString(2));
			
			//Set date
			try{
				Date day = dateFormat.parse(cursor.getString(3));
				Date date = new Date();
				
				//If the event is today, show "Today" instead of the date
				if (dayFormat.format(day).equals(dayFormat.format(date)))
					tvDate.setText(dialog.getContext().getString(R.string.today));
				
				else{
					Calendar cal = Calendar.getInstance();
				    cal.setTime(date);
				    cal.add(Calendar.HOUR_OF_DAY, 24);
				    
				    //If the event is tomorrow, show "Tomorrow" instead of the date
				    if (dayFormat.format(cal.getTime()).equals(dayFormat.format(date))){
				    	tvDate.setText(dialog.getContext().getString(R.string.tomorrow));
				    }
					
				    //Else, show the date
				    else{
				    	SimpleDateFormat printFormat = new SimpleDateFormat("dd MMMM", Locale.US);
				    	tvDate.setText(printFormat.format(day));
				    }
				}
			}
			catch (Exception ex){
				Log.e("Error parsing event date", ex.toString());
			}
			
			//set time
			try{
				if (cursor.getString(4) == null){
					tvTime.setText(timeFormat.format(dateFormat.parse(cursor.getString(3))));
				}
				else{
					tvTime.setText(timeFormat.format(dateFormat.parse(cursor.getString(3))) + " - " + timeFormat.format(dateFormat.parse(cursor.getString(4))));
				}
			}
			catch (ParseException ex){
				Log.e("Error parsing event time", ex.toString());
			}
			
			//Set place
			tvPlace.setText(cursor.getString(5));
			tvAddress.setText(cursor.getString(6));
			
			//Set up map
			location = new LatLng(Double.parseDouble(cursor.getString(7)), Double.parseDouble(cursor.getString(8)));
			mapView = (MapView) dialog.findViewById(R.id.mv_dialog_around_map);
			mapView.onCreate(bund);
			
			//Set close button			
        	btClose.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    			}
    		});
			
        	//Actions to take when the dialog is cancelled
			dialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					if (map != null)
						map.setMyLocationEnabled(false);
					if (mapView != null){
    					mapView.onResume();
    					mapView.onDestroy();
    				}					
				}
			});
        	
			//Show the dialog
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f; 
			dialog.show();
			
			//Start the dialog map
			startMap();
			mapView.onResume();
			dialog.setOnShowListener(new OnShowListener(){
				@Override
				public void onShow(DialogInterface dialog) {
					// Gets to GoogleMap from the MapView and does initialization stuff
					startMap();
					
				}
			});
		}
	}
	
	/**
	 * Show a dialog with info about an event from each of the schedule sections
	 * 
	 * @param id The id of the event
	 */
	private void showScheduleDialog(final int id){
		
		//Create the dialog
		final Dialog dialog = new Dialog(getActivity());
		
		//Set up the window
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_schedule);
		
		//Date formatters
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd-", Locale.US);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
 
		//Set the custom dialog components - text, image and button
		TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_title);
		TextView tvDescription = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_description);
		TextView tvHost = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_host);
		TextView tvDate = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_date);
		TextView tvTime = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_time);
		TextView tvPlace = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_place);
		TextView tvAddress = (TextView) dialog.findViewById(R.id.tv_dialog_schedule_address);
		Button btClose = (Button) dialog.findViewById(R.id.bt_schedule_close);
		
		//Get info about the event
		SQLiteDatabase db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("SELECT event.id, event.name, description, start, end, place.name, address, lat, lon, host FROM event, place WHERE place.id = event.place AND event.id = " + id + ";", null);
		if (cursor.getCount() > 0){
			cursor.moveToNext();
		
			//Set title
			tvTitle.setText(cursor.getString(1));
			
			//set description
			tvDescription.setText(cursor.getString(2));
			
			//Set host
			if (cursor.getString(9) != null){
				Cursor hostCursor = db.rawQuery("SELECT name FROM people WHERE id = " + cursor.getString(9) + ";", null);
				if (hostCursor.moveToNext()){
					tvHost.setVisibility(View.VISIBLE);
					tvHost.setText(String.format(getString(R.string.schedule_host), hostCursor.getString(0)));
				}
			}
			else{
				tvHost.setVisibility(View.GONE);
			}	
			
			//Set date
			try{
				Date day = dateFormat.parse(cursor.getString(3));
				Date date = new Date();
				//If the event is today, show "Today" instead of the date
				if (dayFormat.format(day).equals(dayFormat.format(date)))
					tvDate.setText(dialog.getContext().getString(R.string.today));
				
				else{
					Calendar cal = Calendar.getInstance();
				    cal.setTime(date);
				    cal.add(Calendar.HOUR_OF_DAY, 24);
				    
				    //If the event is tomorrow, show "Tomorrow" instead of the date
				    if (dayFormat.format(cal.getTime()).equals(dayFormat.format(date))){
				    	tvDate.setText(dialog.getContext().getString(R.string.tomorrow));
				    }
					
				    //Else, show the date
				    else{
				    	SimpleDateFormat printFormat = new SimpleDateFormat("dd MMMM", Locale.US);
				    	tvDate.setText(printFormat.format(day));
				    }
				}
			}
			catch (Exception ex){
				Log.e("Error parsing event date", ex.toString());
			}
			
			//Set the time
			try{
				if (cursor.getString(4) == null)
					tvTime.setText(timeFormat.format(dateFormat.parse(cursor.getString(3))));
				else
					tvTime.setText(timeFormat.format(dateFormat.parse(cursor.getString(3))) + " - " + timeFormat.format(timeFormat.parse(cursor.getString(4))));
			}
			catch (ParseException ex){
				Log.e("Error parsing event time", ex.toString());
			}
			
			//Set the place
			tvPlace.setText(cursor.getString(5));
			tvAddress.setText(cursor.getString(6));
			
			//Set up map
			location = new LatLng(Double.parseDouble(cursor.getString(7)), Double.parseDouble(cursor.getString(8)));
			mapView = (MapView) dialog.findViewById(R.id.mv_dialog_schedule_map);
			mapView.onCreate(bund);
			
			//Set close button			
        	btClose.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				dialog.dismiss();
    			}
    		});
			
        	//Actions to take when the dialog is cancelled
			dialog.setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					if (map != null)
						map.setMyLocationEnabled(false);
					if (mapView != null){
    					mapView.onResume();
    					mapView.onDestroy();
    				}					
				}
			});        	
        	
			//Show the dialog
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f; 
			dialog.show();
			
			//Start the map
			startMap();
			mapView.onResume();
			dialog.setOnShowListener(new OnShowListener(){

				@Override
				public void onShow(DialogInterface dialog) {
					// Gets to GoogleMap from the MapView and does initialization stuff
					startMap();
					
				}
			});			
		}
	}
	
	
	/**
	 * Starts the map in the dialogs.
	 */
	public void startMap(){
		mapView.getMapAsync(this);
	}
	
	
	/**
	 * Called when the map is ready to be displayed. 
	 * Sets the map options and a marker for the map.
	 * 
	 * @param googleMap The map to be shown
	 * 
	 * @see com.google.android.gms.maps.OnMapReadyCallback#onMapReady(com.google.android.gms.maps.GoogleMap)
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.map = googleMap;
		map.setMyLocationEnabled(true);
		
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);
		// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
		try {
			MapsInitializer.initialize(this.getActivity());
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 14);
			map.animateCamera(cameraUpdate);
		}
		catch (Exception e) {
			Log.e("Error initializing maps", e.toString());
		}
		//Set GM marker
		MarkerOptions mo = new MarkerOptions();
		mo.title(v.getContext().getString(R.string.app_name));
		mo.position(location);
		map.addMarker(mo);
		
	}
	
	/**
	 * Called when the user location changes. 
	 * Recalculates the list of around events and calls updateLocation() to 
	 * update the distance in the location section.
	 * 
	 * @param location The new location
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		coordinates[0] = location.getLatitude();
		coordinates[1] = location.getLongitude();
		updateLocation();
		populateAround();
		
	}

	/**
     * Called when the location provider changes it's state. 
     * Recalculates the list of events in the around section.
     * 
     * @param provider The name of the provider
     * @param status Status code of the provider
     * @param extras Extras passed 
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		populateAround();		
	}

	
	/**
     * Called when a location provider is enabled.
     * Recalculates the list of events.
     * 
     * @param provider The name of the provider
     * 
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
	@Override
	public void onProviderEnabled(String provider) {
		populateAround();		
	}

	/**
     * Called when a location provider is disabled. 
     * Recalculates the list of events.
     * 
     * @param provider The name of the provider
     * 
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
	@Override
	public void onProviderDisabled(String provider) {
		populateAround();
		
	}
	
	/**
	 * Called when the fragment is paused. 
	 * Stops the location manager
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause(){
		locationManager.removeUpdates(this);
		if (map != null)
			map.setMyLocationEnabled(false);
		if (mapView != null){
			mapView.onPause();
		}
		super.onPause();
	}
	
	/**
	 * Called when the fragment is brought back into the foreground. 
	 * Resumes the map and the location manager.
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume(){
		if (mapView != null)
			mapView.onResume();
		if (map != null)
			map.setMyLocationEnabled(true);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, this);
		super.onResume();
	}
	
	/**
	 * Called when the fragment is destroyed. 
	 * Finishes the map. 
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (map != null)
			map.setMyLocationEnabled(false);
		if (mapView != null){
			mapView.onResume();
			mapView.onDestroy();
		}
	}

	/**
	 * Called in a situation of low memory.
	 * Lets the map handle this situation.
	 * 
	 * @see android.support.v4.app.Fragment#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (mapView != null){
			mapView.onResume();
			mapView.onLowMemory();
		}
	}
}
