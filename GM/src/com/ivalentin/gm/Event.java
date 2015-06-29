package com.ivalentin.gm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * An event holds information about an activity of the festival
 * 
 * @author Iñigo Valentin
 *
 */
public class Event implements Comparable<Event>{
	
	private String name, description, place, host;
	private double[] coordinates = new double[2];
	private Date start, end;
	private boolean gm, schedule;
	private Integer distance = null;
	private double[] location = new double[2];
	
	
	/**
	 * Constructor.
	 * 
	 * @param name Name of the event.
	 * @param description Description of the event.
	 * @param gm 1 if it's a Margolari event, 0 otherwise.
	 * @param schedule 1 if it's an event from the municipal schedule, 0 otherwise.
	 * @param place Name of the place where the event is hold.
	 * @param host Name of the people/organization that organizes the event. Can be null.
	 * @param coordinates Array of doubles [latitude, longitude].
	 * @param start String whit the date and time of the start of the event, in the format yyyy-MM-dd HH:mm:ss.
	 * @param end String whit the date and time of the end of the event, in the format yyyy-MM-dd HH:mm:ss. Can be null.
	 */
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
	
	/**
	 * Return the name of the event.
	 * @return The name of the event.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return the description of the event.
	 * @return The description of the event.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Checks if the event is a Margolari one.
	 * @return true if it's a Margolari event, false otherwise.
	 */
	public boolean isGm(){
		return gm;
	}
	
	/**
	 * Checks if the event is an official one.
	 * @return true if it's an official event, false otherwise.
	 */
	public boolean isSchedule(){
		return schedule;
	}
	
	/**
	 * Return the place of the event.
	 * @return The place of the event.
	 */
	public String getPlace() {
		return place;
	}
	
	/**
	 * Return the host of the event.
	 * @return The host of the event.
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Return the event coordinates.
	 * @return The coordinates of the event [latitude, longitude].
	 */
	public double[] getCoordinates() {
		return coordinates;
	}
	
	/**
	 * Return the event start time.
	 * @return The event start date and time.
	 */
	public Date getStart() {
		return start;
	}
	
	/**
	 * Return the event end time.
	 * @return The event end date and time.
	 */
	public Date getEnd() {
		return end;
	}
	
	/**
	 * Calculates the event distance to a given point.
	 * 
	 * @param loc Array of doubles [latitude, longitude] to calculate the distance of the event from.
	 * @return The distance, in meters, between the event and the given point.
	 */
	public Integer getDistance(double loc[]){
		location = loc;
		distance = Math.round((long) (Distance.calculateDistance(location[0], location[1], coordinates[0], coordinates[1], 'K') * 1000));
		return distance;
	}
	
	/**
	 * Gets the distance between the event location and a location previously provided using getDistance(double loc[]). 
	 * 
	 * @return The distance, in meters, between the event and the location set when calling getDistance(double loc[]). If it wasn't called, it will be null.
	 */
	public Integer getDistance(){
		if (location == null)
			return null;
		else
			return getDistance(location);
	}
	
	/**
	 * Return the time between the current time and the start of the event.
	 * @return The time, in minutes, to the event start.
	 */
	public int getTimeToStart(){
		//TODO:
		return 0;
	}
	
	/**
	 * Return the time between the current time and the end of the event.
	 * @return The time, in minutes, to the event end.
	 */
	public int getTimeToEnd(){
		//TODO:
		return 0;
	}
	
	/**
	 * Compares the event to another event to establish a relative order. 
	 * The order is based on the time and the distance to the user.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * @param Other event to compare to this.
	 */
	@Override
	public int compareTo(Event another) {
		float distanceDif, timeDif;
		float result;
		
		
		//Get distance difference
		if (getDistance() == null && another.getDistance() == null)
			distanceDif = 0;
		else if (getDistance() == null)
			distanceDif = -1;
		else if (another.getDistance() == null)
			distanceDif = 1;
		else{
			distanceDif = getDistance() - another.getDistance();
			if (getDistance() > another.getDistance()){
				if (getDistance() == 0)
					distanceDif = 1;
				else
					distanceDif = distanceDif / getDistance();
			}
			else{
				if (another.getDistance() == 0)
					distanceDif = -1;
				else
					distanceDif = distanceDif / another.getDistance();
			}
		}
		
		//Get time to start difference
		timeDif = getTimeToStart() - another.getTimeToStart();
		if (getTimeToStart() > another.getTimeToStart()){
			if (getTimeToStart() == 0)
				timeDif = 1;
			else
				timeDif = timeDif / getTimeToStart();
		}
		else{
			if (another.getTimeToStart() == 0)
				timeDif = -1;
			else
				timeDif = timeDif / another.getTimeToStart();
			
		}
		
		result = timeDif + distanceDif;
		
		return Math.round(result);
	}
	
	
	
	
	
	
}
