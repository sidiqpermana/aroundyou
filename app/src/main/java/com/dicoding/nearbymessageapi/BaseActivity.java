package com.dicoding.nearbymessageapi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Sidiq on 30/07/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public GoogleApiClient mGoogleApiClient;

    private final int IN_VEHICLE = 0;
    private final int ON_BICYCLE = 1;
    private final int ON_FOOT = 2;
    private final int STILL = 3;
    private final int UNKNOWN = 4;
    private final int TILTING = 5;
    private final int WALKING = 7;
    private final int RUNNING = 8;

    private final int CONDITION_CLEAR = 1;
    private final int CONDITION_CLOUDY = 2;
    private final int CONDITION_FOGGY = 3;
    private final int CONDITION_HAZY = 4;
    private final int CONDITION_ICY = 5;
    private final int CONDITION_RAINY = 6;
    private final int CONDITION_SNOWY = 7;
    private final int CONDITION_STORMY = 8;
    private final int CONDITION_UNKNOWN = 0;
    private final int CONDITION_WINDY = 9;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(BaseActivity.this)
                .addApi(Awareness.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    public String getActivityType(DetectedActivity mDetectedActivity){
        String textType = null;
        switch (mDetectedActivity.getType()){
            case IN_VEHICLE:
                textType = "Driving";
                break;

            case ON_BICYCLE:
                textType = "Biking";
                break;

            case ON_FOOT:
                textType = "Commute (ON_FOOT)";
                break;

            case STILL:
                textType = "Still";
                break;

            case UNKNOWN:
                textType = "Unknown";
                break;

            case TILTING:
                textType = "tilting";
                break;

            case WALKING:
                textType = "Walking";
                break;

            case RUNNING:
                textType = "running";
                break;
        }

        return "You are <b>"+textType+"</b> Confidence Value = ["+mDetectedActivity.getConfidence()+"]";
    }

    public String getWeatherType(Weather weather){
        String textType = "";
        for (int i = 0; i < weather.getConditions().length; i++){
            switch (weather.getConditions()[i]){
                case CONDITION_CLEAR:
                    textType += "Clear";
                    break;

                case CONDITION_CLOUDY:
                    textType += "Cloudy ";
                    break;

                case CONDITION_FOGGY:
                    textType += "Foggy";
                    break;

                case CONDITION_HAZY:
                    textType += "Hazy ";
                    break;

                case CONDITION_ICY:
                    textType += "Icy ";
                    break;

                case CONDITION_RAINY:
                    textType += "Rainy ";
                    break;

                case CONDITION_STORMY:
                    textType += "Stormy ";
                    break;

                case CONDITION_UNKNOWN:
                    textType += "Unknow ";
                    break;

                case CONDITION_WINDY:
                    textType += "Windy ";
                    break;
            }
        }


        return ""+textType+"";
    }
}
