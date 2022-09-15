package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.uniplus.databinding.ActivityMainBinding;
import com.example.uniplus.databinding.ActivityMenuBinding;
import com.google.android.material.imageview.ShapeableImageView;

public class MenuActivity extends AppCompatActivity {
    ShapeableImageView vista;
    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavegation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.secondFragment:
                    Intent intent = new Intent(getBaseContext(), ChatAyudaActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.thirdFragment:
                    System.out.println("pasa al tercero");
                    return true;
                case R.id.fourthFragment:
                    System.out.println("pasa al cuarto");
                    return true;
            }
            return true;
        });
    }
}