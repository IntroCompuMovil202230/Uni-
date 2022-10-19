package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityEspacioBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class EspacioActivity extends AppCompatActivity {
    ActivityEspacioBinding binding;

    int imageResource;
    JSONObject infoEspacio;
    String imagen;
    String titulo;
    String descripcion;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEspacioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int valor = getIntent().getIntExtra("espacio", 0);
        try {
            infoEspacio = loadJSON(valor);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            imagen = infoEspacio.getString("imagen");
            titulo = infoEspacio.getString("titulo");
            descripcion = infoEspacio.getString("descripcion");
            longitude = infoEspacio.getDouble("longitude");
            latitude = infoEspacio.getDouble("latitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageResource = getResources().getIdentifier(imagen, null, this.getPackageName());
        binding.imageView4.setImageResource(imageResource);
        binding.tituloEspacio.setText(titulo);
        binding.descripcionEspacio.setText(descripcion);

        binding.buttonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReservaActivity.class);
                startActivity(intent);
            }
        });

        binding.botonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("espacio", valor);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                startActivity(intent);
            }
        });

        binding.bottomNavegation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.firstFragment:
                    Intent intentMenuP = new Intent(getBaseContext(), MenuActivity.class);
                    startActivity(intentMenuP);
                    return true;
                case R.id.secondFragment:
                    Intent intentMapa = new Intent(getBaseContext(), MapsActivity.class);
                    intentMapa.putExtra("menu", true);
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

    public JSONObject loadJSON(int i) throws JSONException {
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
        JSONObject info = espacios.getJSONObject(i-1);

        return info;
    }
}