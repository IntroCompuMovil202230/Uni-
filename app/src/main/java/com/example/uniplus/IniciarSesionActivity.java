package com.example.uniplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.uniplus.databinding.ActivityIniciarSesionBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class IniciarSesionActivity extends AppCompatActivity {

    ActivityIniciarSesionBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIniciarSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.botonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(binding.email.getText().toString(), binding.password.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(IniciarSesionActivity.this, MenuActivity.class));
        }
    }

    private void login (String email, String password){
        if(validateForm(email, password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(IniciarSesionActivity.this, MenuActivity.class));
                        mAuth.signOut();
                    }else{
                        String message = task.getException().getMessage();
                        Log.i("FirebaseApp", "Authentication Failed: "+message);
                        Toast toast = Toast.makeText(IniciarSesionActivity.this, "Authentication Failed: "+message, Toast.LENGTH_LONG);
                        toast.show();
                        binding.email.setText("");
                        binding.password.setText("");
                    }
                }
            });
        }
    }

    private boolean validateForm(String email, String password){
        //TODO RegExp
        return true;
    }
}