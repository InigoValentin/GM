package com.ivalentin.gm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Event implements Comparable<Event>{
	
	private String name, description, place, host;
	private double[] coordinates = new double[2];
	private Date start, end;
	private boolean gm, schedule;
	public Event(String name, String description, int gm, int schedule, String place, String host, double[] coordinates, String start, String end){
		super();
		this.name = name;
		this.description = description;
		this.place = place;
		if (gm == 1)
			this.gm = true;
		else
			this.gm = false;
		if (schedule == 1)
			this.schedule = true;
		else
			this.schedule = false;
		this.host = host;
		this.coordinates = coordinates;
		Log.e("Start", start);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		try{
			this.start = format.parse(start);
		}
		catch (Exception ex){
			start = null;
		}
		try{
			this.end = format.parse(end);
		}
		catch (Exception ex){
			end = null;
		}
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public boolean isGm(){
		return gm;
	}
	public boolean isSchedule(){
		return schedule;
	}
	public String getPlace() {
		return place;
	}
	public String getHost() {
		return host;
	}
	public double[] getCoordinates() {
		return coordinates;
	}
	public Date getStart() {
		return start;
	}
	public Date getEnd() {
		return end;
	}
	
	public int getDistance(double location[]){
		Log.e("LOCATION", location[0] + "," + location[1]);
		Log.e("COORDINATES", coordinates[0] + "," + coordinates[1]);
		return Math.round((long) (calculateDistance(location[0], location[1], coordinates[0], coordinates[1], 'K') * 1000));
	}
	
	public int getTimeToStart(){
		//TODO:
		return 0;
	}
	
	public int getTimeToEnd(){
		//TODO:
		return 0;
	}
	
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two locations using GeoDataSource (TM) prodducts  :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::    unit = the unit you desire for results                               :*/
	/*::           where: 'M' is statute miles (default)                         :*/
	/*::                  'K' is kilometers                                      :*/
	/*::                  'N' is nautical miles                                  :*/
	/*::  Worldwide cities and other features databases with latitude longitude  :*/
	/*::  are available at http://www.geodatasource.com                          :*/
	/*::                                                                         :*/
	/*::  For enquiries, please contact sales@geodatasource.com                  :*/
	/*::                                                                         :*/
	/*::  Official Web site: http://www.geodatasource.com                        :*/
	/*::                                                                         :*/
	/*::           GeoDataSource.com (C) All Rights Reserved 2015                :*/
	/*::                                                                         :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684;
		    }
		  return (dist);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts decimal degrees to radians             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts radians to decimal degrees             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double rad2deg(double rad) {
		  return (rad * 180 / Math.PI);
		}
		@Override
		public int compareTo(Event another) {
			//TODO: Ponderate distance
			return this.getStart().compareTo(another.getStart());
		}
	
	
	
	
	
	
}
