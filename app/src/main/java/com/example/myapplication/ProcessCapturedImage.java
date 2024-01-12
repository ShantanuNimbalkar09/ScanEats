package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.util.List;

public class ProcessCapturedImage {

        File photoFile;

        ProcessCapturedImage(File photoFile){
            photoFile=this.photoFile;
        }



    private void processImageWithBarcodeScanner(Bitmap bitmap) {
        Bitmap capturedBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        //convert image to bitmap
//        Bitmap bitmap= ImageUtils.imageToBitmap(image);

        BarcodeScannerOptions options=new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        InputImage inputImage=InputImage.fromBitmap(bitmap,0);

        com.google.mlkit.vision.barcode.BarcodeScanner barcodeScanner= BarcodeScanning.getClient(options);
//        showToast("visible");
        Task<List<Barcode>> result=barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode:barcodes){
                        String rawValue = barcode.getRawValue();
                        int valueType = barcode.getValueType();

                        // Update UI or perform other actions
//                        showToast("Raw Value: " + rawValue + "\nValue Type: " + valueType);
                        Log.e("rawValue",rawValue);

//                        request(rawValue);


                    }
                }).addOnFailureListener(e -> {
                    // Handle failure
                    e.printStackTrace();
//                    showToast("Barcode scanning failed");
                });



    }
//    private void showToast(String message) {
//        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
}
