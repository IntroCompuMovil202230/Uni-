package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityEspacioBinding;

public class EspacioActivity extends AppCompatActivity {
    ActivityEspacioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEspacioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReservaActivity.class);
                startActivity(intent);
            }
        });
    }
}