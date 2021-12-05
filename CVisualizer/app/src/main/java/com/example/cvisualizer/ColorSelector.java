package com.example.cvisualizer;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorSelector extends AppCompatActivity implements View.OnClickListener {
    int mDefaultColor;
    FirebaseAuth fAuth;
    FirebaseFirestore database;
    String UID;
    Button mButton;
    Button selectColour;
    TextView currentColour;
    EditText RBG;
    DocumentReference reference;
    Button firButton;
    Button secButton;
    Button thiButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_edit);
        currentColour = findViewById(R.id.currentColour);
        RBG = findViewById(R.id.FullRBG);
        mDefaultColor = Color.RED;
        mButton = (Button) findViewById(R.id.button);
        selectColour = findViewById(R.id.selectRBG);
        firButton = findViewById(R.id.com1);
        secButton = findViewById(R.id.com2);
        thiButton = findViewById(R.id.com3);
        findViewById(R.id.backButton).setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        UID = fAuth.getCurrentUser().getUid();

        organiseFreqColour();
        getFavouriteColour();
        getFrequentColour();


        selectColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RBG.getText().toString().equals(""))
                {
                    String rbg = RBG.getText().toString();
                    setColour(rbg, true, null);
                }
                else
                {
                    Toast.makeText(ColorSelector.this, "Enter an RBG Value", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.backButton)
        {
//            Intent back = new Intent();
//            back.putExtra("colour", mDefaultColor);
//            setResult(1, back);
//            ColorSelector=.super.onBackPressed();
            addFreqToColour();

        }

    }

    public void getFavouriteColour()
    {

        reference = database.collection("Users").document(UID).collection("FavouriteColours").document("Favourite");
        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String, Object>> favArray = (ArrayList<Map<String, Object>>) value.get("FavouriteColour");
                for (int i = 0; i < favArray.size(); i++)
                {
                    Map<String, Object> favColour = favArray.get(i);
                    String colour = (String) favColour.get("Colour");
                    setColour(colour, true, null);
                }

            }
        });
    }

    public void getFrequentColour()
    {

        reference = database.collection("Users").document(UID).collection("CommonColours").document("Common");
        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String, Object>> freArray = (ArrayList<Map<String, Object>>) value.get("FrequentColour");
                String first, second, third;
                Map<String, Object> firstColour = freArray.get(0);
                Map<String, Object> secondColour = freArray.get(1);
                Map<String, Object> thirdColour = freArray.get(2);

                first = (String) firstColour.get("Colour");
                second = (String) secondColour.get("Colour");
                third = (String) thirdColour.get("Colour");

                setColour(first, false, "first");
                setColour(second, false, "second");
                setColour(third, false, "third");

            }
        });
    }

    public void organiseFreqColour()
    {
        reference = database.collection("Users").document(UID).collection("CommonColours").document("Common");
        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
            {
                String strFrequency;
                int mostHighest = 0;
                int frequency;
                int index = 0;
                ArrayList<Map<String, Object>> freArray = (ArrayList<Map<String, Object>>) value.get("FrequentColour");
                Map<String, Object> freColour;
                Map<String, Object> highestColour = null;
                Map<String, Object> indexZero = freArray.get(0);



                for (int i = 0; i < freArray.size(); i++)
                {
                    freColour = freArray.get(i);
                    strFrequency = (String) freColour.get("Frequency");
                    frequency = Integer.parseInt(strFrequency);
                    if (frequency > mostHighest)
                    {
                        mostHighest = frequency;
                        highestColour = freArray.get(i);
                        index = i;
                    }
                }
                freArray.set(0, highestColour);
                freArray.set(index, indexZero);
                mostHighest = 0;
                for (int i = 1; i < freArray.size(); i++)
                {
                    freColour = freArray.get(i);
                    strFrequency = (String) freColour.get("Frequency");
                    frequency = Integer.parseInt(strFrequency);
                    if (frequency > mostHighest)
                    {
                        mostHighest = frequency;
                        highestColour = freArray.get(i);
                        index = i;
                    }
                }
                Map<String, Object> indexOne = freArray.get(1);
                freArray.set(1, highestColour);
                freArray.set(index, indexOne);
                mostHighest = 0;
                Boolean in = false;
                for (int i = 2; i < freArray.size(); i++)
                {
                    freColour = freArray.get(i);
                    strFrequency = (String) freColour.get("Frequency");
                    frequency = Integer.parseInt(strFrequency);
                    if (frequency > mostHighest)
                    {
                        mostHighest = frequency;
                        highestColour = freArray.get(i);
                        index = i;
                        in = true;
                    }
                }
                if (in)
                {
                    Map<String, Object> indexTwo = freArray.get(2);
                    freArray.set(2, highestColour);
                    freArray.set(index, indexTwo);
                }
                reference.update("FrequentColour", freArray);
            }
        });
    }

    public void addFreqToColour()
    {
        ColorDrawable cd = (ColorDrawable) currentColour.getBackground();
        int colourCode = cd.getColor();

        int r = (colourCode>>16)&0xff;
        int g = (colourCode>>8)&0xff;
        int b = colourCode&0xff;
        String addColour = r + "," + g + "," + b;
        reference = database.collection("Users").document(UID).collection("CommonColours").document("Common");
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<Map<String, Object>> freArray = (ArrayList<Map<String, Object>>) document.get("FrequentColour");
                        boolean newColour = true;
                        for (int i = 0; i < freArray.size(); i++)
                        {
                            Map<String, Object> colourObj = freArray.get(i);
                            String colour = (String) colourObj.get("Colour");

                            if (colour.equals(addColour))
                            {
                                String strFrequency = (String) colourObj.get("Frequency");
                                int frequency = Integer.parseInt(strFrequency);
                                frequency = frequency + 1;
                                strFrequency = String.valueOf(frequency);
                                colourObj.put("Frequency", strFrequency);
                                reference.update("FrequentColour", freArray);
                                newColour = false;
                                break;
                            }
                        }
                        if (newColour)
                        {
                            Map<String, Object> newColourObj = new HashMap<>();
                            newColourObj.put("Colour", addColour);
                            newColourObj.put("Frequency", "1");
                            freArray.add(newColourObj);
                            reference.update("FrequentColour", freArray);
                        }

                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setColour(String colour, boolean textview, String button)
    {
        int r;
        int g;
        int b;
        String[] split = colour.split(",");
        r = Integer.parseInt(split[0]);
        g = Integer.parseInt(split[1]);
        b = Integer.parseInt(split[2]);
        @SuppressLint("Range") int newColour = Color.rgb(r,g,b);
        if (textview)
        {
            currentColour.setBackgroundColor(newColour);
        }
        else
        {
            if (button.equals("first"))
            {
                firButton.setBackgroundColor(newColour);
            }
            else if (button.equals("second"))
            {
                secButton.setBackgroundColor(newColour);
            }
            else
            {
                thiButton.setBackgroundColor(newColour);
            }
        }

    }

    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                currentColour.setBackgroundColor(mDefaultColor);
            }
        });
        colorPicker.show();
    }
}
