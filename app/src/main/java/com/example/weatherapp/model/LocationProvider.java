package com.example.weatherapp.model;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class LocationProvider {

    public interface LocationListener {
        void onLocationUpdateCallback(Location location);
    }

    private Context mContext;
    private LocationListener mListener;

    private LocationRequest mLocationRequest;
    private static long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private static long FASTEST_INTERVAL = 10 * 1000; /* 10 sec */

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public LocationProvider(Application application) {
        mContext = application.getApplicationContext();
    }

    public void setListener(LocationListener listener) {
        mListener = listener;
    }

    public void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (mListener != null)
                            mListener.onLocationUpdateCallback(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void stopLocationUpdates() {
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (mListener != null)
                        mListener.onLocationUpdateCallback(locationResult.getLastLocation());
                }
            });
        }
    }

}
