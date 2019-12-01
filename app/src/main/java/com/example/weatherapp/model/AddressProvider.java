package com.example.weatherapp.model;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressProvider {

    public static final int ONE_ADDRESS = 1;

    public interface AddressListener {
        void onAddressReadyCallback(Address address);
    }

    private Geocoder mGeocoder;
    private AddressListener mListener;

    public AddressProvider(Geocoder geocoder) {
        mGeocoder = geocoder;
    }

    public void setListener(AddressListener listener) {
        mListener = listener;
    }

    public void getAddress(Location location) {
        new GeocodingAsyncTask(mGeocoder, mListener).execute(location);
    }

    private static class GeocodingAsyncTask extends AsyncTask<Location, Void, List<Address>> {
        private final Geocoder mGeocoder;
        private AddressListener mListener;

        private GeocodingAsyncTask(Geocoder geocoder, AddressListener listener) {
            mGeocoder = geocoder;
            mListener = listener;
        }

        @Override
        protected List<Address> doInBackground(Location... locations) {
            Location location = locations[0];
            try {
                List<Address> addressList = mGeocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), ONE_ADDRESS);
                return addressList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<Address> addressList) {
            super.onPostExecute(addressList);
            if (!addressList.isEmpty() && mListener != null)
                mListener.onAddressReadyCallback(addressList.get(0));
        }
    }
}



