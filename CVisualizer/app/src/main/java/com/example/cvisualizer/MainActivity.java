package com.example.cvisualizer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private TextView textView;
    private Bitmap bitmap;
    //private Bitmap newImage;
    int pixel;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            Intent log = new Intent(this, ImageEditActivity.class);
            startActivity(log);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.login_button).setOnClickListener(this);


    }
}