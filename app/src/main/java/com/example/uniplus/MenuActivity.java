package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.example.uniplus.databinding.ActivityMainBinding;
import com.example.uniplus.databinding.ActivityMenuBinding;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

public class MenuActivity extends AppCompatActivity implements SensorEventListener{
    ShapeableImageView vista;
    ActivityMenuBinding binding;
    SensorManager sensorManager;
    Sensor tempSensor;
    boolean isAvilable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            isAvilable = true;
        }else{
            binding.temperature.setText("Temperatura no está disponible");
        }

        //PARTE PARA LOS ESPACIOS
        binding.space1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EspacioActivity.class);
                intent.putExtra("espacio", 1);
                startActivity(intent);
            }
        });
        binding.space2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EspacioActivity.class);
                intent.putExtra("espacio", 2);
                startActivity(intent);
            }
        });
        binding.space3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), EspacioActivity.class);
                intent.putExtra("espacio", 3);
                startActivity(intent);
            }
        });

        //PARTE PARA BARRA DE MENU
        binding.bottomNavegation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        binding.temperature.setText(sensorEvent.values[0] + "°C");
        if(sensorEvent.values[0] < 15){
            binding.messageTemp.setText("Abrígate");
        }else{
            binding.messageTemp.setText("Usa protector solar");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAvilable){
            sensorManager.registerListener(this,tempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
         if(isAvilable){
             sensorManager.unregisterListener(this);
         }
    }
}