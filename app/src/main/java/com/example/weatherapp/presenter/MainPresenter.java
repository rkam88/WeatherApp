package com.example.weatherapp.presenter;

import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.example.weatherapp.model.AddressProvider;
import com.example.weatherapp.model.ForecastProvider;
import com.example.weatherapp.model.LocationProvider;
import com.example.weatherapp.view.IMainActivity;

import java.lang.ref.WeakReference;

public class MainPresenter
        implements LocationProvider.LocationListener,
        AddressProvider.AddressListener,
        ForecastProvider.ForecastListener {

    public static final String TAG = "TAG_MainPresenter";

    private WeakReference<IMainActivity> mIMainActivityWeakReference;
    private LocationProvider mLocationProvider;
    private AddressProvider mAddressProvider;
    private ForecastProvider mForecastProvider;

    public MainPresenter(IMainActivity mainActivity,
                         LocationProvider locationProvider,
                         AddressProvider addressProvider,
                         ForecastProvider forecastProvider) {
        mIMainActivityWeakReference = new WeakReference<>(mainActivity);

        mLocationProvider = locationProvider;
        mLocationProvider.setListener(this);

        mAddressProvider = addressProvider;
        mAddressProvider.setListener(this);

        mForecastProvider = forecastProvider;
        mForecastProvider.setListener(this);
    }

    public void startWeatherMonitoring() {
        mLocationProvider.startLocationUpdates();
    }

    @Override
    public void onLocationUpdateCallback(Location location) {
        Log.d(TAG, "onLocationUpdateCallback: got location - " + location.toString());
        mAddressProvider.getAddress(location);
        mForecastProvider.getForecast(location.getLatitude(), location.getAltitude());
    }

    @Override
    public void onAddressReadyCallback(Address address) {
        Log.d(TAG, "onAddressReadyCallback: got address - " + address.getAddressLine(0));

        IMainActivity iMainActivity = mIMainActivityWeakReference.get();
        if (iMainActivity != null) {
            iMainActivity.setLocation(address.getAddressLine(0));
        }
    }

    @Override
    public void onForecastLoadedCallback(String string) {
        Log.d(TAG, "onForecastLoadedCallback: got forecast - " + string);

        IMainActivity iMainActivity = mIMainActivityWeakReference.get();
        if (iMainActivity != null) {
            iMainActivity.setForecast(string);
        }
    }

    public void onStop() {
        mLocationProvider.stopLocationUpdates();
        mLocationProvider.setListener(null);
        mAddressProvider.setListener(null);
        mForecastProvider.setListener(null);
    }
}
