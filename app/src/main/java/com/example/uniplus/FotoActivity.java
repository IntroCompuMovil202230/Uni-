package com.example.uniplus;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.example.uniplus.databinding.ActivityFotoBinding;

public class FotoActivity extends AppCompatActivity {

    ActivityFotoBinding binding;
    String rutaImagen;
    String nombre, correo, password, universidad;


    ActivityResultLauncher<String> getImageGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    setImage(result);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFotoBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageGallery.launch("image/*");
            }
        });

        binding.btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    abrirCamara();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Guarda parametros
        try{
            Bundle b = getIntent().getExtras();
            nombre = b.getString("nombre").toString().trim();
            correo = b.getString("correo").toString().trim();
            password = b.getString("password").toString().trim();
            universidad = b.getString("universidad").toString().trim();
        } catch(Exception ex){
            nombre = null;
            correo = null;
            password = null;
            universidad = null;//Or some error status //
        }

        Log.i("EN FOTO ACTIVITY", "nombre:"+ nombre);
        Log.i("EN FOTO ACTIVITY", "correo:"+ correo);
        Log.i("EN FOTO ACTIVITY", "password:"+ password);
        Log.i("EN FOTO ACTIVITY", "universidad:"+ universidad);
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegistroActivity.class);
                intent.putExtra("valor", 1);
                intent.putExtra("nombre", nombre);
                intent.putExtra("correo", correo);
                intent.putExtra("password", password);
                intent.putExtra("universidad", universidad);
                startActivity(intent);
            }
        });
    }

    private void abrirCamara() throws IOException {
        // PARA CAPTURAR IMAGEN
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagenArchivo = null;

        try{
            imagenArchivo = crearImagen();

        }catch (IOException ex){
            Log.e("Error", ex.toString());
        }

        if(imagenArchivo != null) {
            Uri fotoUri = FileProvider.getUriForFile(this, "com.example.uniplus.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
        }
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Bundle extras = data.getExtras();
            //Bitmap imgBitmap = (Bitmap) extras.get("data");
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);

            binding.ivCamera.setImageBitmap(imgBitmap);
        }
    }

    private File crearImagen() throws IOException {
        String nombreImagen = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImagen, ".jpg", directorio);

        rutaImagen = imagen.getAbsolutePath();
        return imagen;

    }

    private void setImage(Uri uri){
        final InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            binding.ivCamera.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}