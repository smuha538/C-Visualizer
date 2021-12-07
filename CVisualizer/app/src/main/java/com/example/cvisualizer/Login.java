package com.example.cvisualizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth fAuth;
    private EditText email;
    private EditText password;
    private TextView registerPage;
    private Button login;
    private ProgressBar progBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        registerPage = findViewById(R.id.registrationPage);
        login = findViewById(R.id.login_button);
        progBar = findViewById(R.id.progBar);
        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(this);
        registerPage.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        String strEmail = email.getText().toString();
        String strPass = password.getText().toString();
        if (v.getId() == R.id.login_button) {
                if (TextUtils.isEmpty(strEmail)) {
                    email.setError("Enter an Email");
                    return;
                }
                if (TextUtils.isEmpty(strPass)) {
                    password.setError("Enter a Password");
                    return;
                }

                progBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Camera.class));
                        } else {
                            Toast.makeText(Login.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else if (v.getId() == R.id.registrationPage) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
            }
        }
    }