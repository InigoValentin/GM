package com.ivalentin.gm;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Layout that show event close to the user in both time and space.
 * 
 * @author Iñigo Valentin
 *
 */
public class AroundLayout extends Fragment implements LocationListener{

	//The location of the user
	private double[] coordinates = new double[2];
	
	//LocationManager to provide the location
	private LocationManager locationManager;
	
	//The location provider
	private String provider;
	
	/**
	 *  Request updates at startup
	 *
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		locationManager = (LocationManager) this.getView().getContext().getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 5000, 10, this);
	}

	/**
	 * Remove the location listener updates when Activity is paused.
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	/**
	 * When the location is updated, reload the list of events. 
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		coordinates[0] = lat;
		coordinates[1] = lng;
		populateAround();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
	
	/**
	 * Run when the fragment is inflated.
	 * Assigns views, gets the date and does the first call to the {@link populate function}.
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@SuppressLint("InflateParams") //Throws unknown error when done properly.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//Load the layout
		View view = inflater.inflate(R.layout.fragment_layout_around, null);		
		
		//Set the title
		((MainActivity) getActivity()).setSectionTitle(view.getContext().getString(R.string.menu_around));
		
		//Get the location passed from MainActivity so we dont have to wait for it to be aquired.
		Bundle bundle = this.getArguments();
		coordinates[0] = bundle.getDouble("lat", 0);
		coordinates[1] = bundle.getDouble("lon", 0);
		
		
		//Assign parent layout
		final LinearLayout list = (LinearLayout) view.findViewById(R.id.ll_around_list);
				
		//Populate the activity list
		populateAround(list);
		
		//Return the fragment view
		return view;
	}
	
	/**
	 * Populates the list of activities around.
	 * 
	 */
	private void populateAround(){
		LinearLayout list = (LinearLayout) this.getView().findViewById(R.id.ll_around_list);
		populateAround(list);
	}
	
	/**
	 * Populates the list of activities around.
	 * 
	 * @param list The layout where the event will be placed.
	 */
	@SuppressLint("InflateParams") //Views are added from a loop: I can't specify the parent when inflating.
	private void populateAround(LinearLayout list){
		
		//A list of events.
		ArrayList<Event> eventList = new ArrayList<Event>();
		Event event;
		
		//A layout to be populated with an event.
		LinearLayout entry;
		
		//An inflater
        LayoutInflater factory = LayoutInflater.from(getActivity());
        
        //TextViews in each row
        TextView tvRowTitle, tvRowDescription, tvRowAddress, tvRowDistance;
        
        //Icon next to the location text
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.pinpoint, null);
        icon.setBounds(0, 0, 80, 80);
				
		//Read from database
		SQLiteDatabase db = getActivity().openOrCreateDatabase(GM.DB_NAME, Context.MODE_PRIVATE, null);
		//TODO: dont harcode fields
		//TODO: Refine query
		Cursor cursor = db.rawQuery("SELECT schedule, gm, event.name, event.description, place.name, address, lat, lon, start, end, host FROM event, place WHERE event.place = place.id;", null);
		
		//Make the list empty, in case we are not populating it for the first time.
        list.removeAllViews();
        
        
        double distance;
        
        
        //Loop thorough every returned row, creating an event for each
        while (cursor.moveToNext()){
        	
        	//Add event to the list
        	event = new Event(cursor.getString(2), cursor.getString(3), cursor.getInt(1), cursor.getInt(0), cursor.getString(4), cursor.getString(10), new double[] {cursor.getDouble(6), cursor.getDouble(7)}, cursor.getString(8), cursor.getString(9));
        	eventList.add(event);
        }
        
        //Close the cursor and the database
        cursor.close();
        db.close();
        
        //Sort the event list.
        Collections.sort(eventList);
        
        //Loop events in the list.
        for(int i = 0; i < eventList.size(); i++){
        	
        	//Create a new row
        	entry = (LinearLayout) factory.inflate(R.layout.row_around, null);
        	
        	//Set the background color
        	if (i % 2 == 0)
        		entry.setBackgroundColor(getResources().getColor(R.color.background_around_row_even));
        	else
        		entry.setBackgroundColor(getResources().getColor(R.color.background_around_row_odd));
        	
        	//Locate row elements and populate them.
        	tvRowTitle = (TextView) entry.findViewById(R.id.tv_row_around_title);
        	tvRowTitle.setText(eventList.get(i).getName());
        	tvRowDescription = (TextView) entry.findViewById(R.id.tv_row_around_description);
        	tvRowDescription.setText(eventList.get(i).getDescription());
        	tvRowAddress = (TextView) entry.findViewById(R.id.tv_row_around_address);
        	tvRowAddress.setText(eventList.get(i).getPlace());
        	tvRowDistance = (TextView) entry.findViewById(R.id.tv_row_around_distance);
        	distance = eventList.get(i).getDistance(coordinates);
        	if (distance < 1000)
        		tvRowDistance.setText("Distance: " + distance  + " m   Time walking: " + Math.round(distance * 0.012) + " '");
        	else
        		tvRowDistance.setText("Distance: " + (distance / 1000)  + " km   Time walking: " + Math.round(distance * 0.012) + " '");        	
        	//Add the entry to the list.
        	list.addView(entry);
		}
	}
		
}

