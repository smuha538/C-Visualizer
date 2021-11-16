package com.example.cvisualizer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView rgbView;
    private ImageView selectedColor;
    private ImageView onScreenColor;
    private Bitmap bitmap;
    private Bitmap original;
    private boolean enable;
    private boolean set = true;
    private int colour = Color.MAGENTA;
    private int pixel;

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == 1)
                    {
                        Intent intent = result.getData();
                        assert intent != null;
                        int newColour = intent.getIntExtra("colour", 0);
                        if (newColour != 0)
                        {
                            colour = newColour;
                            selectedColor.setBackgroundColor(colour);
                        }

                    }
                }

            }


    );

    @SuppressLint({"ClickableViewAccessibility", "WrongThread"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        imageView = findViewById(R.id.imageViewEdit);
        rgbView = findViewById(R.id.rgbView);
        onScreenColor = findViewById(R.id.onScreenColor);
        selectedColor = findViewById(R.id.selectedColor);
        selectedColor.setBackgroundColor(colour);

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);


        findViewById(R.id.enableButton).setOnClickListener(this);
        findViewById(R.id.colorButton).setOnClickListener(this);
        findViewById(R.id.resetButton).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);

        imageView.setOnTouchListener(onTouchListener());


    }


    @SuppressLint("SetTextI18n")
    public void onClick(View v) {

        if (v.getId() == R.id.enableButton) {
            TextView enableOption = findViewById(R.id.enableButton);
            if (!enable) {
                enable = true;
                //enableOption.setText("Enabled");

            } else {
                enable = false;
                //enableOption.setText("Disabled");
            }
        }
        else if (v.getId() == R.id.colorButton)
        {
            Intent colour = new Intent(ImageEditActivity.this, ColorSelector.class);
            activityLauncher.launch(colour);
        }
        else if (v.getId() == R.id.resetButton)
        {
            bitmap = original.copy(original.getConfig(),true);
            imageView.setImageBitmap(bitmap);
            bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        }
        else if ((v.getId() == R.id.logout)){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }

    }

    public View.OnTouchListener onTouchListener()
    {
        return new View.OnTouchListener()
        {

            @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    bitmap = imageView.getDrawingCache();
                    pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);

                    onScreenColor.setBackgroundColor(Color.rgb(r, g, b));
                    rgbView.setText("R: " + r + "\nG: " + g + "\nB: " + b);

                }

                if(enable) {
                    bitmap = replaceColor(bitmap, pixel, colour);
                    imageView.setImageBitmap(bitmap);
                }

                return true;
            }

        };
    }




    private Bitmap replaceColor(Bitmap src,int fromColor, int targetColor) {
        if(src == null) {
            return null;
        }
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        if(set)
        {
            original = src.copy(src.getConfig(), true);
            set = false;
        }
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        for(int x = 0; x < pixels.length; ++x) {
            pixels[x] = (pixels[x] == fromColor) ? targetColor : pixels[x];
        }
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height);

        return result;
    }

}