package com.example.weatherapp.model;

import com.example.weatherapp.model.forecast.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMapApi {

    @GET("data/2.5/weather")
    Call<Forecast> getForecast(
            @Query("lat") String latitude,
            @Query("lon") String longitude,
            @Query("APPID") String apiKey);

}
