package com.example.uniplus;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button signIn;
    Button signUp;
=======
import android.os.Bundle;

<<<<<<< HEAD:app/src/main/java/com/example/uniplus/MainActivity.java
public class MainActivity extends AppCompatActivity {
>>>>>>> origin
=======
public class ReservaActivity extends AppCompatActivity {
>>>>>>> origin/registro_reserva:app/src/main/java/com/example/uniplus/ReservaActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD:app/src/main/java/com/example/uniplus/MainActivity.java
        setContentView(R.layout.activity_main);
<<<<<<< HEAD

        //inflate

        signIn = findViewById(R.id.signIn);
        signUp = findViewById(R.id.signUP);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SignIn.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Registro.class);
                startActivity(intent);
            }
        });
=======
>>>>>>> origin
=======
        setContentView(R.layout.activity_reserva);
>>>>>>> origin/registro_reserva:app/src/main/java/com/example/uniplus/ReservaActivity.java
    }
}