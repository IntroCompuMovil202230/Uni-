package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.uniplus.databinding.ActivityEspacioBinding;
import com.example.uniplus.databinding.ActivityMenuBinding;
import com.example.uniplus.databinding.ActivityMenuClienteBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MenuClienteActivity extends AppCompatActivity {

    ActivityMenuClienteBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Metodo para cerrar sesion
                CerrarSesion();
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
            }
            return true;
        });
    }

    //Metodo cerrar sesion
    private void CerrarSesion(){
        mAuth.signOut(); //Cierra la sesion del usuario
        Toast.makeText(this, "Ha cerrado sesión", Toast.LENGTH_SHORT).show();
        //Nos manda para el main activity
        startActivity(new Intent(MenuClienteActivity.this, MainActivity.class));
    }
}