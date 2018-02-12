package org.brmnt.taxiadmin.test.service;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Bramengton on 12/02/2018.
 */
class LocationListener implements android.location.LocationListener {
    private static final String TAG = "LocationListener";
    private Location mLastLocation;
    private double mLatitude = 46.975033;
    private double mLongitude = 31.994583;

    LocationListener(String provider) {
        Log.i(TAG, "Current provider: " + provider);
        mLastLocation = new Location(provider);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed: " + location.toString());
        mLastLocation.set(location);
        getLatitude();
        getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "provider disabled: " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Status changed: " + provider);
    }

    public boolean isActive(){
        return mLastLocation!=null;
    }

    /**
     * Function to get latitude
     * */
    double getLatitude(){
        if(mLastLocation != null){
            mLatitude = mLastLocation.getLatitude();
        }
        return mLatitude;
    }

    /**
     * Function to get longitude
     * */
    double getLongitude(){
        if(mLastLocation != null){
            mLongitude = mLastLocation.getLongitude();
        }
        return mLongitude;
    }
}
