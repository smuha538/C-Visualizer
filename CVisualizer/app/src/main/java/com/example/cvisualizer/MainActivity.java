package com.example.cvisualizer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;
    private Button button;
    private File photoFile;
    private String FILE_NAME = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.openCamera);

        button.setOnClickListener(this);
    }

    public void onClick (final View v) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = getPhotoFile(FILE_NAME);
            Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            startActivityForResult(takePicture, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getPhotoFile(String fileName) throws IOException {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName, ".jpg", storageDirectory);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        imageView.setImageBitmap(photo);
    }
}