package com.example.uniplus;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICKER_REQUEST = 4;
    private static final int CAMERA = 2;
    private static final int ALMACENAMIENTO_EXTERNO = 3;
    private static boolean accessCamera = false, accessAlm = false;

    private static final String PATH_USERS = "users/";
    private static final String PATH_IMAGE = "images/";

    private EditText etName, universidad, etCorreo, etPassword;
    private double lat = -1, lon = -1;
    private Button btnGallery, btnCamera, btnRegister;
    private Bitmap bmImage;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorage;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private static final int REQUEST_CHECK_SETTINGS = 99;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private String justificacion = "Se necesita el GPS para mostrar la ubicación del evento";

    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    ActivityResultLauncher<String> getImageGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    saveImage(result);
                }
            }
    );

    ActivityResultLauncher<Uri> getImageCamera = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    saveImage(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(PATH_USERS);

        mStorage = FirebaseStorage.getInstance().getReference();

        etName = findViewById(R.id.nombre);
        universidad = findViewById(R.id.universidad);
        etCorreo = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        btnGallery = findViewById(R.id.btnGallery);
        btnCamera = findViewById(R.id.btnCamera);
        btnRegister = findViewById(R.id.botonRegistro);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getFilesDir(),"picFromCamera");
                imageUri = FileProvider.getUriForFile(view.getContext(), getApplicationContext().getPackageName()+".fileprovider",file);
                getImageCamera.launch(imageUri);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageGallery.launch("image/*");
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });

        if (ContextCompat.checkSelfPermission(RegistroActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
        updateCurrentPosition();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
    }

    private void saveImage(Uri uriF) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = etCorreo.getText().toString();
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);


        storageReference.putFile(uriF)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //binding.firebaseimage.setImageURI(null);
                        Toast.makeText(RegistroActivity.this,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(RegistroActivity.this,"Failed to Upload",Toast.LENGTH_SHORT).show();


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

    private void requestLocation(){

        request(this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                justificacion,
                LOCATION_PERMISSION_CODE);

        if (ContextCompat.checkSelfPermission(RegistroActivity.this,
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
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }
        };
    }

    private boolean requestPermission(Activity context, String permit, String justification, int id){
        if(ContextCompat.checkSelfPermission(context, permit) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permit)){
                Toast.makeText(this, justification, Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permit}, id);
            return false;
        } else {
            return true;
        }
    }

    private void usePermissionCamera(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void usePermissionImage(){
        Intent pictureIntent = new Intent(Intent.ACTION_PICK);
        pictureIntent.setType("image/*");
        startActivityForResult(pictureIntent, IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    usePermissionCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied to camera", Toast.LENGTH_LONG).show();
                }
                break;
            }

            case ALMACENAMIENTO_EXTERNO: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    usePermissionImage();
                } else {
                    Toast.makeText(getApplicationContext(), "Access denied to image gallery", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_IMAGE_CAPTURE: {
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    bmImage = imageBitmap;
                }
                break;
            }
            case IMAGE_PICKER_REQUEST: {
                if(resultCode == RESULT_OK){
                    try{
                        final Uri imageUri = data.getData();
                        final InputStream is = getContentResolver().openInputStream(imageUri);
                        final Bitmap selected = BitmapFactory.decodeStream(is);
                        bmImage = selected;
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            }
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){

        System.out.println("LLEGO A LA PRIMERA");
        if(user != null){
            System.out.println("LLEGO A LA SEGUNDA");
            final String name = etName.getText().toString();
            final String apellido = universidad.getText().toString();
            if (!name.equalsIgnoreCase("")
                    && !apellido.equalsIgnoreCase("")  && lat != -1
                    && lon != -1) {
                System.out.println("LLEGO A LA TERCERA");
                Usuario newUser = new Usuario();
                newUser.setName(name);
                newUser.setApellido(apellido);
                newUser.setLatitude(lat);
                newUser.setLongitude(lon);
                newUser.setDisponible(true);

                mRef = mDatabase.getReference(PATH_USERS + user.getUid());
                mRef.setValue(newUser);

                StorageReference imgRef = mStorage.child(PATH_IMAGE + user.getUid() + "/" + "profile.png");
                imgRef.putBytes(uploadImage()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("LLEGO A LA CUARTA");
                        Toast.makeText(RegistroActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            //etCorreo.setText("");
            etPassword.setText("");
        }
    }

    private byte[] uploadImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void signUpUser() {
        final String user = etCorreo.getText().toString();
        final String password = etPassword.getText().toString();
        if (!user.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
            mAuth.createUserWithEmailAndPassword(user, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Toast.makeText(RegistroActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }
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