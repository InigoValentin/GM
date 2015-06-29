package com.ivalentin.gm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Section that will be seen when the app is started. 
 * Contains info from almost every other section.
 * 
 * @author Iñigo Valentin
 * 
 * @see Fragment
 *
 */
public class HomeLayout extends Fragment{

	
	//The location of the user
	private double[] coordinates = new double[2];
	
	//Layouts for each section
	private LinearLayout llSchedule, llGm, llAround, llLocation;
	private LinearLayout llScheduleContent, llGmContent, llAroundContent;
	private LinearLayout llScheduleLink, llGmLink, llAroundLink, llLocationLink, llJoinLink;
	
	//TextView showing the distance of GM
	private TextView tvLocation;
	
	/**
	 * Run when the fragment is inflated.
	 * Assigns views, gets the date and does the first call to the {@link populate function}.
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
		//llJoin = (LinearLayout) view.findViewById(R.id.ll_home_section_join);
		llScheduleContent = (LinearLayout) view.findViewById(R.id.ll_home_section_schedule_content);
		llGmContent = (LinearLayout) view.findViewById(R.id.ll_home_section_gm_content);
		llAroundContent = (LinearLayout) view.findViewById(R.id.ll_home_section_around_content);
		//llLocationContent = (LinearLayout) view.findViewById(R.id.ll_home_section_location_content);
		//llJoinContent = (LinearLayout) view.findViewById(R.id.ll_home_section_join_content);
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
				//TODO: Show dialog
			}
		});
		
		//Populate the schedule section
		db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.add(Calendar.HOUR_OF_DAY, 24); 
	    maxDate = cal.getTime();
		cursor = db.rawQuery("SELECT event.id, event.name, event.description, event.place, place.id, place.name, event.start FROM event, place WHERE schedule = 1 AND place.id = event.place AND start BETWEEN '" + dateFormat.format(date) + "' AND '" + dateFormat.format(maxDate) + "' ORDER BY start DESC LIMIT 2;", null);
		if (cursor.getCount() == 0){
			llSchedule.setVisibility(View.GONE);
		}
		else{
			TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime;
			while (cursor.moveToNext()){
				//Create a new row
	        	entry = (LinearLayout) factory.inflate(R.layout.row_home_schedule, null);
	        	
	        	//Locate row elements
	        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_schedule_title);
	        	tvRowName.setText(cursor.getString(1));
	        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_schedule_description);
	        	tvRowDescription.setText(cursor.getString(2));
	        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_schedule_place);
	        	tvRowPlace.setText(cursor.getString(5));
	        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_schedule_time);
	        	Date tm;
				try {
					tm = dateFormat.parse(cursor.getString(6));
					if (dayFormat.format(tm).equals(dayFormat.format(date)))
		        		tvRowTime.setText(view.getContext().getString(R.string.today) + " " + timeFormat.format(tm));
		        	else
		        		tvRowTime.setText(view.getContext().getString(R.string.tomorrow) + " " + timeFormat.format(tm));
				} catch (ParseException e) {
					Log.e("Date error", e.toString());
				}
	        	
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
		cursor = db.rawQuery("SELECT event.id, event.name, event.description, event.place, place.id, place.name, event.start FROM event, place WHERE gm = 1 AND place.id = event.place AND start BETWEEN '" + dateFormat.format(date) + "' AND '" + dateFormat.format(maxDate) + "' ORDER BY start DESC LIMIT 2;", null);
		if (cursor.getCount() == 0){
			llGm.setVisibility(View.GONE);
		}
		else{
			TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime;
			while (cursor.moveToNext()){
				//Create a new row
	        	entry = (LinearLayout) factory.inflate(R.layout.row_home_schedule, null);
	        	
	        	//Locate row elements
	        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_schedule_title);
	        	tvRowName.setText(cursor.getString(1));
	        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_schedule_description);
	        	tvRowDescription.setText(cursor.getString(2));
	        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_schedule_place);
	        	tvRowPlace.setText(cursor.getString(5));
	        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_schedule_time);
	        	Date tm;
				try {
					tm = dateFormat.parse(cursor.getString(6));
					if (dayFormat.format(tm).equals(dayFormat.format(date)))
		        		tvRowTime.setText(view.getContext().getString(R.string.today) + " " + timeFormat.format(tm));
		        	else
		        		tvRowTime.setText(view.getContext().getString(R.string.tomorrow) + " " + timeFormat.format(tm));
				} catch (ParseException e) {
					Log.e("Date error", e.toString());
				}
	        	
	        	llGmContent.addView(entry);
			}
			cursor.close();
		}

		//Populate the around section
		db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		
	    ArrayList<Event> eventList = new ArrayList<Event>();
		Event event;
		cursor = db.rawQuery("SELECT schedule, gm, event.name, event.description, place.name, address, lat, lon, start, end, host FROM event, place WHERE event.place = place.id;", null);
		Date startMinus30, endMinus15, startPlus30;
		while (cursor.moveToNext()){
			try{
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
				    
				    //If in range
				    if (date.after(startMinus30) && date.before(endMinus15)){
				    	event = new Event(cursor.getString(2), cursor.getString(3), cursor.getInt(1), cursor.getInt(0), cursor.getString(4), cursor.getString(10), new double[] {cursor.getDouble(6), cursor.getDouble(7)}, cursor.getString(8), cursor.getString(9));
			        	eventList.add(event);
				    }
			    }
			    //Events without end time
			    else{
			    	if (date.after(startMinus30) && date.before(startPlus30)){
			    		event = new Event(cursor.getString(2), cursor.getString(3), cursor.getInt(1), cursor.getInt(0), cursor.getString(4), cursor.getString(10), new double[] {cursor.getDouble(6), cursor.getDouble(7)}, cursor.getString(8), cursor.getString(9));
			        	eventList.add(event);
			    	}
			    }
				
			}
			catch (ParseException e){
				Log.e("Error parsing date for around event", e.toString());
			}
		}
		cursor.close();
		if (eventList.size() == 0)
			llAround.setVisibility(View.GONE);
		else{
			TextView tvRowName, tvRowDescription, tvRowPlace, tvRowTime;
			Collections.sort(eventList);
			for(int i = 0; i < eventList.size() && i < 2; i++){
	        	
	        	//Create a new row
	        	entry = (LinearLayout) factory.inflate(R.layout.row_around, null);
	        	
	        	
	        	//Locate row elements and populate them.
	        	tvRowName = (TextView) entry.findViewById(R.id.tv_row_home_schedule_title);
	        	tvRowName.setText(eventList.get(i).getName());
	        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_home_schedule_description);
	        	tvRowDescription.setText(eventList.get(i).getDescription());
	        	tvRowPlace = (TextView) entry.findViewById(R.id.tv_row_home_schedule_place);
	        	tvRowPlace.setText(eventList.get(i).getPlace());
	        	tvRowTime = (TextView) entry.findViewById(R.id.tv_row_home_schedule_time);
	        	tvRowTime.setText(timeFormat.format(eventList.get(i).getStart()));
	        	
	        	//Add the entry to the list.
	        	llAroundContent.addView(entry);
			}
		}
		
		//Populate the location section
		SharedPreferences preferences = view.getContext().getSharedPreferences(GM.PREF, Context.MODE_PRIVATE);
		tvLocation = (TextView) view.findViewById(R.id.tv_home_location_1);
		try {
			Date locationDate = dateFormat.parse(preferences.getString(GM.PREF_GM_LOCATION, "1970-01-01 00:00:00"));
			cal = Calendar.getInstance();
		    cal.setTime(date);
		    cal.add(Calendar.MINUTE, -10);
		    if (cal.getTime().before(locationDate)){
		    	//Get Location
		    	double gmLat = Double.parseDouble(preferences.getString(GM.PREF_GM_LATITUDE, "0"));
		    	double gmLon = Double.parseDouble(preferences.getString(GM.PREF_GM_LONGITUDE, "0"));
		    	Double distance = Distance.calculateDistance(coordinates[0], coordinates[1], gmLat, gmLon, 'K');
		    	distance = (double) Math.round(1000 * distance);
		    	
		    	if (distance > 1000d)
		    		tvLocation.setText(String.format(view.getContext().getString(R.string.home_section_location_text_1), String.format("%.2f", distance / 1000)  + " " + view.getContext().getString(R.string.kilometers), Math.round(distance * 0.012)));
	        		//tvLocation.setText("Distance: " + (distance / 1000)  + " km   Time walking: " + Math.round(distance * 0.012) + " '");
		    	else
		    		tvLocation.setText(String.format(view.getContext().getString(R.string.home_section_location_text_1), distance.intValue()  + " " + view.getContext().getString(R.string.meters), Math.round(distance * 0.012)));

		    }
		    else{
		    	llLocation.setVisibility(View.GONE);
		    }
			
		} catch (ParseException e) {
			llLocation.setVisibility(View.GONE);
			Log.e("Error getting location date", e.toString());
		}
		
		//Populate the "join" section
		//TODO
		
		//Assign buttons in social section
		ImageView ivSocialF = (ImageView) view.findViewById(R.id.iv_social_facebook);
		ImageView ivSocialT = (ImageView) view.findViewById(R.id.iv_social_twitter);
		ImageView ivSocialG = (ImageView) view.findViewById(R.id.iv_social_googleplus);
		ImageView ivSocialY = (ImageView) view.findViewById(R.id.iv_social_youtube);
		
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
		
	    //Return the view itself.
		return view;
	}
}
