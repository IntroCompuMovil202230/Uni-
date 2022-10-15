package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityIniciarSesionBinding;
import com.example.uniplus.databinding.ActivityRegistroBinding;

public class IniciarSesionActivity extends AppCompatActivity {

    ActivityIniciarSesionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityIniciarSesionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        binding.botonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignIn = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intentSignIn);
            }
        });

    }
}