package com.example.cvisualizer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

/**
 * This body of code was borrowed from:
 * https://www.geeksforgeeks.org/how-to-view-all-the-uploaded-images-in-firebase-storage/
 */
public class Storage extends AppCompatActivity implements View.OnClickListener
{
    private FirebaseAuth fAuth;
    private FirebaseStorage storage;
    private String UID;
    ArrayList<String> imagelist;
    RecyclerView recyclerView;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        imagelist=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerview);
        findViewById(R.id.prevButton).setOnClickListener(this);
        adapter = new ImageAdapter(imagelist,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(null));
        fAuth = FirebaseAuth.getInstance();
        UID = fAuth.getUid();
        storage = FirebaseStorage.getInstance();
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/" + UID + "/");
        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference file:listResult.getItems()){
                    file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagelist.add(uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.prevButton){
            Intent i = new Intent(Storage.this, Camera.class);
            startActivity(i);
        }
    }
}
