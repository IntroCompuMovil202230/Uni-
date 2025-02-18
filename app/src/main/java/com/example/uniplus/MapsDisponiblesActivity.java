package com.example.uniplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsDisponiblesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String PATH_USERS = "users/";

    private GoogleMap mMap;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Usuario data;
    private Usuario other;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private static final int REQUEST_CHECK_SETTINGS = 99;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private String justificacion = "Se necesita el GPS para mostrar la ubicación del evento";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //getSupportActionBar().setTitle("Seguimiento");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.followMap);
        mapFragment.getMapAsync(this);

        user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(MapsDisponiblesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        initCurrentUser(user);
        if (ContextCompat.checkSelfPermission(MapsDisponiblesActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        updateCurrentPosition();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            mMap.setMyLocationEnabled(true);
            try {
                MapsInitializer.initialize(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateCurrentPosition();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.65, -74.05), 12));

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes
                            .RESOLUTION_REQUIRED:
                        try{
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(MapsDisponiblesActivity.this,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes
                            .SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public static void request(final Activity activity, final String permissionCode , String justificacion, final int idCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permissionCode)) {

            new AlertDialog.Builder(activity)
                    .setTitle("Se necesita un permiso")
                    .setMessage(justificacion)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permissionCode}, idCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permissionCode}, idCode);
        }
    }

    private void requestPermission(){

        request(this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                justificacion,
                LOCATION_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(MapsDisponiblesActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    startLocationUpdates();
                }
            });
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    public void updateCurrentPosition(){
        mLocationCallback = new LocationCallback(){
            public void onLocationResult(LocationResult locationResult){
                Location location = locationResult.getLastLocation();
                if(location != null){
                    LatLng myLocation = new LatLng(data.getLatitude(), data.getLongitude());
                    LatLng otherLocation = new LatLng(other.getLatitude(), other.getLongitude());
                    if(mMap != null){
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(myLocation).title("Current location").alpha(0.75f)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        mMap.addMarker(new MarkerOptions().position(otherLocation).title("Other user location").alpha(0.75f)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        if(location.getLatitude() != data.getLatitude() && location.getLongitude() != data.getLongitude()){
                            data.setLatitude(location.getLatitude());
                            data.setLongitude(location.getLongitude());
                            mRef = mDatabase.getReference(PATH_USERS + user.getUid() + "/" + "latitude");
                            mRef.setValue(location.getLatitude());
                            mRef = mDatabase.getReference(PATH_USERS + user.getUid() + "/" + "longitude");
                            mRef.setValue(location.getLongitude());
                            double dist = distance(data.getLatitude(), data.getLongitude(), other.getLatitude(), other.getLongitude());
                            Toast.makeText(MapsDisponiblesActivity.this, "Distance is: " + dist + " km", Toast.LENGTH_LONG).show();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
                        }
                    }
                }
            }
        };
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void initCurrentUser(FirebaseUser user){
        System.out.println("LLEGA AL PRIMERO");
        if(user != null){
            System.out.println("LLEGA AL SEGUNDO");
            mRef = mDatabase.getReference(PATH_USERS + user.getUid());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("LLEGA AL TERCERO");
                    data = dataSnapshot.getValue(Usuario.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapsDisponiblesActivity.this, "Data retriving failed!", Toast.LENGTH_SHORT).show();
                }
            });

            mRef = mDatabase.getReference(PATH_USERS + getIntent().getStringExtra("key"));
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println("LLEGA AL CUARTO");
                    other = dataSnapshot.getValue(Usuario.class);
                    double dist = distance(data.getLatitude(), data.getLongitude(), other.getLatitude(), other.getLongitude());
                    Toast.makeText(MapsDisponiblesActivity.this, "Distance is: " + dist + " km", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MapsDisponiblesActivity.this, "Data retriving failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private double distance(double myLat, double myLong, double otherLat, double otherLong){
        double latDistance = Math.toRadians(myLat - otherLat);
        double longDistance = Math.toRadians(myLong - otherLong);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(myLat)) *
                Math.cos(Math.toRadians(otherLat)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double res = 6371.01 * c;

        return Math.round(res * 100.0) / 100.0;
    }

    @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
}