package com.example.cvisualizer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;

  /**
     * Displays the image and its name for the user to view
     * @param savedInstanceState
     */
public class ImageDetail extends AppCompatActivity implements View.OnClickListener
{
    private StorageReference storage;
    private String strUri;
    private ImageView imageView;
    private TextView nameView;
    private File sendFile;
    private String name;

  
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_options);
        findViewById(R.id.returnButton).setOnClickListener(this);
        findViewById(R.id.editButton).setOnClickListener(this);
        findViewById(R.id.deleteButton).setOnClickListener(this);
        imageView = findViewById(R.id.detailedImage);
        nameView = findViewById(R.id.projectText);
        Intent image = getIntent();
        Bundle bun = image.getExtras();
        strUri = bun.getString("path");
        storage = FirebaseStorage.getInstance().getReferenceFromUrl(strUri);
        name = storage.getName();
        nameView.setText(name);
        try {
            File localFile = File.createTempFile("images", "png");
            storage.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bits = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    sendFile = localFile.getAbsoluteFile();
                    imageView.setImageBitmap(bits);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v)
    {
        Intent activity;
         // Deletes the image from the firebase storage
        if (v.getId() == R.id.deleteButton)
        {
            storage.delete();
            Toast.makeText(ImageDetail.this, "Image Deleted", Toast.LENGTH_SHORT).show();
            activity = new Intent(this, Storage.class);
            startActivity(activity);
        }
         // Sends the user back to the gallery/storage
        else if (v.getId() == R.id.returnButton)
        {
            activity = new Intent(ImageDetail.this, Storage.class);
            startActivity(activity);
        }
         // Sends the image path to the Image Edit Activity for editing
        else if (v.getId() == R.id.editButton)
        {
            activity = new Intent(ImageDetail.this, ImageEditActivity.class);
            activity.putExtra("path", sendFile);
            activity.putExtra("name", name);
            startActivity(activity);
        }
    }
}
