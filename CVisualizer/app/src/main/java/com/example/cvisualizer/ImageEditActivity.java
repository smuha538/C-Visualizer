package com.example.cvisualizer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;

/**
* This class lets the user edit an image and save it
*/
public class ImageEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView rgbView;
    private ImageView selectedColor;
    private ImageView onScreenColor;
    private Bitmap bitmap;
    private Bitmap original;
    private FirebaseStorage storage;
    private FirebaseAuth fAuth;
    private String UID;
    private EditText projectName;
    private boolean enable;
    private boolean set = true;
    private int colourR = Color.WHITE;
    private int pixel;

    /**
     * Gets the user selected colour from the Colour Selector Class
     */
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
                            colourR = newColour;
                            selectedColor.setBackgroundColor(colourR);
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
        selectedColor.setBackgroundColor(colourR);
        storage = FirebaseStorage.getInstance();
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);
        fAuth = FirebaseAuth.getInstance();
        UID = fAuth.getUid();
        projectName = findViewById(R.id.projectName);
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.enableButton).setOnClickListener(this);
        findViewById(R.id.colorButton).setOnClickListener(this);
        findViewById(R.id.resetButton).setOnClickListener(this);
        findViewById(R.id.backButton).setOnClickListener(this);
        imageView.setOnTouchListener(onTouchListener());
        Intent camera = getIntent();
        Bundle imageInfo = camera.getExtras();
        File cameraImage = (File) imageInfo.get("path");
        Bitmap photo = BitmapFactory.decodeFile(cameraImage.getAbsolutePath());
        imageView.setImageBitmap(photo);
        String name = (String) imageInfo.get("name");
        if (!name.equals(""))
        {
            projectName.setText(name);
        }


    }


    @SuppressLint("SetTextI18n")
    public void onClick(View v) {
         // Toggles the ability to change the colour or just view the rbg values and colour of the hovered pixel
        if (v.getId() == R.id.enableButton) {
            ToggleButton enableOption = findViewById(R.id.enableButton);
            if (!enable) {
                enable = true;
                enableOption.setBackgroundColor(Color.GREEN);

            } else {
                enable = false;
                enableOption.setBackgroundColor(Color.RED);
            }
        }
         // Lets the user go to the colour selector activity
        else if (v.getId() == R.id.colorButton)
        {
            Bundle info = new Bundle();
            Intent colour = new Intent(ImageEditActivity.this, ColorSelector.class);
            info.putInt("color", colourR);
            colour.putExtras(info);
            activityLauncher.launch(colour);
        }
         // Lets the user get the original bitmap to have a fresh start
        else if (v.getId() == R.id.resetButton) {
            if (original == null)
            {
                Toast.makeText(this,"No Changes Have Been Made", Toast.LENGTH_SHORT).show();
            }
            else {
                bitmap = original.copy(original.getConfig(), true);
                imageView.setImageBitmap(bitmap);
                bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            }
        }
         // Lets the user to save the bitmap to the firebase storage
        else if (v.getId() == R.id.saveButton) {
            String strProjectName = projectName.getText().toString();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap = imageView.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            StorageReference imageStorage = storage.getReference();
            StorageReference imageRef = imageStorage.child("images/" + UID + "/" + strProjectName);

            Task<Uri> urlTask = imageRef.putBytes(data).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String uri = downloadUri.toString();

                }
            });
            Toast.makeText(ImageEditActivity.this, strProjectName + " is Saved to the Cloud Storage", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Camera.class));
        }
        else if ((v.getId() == R.id.backButton)){
            startActivity(new Intent(getApplicationContext(), Camera.class));

        }
    }

    /**
     * This listener gets the rbg value of the pixel the user clicks on and lets them
     * to change the colour of the selected pixel.
     * @return true
     */
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
                    bitmap = replaceColor(bitmap, pixel, colourR);
                    imageView.setImageBitmap(bitmap);
                }

                return true;
            }

        };
    }

    /**
     * This body of code was borrowed from:
     * https://shaikhhamadali.blogspot.com/2013/08/changereplacementremove-pixel-colors-in.html
     *
     * This method gets a bitmap and recreates a new bitmap with the dimensions of the original, whilst
     * substituting the selected pixel colour with the target pixel colour.
     * @param src bitmap
     * @param fromColor selected pixel colour
     * @param targetColor target pixel colour
     * @return result new bitmap
     */
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
