package com.ivalentin.gmmaster;

import android.location.Location;

/**
 * Interface to implement OnNewLocationListener.
 * 
 * @author Iñigo Valentin
 *
 */
public interface OnNewLocationListener {
    public abstract void onNewLocationReceived(Location location);
}
