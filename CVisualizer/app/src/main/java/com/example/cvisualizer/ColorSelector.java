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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorSelector extends AppCompatActivity implements View.OnClickListener
{
    ArrayList<Integer> favColors = new ArrayList<>();
    int mDefaultColor;
    FirebaseAuth fAuth;
    FirebaseFirestore database;
    String UID;
    Button mButton;
    Button selectColour;
    ImageView currentColour;
    EditText RBG;
    DocumentReference reference;
    Button firButton;
    Button secButton;
    Button thiButton;
    ImageButton buttonStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_edit);

        Intent main = getIntent();
        Bundle colorInfo = main.getExtras();
        int currentC = colorInfo.getInt("color");
        currentColour = findViewById(R.id.currentColor);
        currentColour.setBackgroundColor(currentC);

        RBG = findViewById(R.id.enterRBG);
        mDefaultColor = Color.RED;
        mButton = (Button) findViewById(R.id.bttnColorSel);
        selectColour = findViewById(R.id.bttnCurSel);
        firButton = findViewById(R.id.freqColor1);
        secButton = findViewById(R.id.freqColor2);
        thiButton = findViewById(R.id.freqColor3);
        buttonStar = findViewById(R.id.favorite_button);
        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.freqColor1).setOnClickListener(this);
        findViewById(R.id.freqColor2).setOnClickListener(this);
        findViewById(R.id.freqColor3).setOnClickListener(this);
        findViewById(R.id.favorite_button).setOnClickListener(this);
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
                    favouriteButtonColour();
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
        ColorDrawable cd = (ColorDrawable) currentColour.getBackground();
        int colorCode = cd.getColor();
        ColorDrawable freq;
        int freColour;

         // Send the currentcolour back to the Image Edit Activity and calls the addFreqToColour()
         // method to add 1 to the frequency of the colour
        if (v.getId() == R.id.backButton)
        {
            addFreqToColour();
            Intent back = new Intent();
            back.putExtra("colour", colorCode);
            setResult(1, back);
            ColorSelector.super.onBackPressed();

        }
         // Lets the user pick the colour from the most commonly used colour
        else if (v.getId() == R.id.freqColor1)
        {
            freq = (ColorDrawable) firButton.getBackground();
            freColour = freq.getColor();
            currentColour.setBackgroundColor(freColour);

        }
        //  Lets the user pick the colour from the second most commonly used colour
        else if (v.getId() == R.id.freqColor2)
        {
            freq = (ColorDrawable) secButton.getBackground();
            freColour = freq.getColor();
            currentColour.setBackgroundColor(freColour);
        }
          //Lets the user pick the colour from the third most commonly used colour
        else if (v.getId() == R.id.freqColor3)
        {
            freq = (ColorDrawable) thiButton.getBackground();
            freColour = freq.getColor();
            currentColour.setBackgroundColor(freColour);
        }
         // Lets the user favourite a colour
        else if (v.getId() == R.id.favorite_button)
        {
            favouriteAColour();
        }
        favouriteButtonColour();

    }

    /**
     * This Method checks if the current colour is favourited or not.
     * If it is, then the favourite star button will light up, if not,
     * then it turn to normal
     */
    public void favouriteButtonColour()
    {

        ColorDrawable cd = (ColorDrawable) currentColour.getBackground();
        int colourCode = cd.getColor();
        boolean favourited = false;
        for (int i = 0; i < favColors.size(); i++)
        {
               if(favColors.get(i) == colourCode)
               {
                   buttonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
                   favourited = true;
                   break;
               }
        }
        if (!favourited)
        {
            buttonStar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
        }
    }

    /**
     * This method gets the current colour from the current colour ImageView
     * @return currentColour
     */
    public String getCurrentColour()
    {
        ColorDrawable cd = (ColorDrawable) currentColour.getBackground();
        int colourCode = cd.getColor();
        int r = (colourCode>>16)&0xff;
        int g = (colourCode>>8)&0xff;
        int b = colourCode&0xff;
        String currentColour = r + "," + g + "," + b;
        return currentColour;
    }

    /**
     * This method add a colour to the favourite database on firebase when the user clicks on
     * the favourite star button. If a colour is already a favourite, then it removes the colour
     * from the database
     */
    private void favouriteAColour()
    {

        String addColour = getCurrentColour();
        reference = database.collection("Users").document(UID).collection("FavouriteColours").document("Favourite");
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<Map<String, Object>> freArray = (ArrayList<Map<String, Object>>) document.get("FavouriteColour");
                        boolean exists = false;
                        for (int i = 0; i < freArray.size(); i++)
                        {
                            Map<String, Object> colourObj = freArray.get(i);
                            String colour = (String) colourObj.get("Colour");

                            if (colour.equals(addColour))
                            {
                                freArray.remove(i);
                                reference.update("FavouriteColour", freArray);
                                exists = true;
                                break;
                            }
                        }
                        if (!exists)
                        {
                            Map<String, Object> newColourObj = new HashMap<>();
                            newColourObj.put("Colour", addColour);
                            freArray.add(newColourObj);
                            reference.update("FavouriteColour", freArray);
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

    /**
     * This method gets all the favourite colours from the database and sends them to the setColour()
     * method to add them to an arraylist, which is then sent to the recyclerview to be displayed
     */
    public void getFavouriteColour()
    {

        reference = database.collection("Users").document(UID).collection("FavouriteColours").document("Favourite");
        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                ArrayList<Map<String, Object>> favArray = (ArrayList<Map<String, Object>>) value.get("FavouriteColour");
                favColors = new ArrayList<>();
                if (!favArray.isEmpty()) {
                    for (int i = 0; i < favArray.size(); i++) {
                        Map<String, Object> favColour = favArray.get(i);
                        String colour = (String) favColour.get("Colour");
                        setColour(colour, false, "favourite");
                    }
                    favouriteButtonColour();
                    initRecyclerView();
                }
            }
        });
    }

    /**
     * This method gets the top most used colours from the database and sends them to the setColour()
     * method to be displayed on the screen
     */
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

    /**
     * This method gets all the colours the user has used from the database and then organises them
     * in the order of the most used (top 3 only) and then updates the database.
     */
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

    /**
     * This method increases the frequency of a colour in the database each time the user uses it
     */
    public void addFreqToColour()
    {

        String addColour = getCurrentColour();
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

    /**
     * This colour sets the colour on the screen or puts them in an array depending on what gets
     * passed as the parameter
     * @param colour the string colour
     * @param currentColourSet set current clour if true
     * @param type type of colour setting
     */
    public void setColour(String colour, boolean currentColourSet, String type)
    {
        int r;
        int g;
        int b;
        String[] split = colour.split(",");
        r = Integer.parseInt(split[0]);
        g = Integer.parseInt(split[1]);
        b = Integer.parseInt(split[2]);
        @SuppressLint("Range") int newColour = Color.rgb(r,g,b);
        if (currentColourSet) {
            currentColour.setBackgroundColor(newColour);
        }
        else if (type.equals("favourite"))
        {
            favColors.add(newColour);
        }
        else
        {
            if (type.equals("first"))
            {
                firButton.setBackgroundColor(newColour);
            }
            else if (type.equals("second"))
            {
                secButton.setBackgroundColor(newColour);
            }
            else
            {
                thiButton.setBackgroundColor(newColour);
            }
        }

    }

    /**
     * This method lets the user to use the AmbilWarna Colour picker
     */
    public void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDefaultColor = color;
                currentColour.setBackgroundColor(mDefaultColor);
                favouriteButtonColour();
            }
        });
        colorPicker.show();
    }


    /**
     * This recyclerview displays all the favourite colours on the screen
     */
    private void initRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView favRecycler = findViewById(R.id.favRecycler);
        favRecycler.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, favColors);
        favRecycler.setAdapter(adapter);
        favouriteButtonColour();
    }
}
