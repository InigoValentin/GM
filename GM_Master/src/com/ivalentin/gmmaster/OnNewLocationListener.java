package com.ivalentin.gmmaster;

import android.location.Location;

public interface OnNewLocationListener {
    public abstract void onNewLocationReceived(Location location);
}
