package com.example.uniplus;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityTomarfotoBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TomarfotoActivity extends AppCompatActivity {
    ActivityTomarfotoBinding binding;
    Uri uriCamera;

    ActivityResultLauncher<Uri> getImageCamera = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    setImage(uriCamera);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomarfoto);
        binding.botonAgregarCarne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FileProvider
                File file = new File(getFilesDir(), "picFromCamera");
                uriCamera = FileProvider.getUriForFile(view.getContext(), getApplicationContext().getPackageName() + ".fileprovider", file);
                getImageCamera.launch(uriCamera);
            }
        });
    }

    private void setImage(Uri uri) {
        final InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            binding.imagenCarne.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}