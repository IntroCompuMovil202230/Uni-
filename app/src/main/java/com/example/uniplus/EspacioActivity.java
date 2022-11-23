package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.car.ui.toolbar.MenuItem;
import com.example.uniplus.databinding.ActivityEspacioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class EspacioActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityEspacioBinding binding;

    Button bfecha, bhora, bfecha1, bhora1;
    EditText efecha, ehora, efecha1, ehora1;
    private int dia, mes, annio, hora, minutos;

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

        bfecha = binding.btnfecha1;
        bhora = binding.btnhora1;
        efecha = binding.editTextFecha;
        ehora = binding.editTextHora;

        bfecha1 = binding.btnfecha2;
        bhora1 = binding.btnhora2;
        efecha1 = binding.editTextFecha2;
        ehora1 = binding.editTextHora2;

        bfecha.setOnClickListener(this);
        bhora.setOnClickListener(this);

        bfecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                    dia = c.get(Calendar.DAY_OF_MONTH);
                    mes = c.get(Calendar.MONTH);
                    annio = c.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(EspacioActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            efecha1.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        }
                    }
                            ,dia,mes,annio);
                    datePickerDialog.show();
                }
        });

        bhora1.setOnClickListener(new View.OnClickListener() {
              final Calendar c = Calendar.getInstance();
              @Override
              public void onClick(View view) {
                  hora = c.get(Calendar.HOUR_OF_DAY);
                  minutos = c.get(Calendar.MINUTE);

                  TimePickerDialog timePickerDialog = new TimePickerDialog(EspacioActivity.this, new TimePickerDialog.OnTimeSetListener() {
                      @Override
                      public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                          ehora1.setText(hourOfDay+":"+minute);
                      }
                  }, hora, minutos, false);
                  timePickerDialog.show();
                  cambiarPrecio();
              }
        });

        imageResource = getResources().getIdentifier(imagen, null, this.getPackageName());
        binding.imageView4.setImageResource(imageResource);
        binding.tituloEspacio.setText(titulo);
        binding.descripcionEspacio.setText(descripcion);

        binding.buttonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReservaActivity.class);
                intent.putExtra("primerahora",ehora.getText().toString());
                intent.putExtra("segundahora",ehora1.getText().toString());
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

    public void cambiarPrecio(){
        binding.textViewValorTotal.setText("15.000");
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


    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        if(v==bfecha){
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            annio = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    efecha.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                }
            }
            ,dia,mes,annio);
            datePickerDialog.show();
        }
        if(v==bhora){
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                   ehora.setText(hourOfDay+":"+minute);
                }
            }, hora, minutos, false);
            timePickerDialog.show();
        }
    }
}