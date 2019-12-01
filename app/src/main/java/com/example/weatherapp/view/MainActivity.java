package com.example.weatherapp.view;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapp.model.ForecastProvider;
import com.example.weatherapp.presenter.MainPresenter;
import com.example.weatherapp.R;
import com.example.weatherapp.model.AddressProvider;
import com.example.weatherapp.model.LocationProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    public static final String TAG = "TAG_MainActivity";

    private static final int REQUEST_CODE = 101;

    private TextView mLocation;
    private TextView mForecast;
    private MainPresenter mMainPresenter;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    mMainPresenter.startWeatherMonitoring();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public void setLocation(String text) {
        mLocation.setText(text);
    }

    @Override
    public void setForecast(String text) {
        mForecast.setText(text);
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        mMainPresenter = new MainPresenter(this,
                new LocationProvider(getApplication()),
                new AddressProvider(new Geocoder(this, Locale.getDefault())),
                new ForecastProvider());

        checkGooglePlayServices();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mMainPresenter.onStop();
    }



    private void checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int statusCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (statusCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = googleApiAvailability.getErrorDialog(this, statusCode,
                    0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });

            errorDialog.show();
        } else {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            mMainPresenter.startWeatherMonitoring();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET},
                REQUEST_CODE);
    }

    private void initViews() {
        mLocation = findViewById(R.id.text_view_location);
        mForecast = findViewById(R.id.text_view_forecast);
    }
}
