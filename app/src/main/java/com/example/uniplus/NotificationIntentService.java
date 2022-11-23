package com.example.uniplus;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class NotificationIntentService extends JobIntentService {
    private static final int JOB_ID = 13;
    private static final int ACCESS_LOCATION_ID = 1;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private static Parcelable points;
    private List<LatLng> pointsF=new ArrayList<LatLng>();
    private final String justificacion = "Se necesita el GPS para mostrar la ubicación del evento";


    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationIntentService.class, JOB_ID, intent);
        //aintent.putExtra("points", points);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pointsF = (List<LatLng>) points;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        System.out.println("PASA POR ACAAAAA-----------------------------------------------");
        //solicitPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Permission to Access Location", ACCESS_LOCATION_ID);

        /*for (int i = 0; i < pointsF.size(); i++) {
            System.out.println("------------------->" + pointsF.get(i).longitude);
        }*/

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = createLocationRequest();
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    System.out.println(location.getLatitude() + " " + location.getLongitude());
                }
            }
        };
    }

    protected LocationRequest createLocationRequest(){
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
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
}
