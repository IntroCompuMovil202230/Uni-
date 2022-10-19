package com.example.uniplus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;

import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.uniplus.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends  FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int ACCESS_LOCATION_ID = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private static final int ACCESS_WRITE_STORAGE = 3;


    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LatLng lastLocation;
    private LatLng myLocation;
    private LatLng otherLocation;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightSensorListener;
    private Marker marker;
    private Geocoder mGeocoder;
    private List<Polyline> route = null;
    private List<LatLng> points=new ArrayList<LatLng>();
    SupportMapFragment mapFragment;
    boolean mapaMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int valor = getIntent().getIntExtra("espacio", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        mapaMenu = getIntent().getBooleanExtra("menu", false);

        solicitPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Permission to Access Location", ACCESS_LOCATION_ID);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if(location != null){
                    usePermission(valor, latitude, longitude);
                }
            }
        };

        //cambiar color de mapa con sensor de luz
        changeColorMap();

        binding.bottomNavegation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.firstFragment:
                    Intent intentMenuP = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(intentMenuP);
                    return true;
                case R.id.thirdFragment:
                    Intent intentLista = new Intent(getBaseContext(), ListaActivity.class);
                    startActivity(intentLista);
                    return true;
                case R.id.fourthFragment:
                    Intent intentPerfil = new Intent(getBaseContext(), MenuClienteActivity.class);
                    startActivity(intentPerfil);
                    return true;
            }
            return true;
        });
    }

    private void setPlaces() {
        if(mMap != null){

            try {
                JSONArray listaEspacios = loadJSON();
                for (int i = 0; i<listaEspacios.length();i++){
                    JSONObject info = listaEspacios.getJSONObject(i);
                    LatLng posicionEspacio = new LatLng(info.getDouble("latitude"), info.getDouble("longitude"));
                    points.add(posicionEspacio);
                }

                for (int i = 0; i < points.size(); i++){
                    mMap.addMarker(new MarkerOptions().position(points.get(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        settingsLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        if(mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Habilitaciones
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if(points.contains(marker.getPosition())){
                    int position = points.indexOf(marker.getPosition());
                    Intent intent = new Intent(getBaseContext(), EspacioActivity.class);
                    intent.putExtra("espacio", position+1);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    //PERMISOS PARA ACCESO
    private void solicitPermission(Activity context, String permit, String justification, int id) {
        if (ContextCompat.checkSelfPermission(context, permit) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permit)) {
                Toast.makeText(this, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permit}, id);
        }
    }

    protected com.google.android.gms.location.LocationRequest createLocationRequest(){
        com.google.android.gms.location.LocationRequest request = new com.google.android.gms.location.LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    private void usePermission(int valor, double latitude, double longitude) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        //posicion del usuario
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(myLocation).title("My Location").snippet("My Home").alpha(0.8f)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                                if(valor != 0){
                                    LatLng position = new LatLng(latitude, longitude);
                                    googleMap.addMarker(new MarkerOptions().position(position).title("WorkSpace"));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                                    showRoute(myLocation.latitude, myLocation.longitude, position.latitude, position.longitude);
                                }
                                if(mapaMenu){
                                    setPlaces();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    //SE REALIZA LA PETICION PARA LA ACTUALIZACION DE LA UBICACION DEL USUARIO
    private void startLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    //YA QUE EL LOCATION PUEDE QUE DE NULL SE SOLICITA ACTUALIZACION DE UBICACION POR NUESTRA CUENTA
    public void settingsLocation(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED: {
                        try{
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        }catch(IntentSender.SendIntentException sendEx)
                        {
                        }
                        break;
                    }

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                        break;
                    }
                }
            }
        });
    }

    public void showRoute(double myLat, double myLong, double otherLat, double otherLong){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(new LatLng(myLat, myLong), new LatLng(otherLat, otherLong))
                .key("AIzaSyCKcr5QdERwesRNl3X0Z9tc2ceXxN7gLUg")
                .build();
        routing.execute();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int index) {
        if(route != null){
            route.clear();
        }
        PolylineOptions pOptions = new PolylineOptions();
        LatLng polyStart = null;
        LatLng polyEnd = null;

        route = new ArrayList<>();
        for(int i = 0; i < arrayList.size(); i++){
            if(i == index){
                pOptions.color(Color.rgb(218, 0, 255));
                pOptions.width(12);
                pOptions.addAll(arrayList.get(index).getPoints());
                Polyline polyline = mMap.addPolyline(pOptions);
                polyStart = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polyEnd = polyline.getPoints().get(k - 1);
                route.add(polyline);
            }
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(polyStart, 15));
        stopLocationUpdates();
    }

    @Override
    public void onRoutingCancelled() {
        showRoute(myLocation.latitude, myLocation.longitude, otherLocation.latitude, otherLocation.longitude);
    }

    public JSONArray loadJSON() throws JSONException {
        String json = null;
        try {
            InputStream is = this.getAssets().open("espacios.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JSONObject object = new JSONObject(json);
        JSONArray espacios = object.getJSONArray("espacios");

        return espacios;
    }

    private void changeColorMap(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (mMap != null) {
                    if (sensorEvent.values[0] < 2000) {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.night));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }
}