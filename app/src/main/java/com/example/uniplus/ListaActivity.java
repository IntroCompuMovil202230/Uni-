package com.example.uniplus;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.uniplus.databinding.ActivityListaBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ListaActivity extends AppCompatActivity {
    ActivityListaBinding binding;
    ListView lista;

    /*
    String imagen;
    String titulo; */

    String[] titulos = new String[]{
            "Calle 40B",
            "Carrera 7",
            "plaza 39",
    };

    int[] imagenes = {
            R.drawable.cra13,
            R.drawable.cra7,
            R.drawable.plaza39,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ListView lista = (ListView) findViewById(R.id.listaEspacios);

        lista.setAdapter(new ElementoLista(this, titulos, imagenes));

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EspacioActivity.class);
                intent.putExtra("espacio", position+1);
                startActivity(intent);
            }
        });

        /*
        //LEER JSON
        JSONArray listaEspacios = null;
        try {
            listaEspacios = loadJSON();
            for (int i = 0; i<listaEspacios.length();i++){
                JSONObject info = listaEspacios.getJSONObject(i);
//                listaImagenes.add(info.getString("imagen"));
//              listaTitulos.add(info.getString("titulo"));
//                imagen = info.getString("imagen");
//                titulo = info.getString("titulo");

                //binding.listaEspacios.setAdapter();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } */


        //BARRA DE MENU
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
                case R.id.fourthFragment:
                    Intent intentPerfil = new Intent(getBaseContext(), MenuClienteActivity.class);
                    startActivity(intentPerfil);
                    return true;
            }
            return true;
        });
    }

    public JSONArray loadJSON() throws JSONException {
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

        return espacios;
    }
}