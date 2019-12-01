package com.example.weatherapp.model;


import android.util.Log;

import com.example.weatherapp.model.forecast.Forecast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForecastProvider {

    private ForecastListener mListener;

    public interface ForecastListener {

        void onForecastLoadedCallback(String string);

    }

    public static final String TAG = "TAG_ForecastProvider";
    private static final String API_KEY = "c5df8b87623d6e2166011600e0f92161";
    private static final String BASE_URL = "http://api.openweathermap.org/";
    //Complete URL example:
    //http://api.openweathermap.org/data/2.5/weather?lat=55.7558&lon=37.6173&APPID=c5df8b87623d6e2166011600e0f92161

    private IOpenWeatherMapApi apiService;

    public ForecastProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IOpenWeatherMapApi.class);
    }

    public void getForecast(double latitude, double altitude) {
        String lat = String.valueOf(latitude);
        String alt = String.valueOf(altitude);
        Call<Forecast> call = apiService.getForecast(lat, alt, API_KEY);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast forecast = response.body();
                float currentTemperature = forecast.getMain().getTemp() - 273.15f;
                String description = forecast.getWeather().get(0).getDescription();
                if (mListener != null)
                    mListener.onForecastLoadedCallback(String.format("%.2f C (%s)", currentTemperature, description));
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.d(TAG, "getForecast onFailure: something went wrong - " + t.getMessage());
            }
        });


    }

    public void setListener(ForecastListener listener) {
        mListener = listener;
    }
}
