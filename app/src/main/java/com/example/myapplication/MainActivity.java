package com.example.myapplication;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;


    private PreviewView previewView;
    private Button captureButton, pickFromGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.cameraView);
        captureButton = findViewById(R.id.captureButton);

        pickFromGallery = findViewById(R.id.pickFromGalleryButton);


//        Button btn = findViewById(R.id.button);
        Bitmap barcodeImage = BitmapFactory.decodeResource(getResources(), R.drawable.img);


//        captureButton.setOnClickListener(v -> processImageWithBarcodeScanner(barcodeImage));

        requestPermissionsAndStartCamera();
//        File Photofile=new File(getExternalMediaDirs()[0],"img.png");
//        ProcessCapturedImage img=new ProcessCapturedImage(Photofile);
       // captureButton.setOnClickListener(v -> captureimage());

        //for testing purpose
        captureButton.setOnClickListener(c->processImageWithBarcodeScanner(barcodeImage));


       ActivityResultLauncher<PickVisualMediaRequest>pickmedia=
               registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri->{
                   if (uri != null) {
                       Log.d("PhotoPicker", "Selected URI: " + uri);
                       File photo=new File(getExternalMediaDirs()[0], String.valueOf(uri));
                       processCapturedImage(photo);
                   } else {
                       Log.d("PhotoPicker", "No media selected");
                   }
               });


        pickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickmedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());




            }
        });

    }

    private void captureimage() {
        ImageCapture imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        ;
        File photofile = new File(getExternalMediaDirs()[0], "img.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photofile).build();
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        processCapturedImage(photofile);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                        //processCapturedImage(photofile);
                        showToast("Error capturing image");
                    }
                }
        );
    }

    private void processCapturedImage(File photoFile) {
        Bitmap capturedBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        // Now you can use the capturedBitmap for further processing
        // For example, send it to the barcode scanner
        processImageWithBarcodeScanner(capturedBitmap);
    }

    public void requestPermissionsAndStartCamera() {
        // Implement your permission request logic here
        // You can use the ActivityResultLauncher as shown in previous examples

        // Once permission is granted, start the camera
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageCapture imageCapture = new ImageCapture.Builder().build();


                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {

                    Bitmap barcodeImage = BitmapFactory.decodeResource(getResources(), R.drawable.img);

                    processImageWithBarcodeScanner(barcodeImage);
                    //close the image proxy
                    imageProxy.close();
                });
                //select back camera as the default
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void processImageWithBarcodeScanner(Bitmap bitmap) {
        //convert image to bitmap
//        Bitmap bitmap= ImageUtils.imageToBitmap(image);

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        com.google.mlkit.vision.barcode.BarcodeScanner barcodeScanner = BarcodeScanning.getClient(options);
        showToast("visible");
        Task<List<Barcode>> result = barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        int valueType = barcode.getValueType();

                        // Update UI or perform other actions
                        showToast("Raw Value: " + rawValue + "\nValue Type: " + valueType);
                        Log.e("rawValue", rawValue);
                        request(rawValue);


                    }
                }).addOnFailureListener(e -> {
                    // Handle failure
                    e.printStackTrace();
                    showToast("Barcode scanning failed");
                });


    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    public void request(String code) {
        Toast.makeText(this, "inside Request", Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String baseUrl = "http://172.20.10.4:8080";
        String endpoint = "/product/";
        String url = baseUrl + endpoint + code;

        Log.d("Request", "URL: " + url);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showToast("inside onResponse");
                Log.d("Request", "Inside onResponse");
                Log.d("Request", "Response: " + response.toString());
                handleApiResponse(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Request", "Inside onError");
                Log.e("Request", "Error: " + error.toString());
                showToast("inside onError");
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void handleApiResponse(String responseData) {
        Intent intent = new Intent(MainActivity.this, capturedData.class);
        intent.putExtra("captured_data", responseData);
        startActivity(intent);
    }



}












