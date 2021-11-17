package com.example.cvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Registration extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth fAuth;
    private EditText email;
    private EditText password;
    private TextView loginIn;
    private Button register;
    private ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginIn = findViewById(R.id.loginPage);
        register = findViewById(R.id.signUp);
        progBar = findViewById(R.id.progressBar);


        register.setOnClickListener(this);
        loginIn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();

//        //if(fAuth.getCurrentUser() != null)
//        {
//            startActivity(new Intent(getApplicationContext(), ImageEditActivity.class));
//            finish();
//        }

    }

    @Override
    public void onClick(View v)
    {
        String strEmail = email.getText().toString();
        String strPass = password.getText().toString();
        if (v.getId() == R.id.signUp)
        {
            if (TextUtils.isEmpty(strEmail))
            {
                email.setError("Enter an Email");
                return;
            }
            if (TextUtils.isEmpty(strPass))
            {
                password.setError("Enter a Password");
                return;
            }
            int PASSWORDLENGTH = 6;
            if (strPass.length() < PASSWORDLENGTH)
            {
                password.setError("Password Must be At Least 6 Characters Long");
                return;
            }

            progBar.setVisibility(View.VISIBLE);

            fAuth.createUserWithEmailAndPassword(strEmail,strPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Registration.this, "Successfully Created User", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ImageEditActivity.class));
                    }
                    else
                    {
                        Toast.makeText(Registration.this,"Error: " + task.getException(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else if (v.getId() == R.id.loginPage)
        {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

    }
}


