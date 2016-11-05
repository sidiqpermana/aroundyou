package com.dicoding.nearbymessageapi;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FindNearbyActivity extends BaseActivity
    implements EasyPermissions.PermissionCallbacks,
        View.OnClickListener, OnMapReadyCallback{
    private static final int RC_APP_PERMS = 122;
    public static final String TAG = "NearbyApp";

    private Button btnFind;
    private TextView tvUserEmail, tvLocation, tvWeatherType, tvWeatherTemp;
    private LinearLayout lnWeather;
    private CircleImageView ivProfileUser;
    private GoogleMap map;
    private String TAG_LOCATION = "DetectLocation";
    private String TAG_PLACES = "DetectPlaces";
    private String TAG_WEATHER = "DetectWeather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);

        btnFind = (Button)findViewById(R.id.btn_find);
        btnFind.setOnClickListener(this);

        getSupportActionBar().setTitle("Around You");

        tvUserEmail = (TextView)findViewById(R.id.tv_user_email);
        tvLocation = (TextView)findViewById(R.id.tv_location);
        tvWeatherType = (TextView)findViewById(R.id.tv_weather_type);
        tvWeatherTemp = (TextView)findViewById(R.id.tv_weather_temprature);
        ivProfileUser = (CircleImageView)findViewById(R.id.ivProfilePicture);
        lnWeather = (LinearLayout)findViewById(R.id.ln_weather);

        activatePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_APP_PERMS)
    private void activatePermissions() {
        String[] perms = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            tvUserEmail.setText("Hola,, "+MainActivity.getEmail(this));
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_permissions),
                    RC_APP_PERMS, perms);
        }
    }

    private void getCurrentUserLocation(){
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e(TAG_LOCATION, "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        String userLocation = "User Location : "+location.getLatitude()+" "+location.getLongitude()+" " +
                                "[With accuracy : "+(int)location.getAccuracy()+" m]";

                        Log.d(TAG_LOCATION, userLocation);

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        Marker userPosition = map.addMarker(new MarkerOptions().position(latLng)
                                .title("Your Current Position"));

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        // Zoom in, animating the camera.
                        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                    }
                });
    }

    private void getProbleUserPlaces(){
        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                            Log.e(TAG_PLACES, "Could not get places.");
                            return;
                        }
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();
                        // Show the top 5 possible location results.
                        String places = "";
                        if (placeLikelihoodList != null) {
                            for (int i = 0; i < 5 && i < placeLikelihoodList.size(); i++) {
                                PlaceLikelihood p = placeLikelihoodList.get(i);
                                places += p.getPlace().getName().toString() + ", likelihood: " + p.getLikelihood()+" ";
                            }
                            String userPlaces = "Possible places : "+places;
                            Log.d(TAG_PLACES, userPlaces);
                            String problePlace = "Current location : "+placeLikelihoodList.get(0).getPlace().getName()+"\n"+
                                    placeLikelihoodList.get(0).getPlace().getAddress();
                            tvLocation.setText(problePlace);
                        } else {
                            Log.e(TAG_PLACES, "Place is null.");
                        }
                    }
                });
    }

    private void getCurrentWeather(){
        lnWeather.setVisibility(View.GONE);
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e(TAG_WEATHER, "Could not get weather.");
                            return;
                        }
                        lnWeather.setVisibility(View.VISIBLE);
                        Weather weather = weatherResult.getWeather();
                        String weatherDetails = "Condition : "+getWeatherType(weather)+"\n"+
                                "Person feel temprature : "+weather.getFeelsLikeTemperature(Weather.CELSIUS)+"\n"+
                                "Humidity : "+weather.getHumidity();
                        Log.d(TAG_WEATHER, weatherDetails);
                        tvWeatherType.setText(getWeatherType(weather));
                        tvWeatherTemp.setText(new DecimalFormat("##.#").format(weather.getFeelsLikeTemperature(Weather.CELSIUS))+"°C");
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d(TAG, "Permissions granted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG,"Permissions denied");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_find){
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getCurrentUserLocation();
        getProbleUserPlaces();
        getCurrentWeather();
    }
}
