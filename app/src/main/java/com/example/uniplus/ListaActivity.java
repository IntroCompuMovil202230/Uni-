package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityListaBinding;
import com.example.uniplus.databinding.ActivityReservaBinding;

public class ListaActivity extends AppCompatActivity {

    private ActivityListaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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