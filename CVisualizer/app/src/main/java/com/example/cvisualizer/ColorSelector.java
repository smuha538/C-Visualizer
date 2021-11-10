package com.example.cvisualizer;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class ColorSelector extends AppCompatActivity implements View.OnClickListener
{
    int newColour;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_edit);

        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.blackButton).setOnClickListener(this);
        findViewById(R.id.orangeButton).setOnClickListener(this);
        findViewById(R.id.redButton).setOnClickListener(this);
        findViewById(R.id.blueButton).setOnClickListener(this);
        findViewById(R.id.yellowButton).setOnClickListener(this);
        findViewById(R.id.greenButton).setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.backButton)
        {
            Intent back = new Intent();
            back.putExtra("colour", newColour);
            setResult(1, back);
            ColorSelector.super.onBackPressed();
        }
        else if (v.getId() == R.id.greenButton)
        {
            newColour = Color.GREEN;
        }
        else if (v.getId() == R.id.blackButton)
        {
            newColour = Color.BLACK;
        }
        else if (v.getId() == R.id.blueButton)
        {
            newColour = Color.BLUE;
        }
        else if (v.getId() == R.id.yellowButton)
        {
            newColour = Color.YELLOW;
        }
        else if (v.getId() == R.id.redButton)
        {
            newColour = Color.RED;
        }
        else if (v.getId() == R.id.orangeButton)
        {
            newColour = Color.rgb(255,165,0);
        }
    }
}
