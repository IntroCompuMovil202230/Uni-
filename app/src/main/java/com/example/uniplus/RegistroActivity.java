package com.example.uniplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uniplus.databinding.ActivityRegistroBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    public Boolean registroCarnet;
    public int contadorValidarCarnet;
    public String guardarNum;
    String nombre, correo, password, universidad;
    String nombrePrueba, emailPrueba, passwordPrueba, universidadPrueba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        binding.addUserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignIn = new Intent(getBaseContext(), FotoActivity.class);
                nombrePrueba = binding.nombre.getText().toString();
                emailPrueba = binding.email.getText().toString();
                passwordPrueba= binding.password.getText().toString();
                universidadPrueba = binding.universidad.getText().toString();
                Log.i("DATOS GUARDADOS", "nombre:"+ nombrePrueba);
                Log.i("DATOS GUARDADOS", "correo:"+ emailPrueba);
                Log.i("DATOS GUARDADOS", "password:"+ passwordPrueba);
                Log.i("DATOS GUARDADOS", "universidad:"+ universidadPrueba);
                intentSignIn.putExtra("nombre", nombrePrueba);
                intentSignIn.putExtra("correo", emailPrueba);
                intentSignIn.putExtra("password", passwordPrueba);
                intentSignIn.putExtra("universidad", universidadPrueba);
                startActivity(intentSignIn);
            }
        });

        try{
            Bundle b = getIntent().getExtras();
            contadorValidarCarnet = b.getInt("valor");
            nombre = b.getString("nombre");
            correo = b.getString("correo");
            password = b.getString("password");
            universidad = b.getString("universidad");

        } catch(Exception ex){
            contadorValidarCarnet = -1;
            nombre = null;
            correo = null;
            password = null;
            universidad = null;//Or some error status //
        }

        Log.i("EN REGISTRO PARA SETEAR", "nombre:"+ nombre);
        Log.i("EN REGISTRO PARA SETEAR", "correo:"+ correo);
        Log.i("EN REGISTRO PARA SETEAR", "password:"+ password);
        Log.i("EN REGISTRO PARA SETEAR", "universidad:"+ universidad);

        setearCampos(nombre, correo, password, universidad);

        if(contadorValidarCarnet == 1) {
            binding.botonRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registrarUsuario();
                }
            });
        }
        else{
            binding.botonRegistro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(RegistroActivity.this, "Debe registrar su carnet", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void setearCampos(String nombre, String correo, String password, String universidad){
        binding.nombre.setText(nombre);
        binding.email.setText(correo);
        binding.password.setText(password);
        binding.universidad.setText(universidad);
    }
    private void registrarUsuario(){
        String correo = binding.email.getText().toString().trim();
        String contrasena = binding.password.getText().toString().trim();
        String nombre = binding.nombre.getText().toString().trim();
        String universidad = binding.universidad.getText().toString().trim();

        Boolean validacionEmail;
        Boolean validacionPassword;
        Boolean validacionNombre;
        Boolean validacionUniversidad;

        //Verificar que los campos no esten vacios
        validacionEmail = estaVacio(correo);
        validacionPassword = estaVacio(contrasena);
        validacionNombre = estaVacio(nombre);
        validacionUniversidad = estaVacio(universidad);

        if(validacionEmail != false || validacionPassword != false || validacionNombre != false || validacionUniversidad != false){
            Toast.makeText(this, "Debe llenar todos los campos para continuar", Toast.LENGTH_SHORT).show();
        }
        if(validacionEmail == false && validacionPassword == false && validacionNombre == false && validacionUniversidad == false){
            progressDialog.setMessage("Realizando registro en linea...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegistroActivity.this, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show();
                        Log.i("Auxiliox2", "correo:"+ correo);
                        Log.i("Auxilio", "contra:"+ contrasena);
                        mAuth.getCurrentUser().updatePassword(contrasena);
                        Intent intentNewUser = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intentNewUser);
                    } else{
                        Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                        String message = task.getException().getMessage();
                        Log.i("FirebaseApp", "No creo el usuario: "+message);
                    }
                    progressDialog.dismiss();
                }
            });
        }
    }

    private Boolean estaVacio(String algo){
        if(TextUtils.isEmpty(algo)){
            return true;
        }
        else{
            return false;
        }
    }

    /*
    private Boolean estaVacioPassword(String email){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Se debe ingresar una contraseña", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return false;
    }

    private Boolean estaVacioNombre(String email){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            return false;
        }
    }

    private Boolean estaVacioUniversidad(String email){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Debe ingresar una Universidad", Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            return false;
        }
    } */
}