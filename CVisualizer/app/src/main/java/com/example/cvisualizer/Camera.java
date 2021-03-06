package com.example.cvisualizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.cvisualizer.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


/**
 * The Camera class that provides the camera functionality mainly used for the app.
 * Asks users for permission to use camera for the app.
 *
 *  Code borrowed from https://medium.com/swlh/introduction-to-androids-camerax-with-java-ca384c522c5
 *  and https://www.youtube.com/watch?v=IrwhjDtpIU0
 */
public class Camera extends AppCompatActivity implements View.OnClickListener{
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;

    /**
     * On first use of Camera activity, system asks user for permission to use camera.
     *
     * Code borrowed partly from https://medium.com/swlh/introduction-to-androids-camerax-with-java-ca384c522c5
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

        Button captureButton = (Button) this.findViewById(R.id.capture);
        captureButton.setOnClickListener(this);
        Button galleryButton = (Button) this.findViewById(R.id.gallery);
        galleryButton.setOnClickListener(this);
        findViewById(R.id.logoutButton).setOnClickListener(this);
    }

    /**
     * There are 3 buttons on the camera layout, the capture, the gallery, and the logout button.
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {

        switch (v.getId()) {
            case R.id.capture:
                capturePhoto();
                break;
            case R.id.gallery:
                viewGallery();
                break;
            case R.id.logoutButton:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                break;
        }
    }

    /**
     * Helper method for transferring over to the Storage class to view users' gallery.
     */
    private void viewGallery() {
        Intent intent = new Intent(Camera.this, Storage.class);
        startActivity(intent);
    }

    /**
     * Helper method for onClick() for capturing a photograph.
     * Image is first saved to sdk file in the phone's
     * CameraX directory as a .jpg. Path of the file
     * is retrieved if image save is successful and then
     * sent as Intent to ImageEditActivity class.
     */
    private void capturePhoto() {
        File photoDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "CameraXPhotos");

        if(!photoDir.exists())
            photoDir.mkdir();

        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String photoFilePath = photoDir.getAbsolutePath() + "/" + timestamp + ".jpg";

        File photoFile = new File(photoFilePath);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Intent intent = new Intent(Camera.this, ImageEditActivity.class);
                        intent.putExtra("name", "");
                        intent.putExtra("path", photoFile.getAbsoluteFile());
                        startActivity(intent);

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(Camera.this, "Error saving photo: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Helper method for capturePhoto() to return an Executor.
     * The Executor runs enqueued tasks on the main thread associated with the class's context
     *
     * Code borrowed from https://www.youtube.com/watch?v=IrwhjDtpIU0
     *
     * @return ContextCompat.getMainExecutor(this)
     */
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    /**
     * Binds ImageAnalyzer to camera provider created in onCreate() and
     * adapts to camera's rotations. Also locks camera lens to record the
     * back camera of the phone.
     *
     * Code borrowed from both https://www.youtube.com/watch?v=IrwhjDtpIU0 and
     * https://medium.com/swlh/introduction-to-androids-camerax-with-java-ca384c522c5
     *
     * @param cameraProvider
     */
    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                image.close();
            }
        });

        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageCapture);
    }
}