package com.example.cvisualizer;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;


public class Registration extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth fAuth;
    private EditText email;
    private EditText password;
    private TextView loginIn;
    private Button register;
    private ProgressBar progBar;
    private FirebaseFirestore database;
    private String UID;
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
        database = FirebaseFirestore.getInstance();
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
                        UID = fAuth.getCurrentUser().getUid();
                        DocumentReference referenceDoc = database.collection("Users").document(UID);
                        DocumentReference referenceDoc2 = database.collection("Users").document(UID).collection("CommonColours").document("Common");
                        DocumentReference referenceDoc3 = database.collection("Users").document(UID).collection("FavouriteColours").document("Favourite");
                        Map <String,Object> newUser = new HashMap<>();
                        newUser.put("Email", strEmail);
                        newUser.put("Password", strPass);
                        Map <String, Map <String, Map <String, Object>>> favColour = new HashMap<>();
                        Map <String, Object> colourValue = new HashMap<>();
                        colourValue.put("Colour", "");
                        colourValue.put("Exists", true);
                        Map <String, Map<String, Object>> favNum = new HashMap<>();
                        favNum.put("FirstFav", colourValue);
                        favNum.put("SecondFav", colourValue);
                        favNum.put("ThirdFav", colourValue);
                        favColour.put("FavouriteColour", favNum);
                        Map <String, Map <String, Map <String, Object>>> freqColour = new HashMap<>();
                        Map <String, Object> colourVal = new HashMap<>();
                        colourVal.put("Colour", "");
                        colourVal.put("Frequency", 0);
                        Map <String, Map<String, Object>> comNum = new HashMap<>();
                        comNum.put("FirstCommon", colourVal);
                        comNum.put("SecondCommon", colourVal);
                        comNum.put("ThirdCommon", colourVal);
                        freqColour.put("FrequentColour", comNum);


                        referenceDoc.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"Created User Profile For: " + UID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Error: " + e.toString());
                            }
                        });
                        referenceDoc3.set(favColour).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"Created User Profile For: " + UID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Error: " + e.toString());
                            }
                        });
                        referenceDoc2.set(freqColour).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"Created User Profile For: " + UID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Error: " + e.toString());
                            }
                        });
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


