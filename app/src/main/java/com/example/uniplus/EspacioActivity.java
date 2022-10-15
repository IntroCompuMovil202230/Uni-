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

        binding.bottomNavegation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.secondFragment:
                    Intent intentMapa = new Intent(getBaseContext(), MapaActivity.class);
                    startActivity(intentMapa);
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
}