package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class capturedData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_data);
        TextView displayData=findViewById(R.id.data);

        Intent i=getIntent();

        String data=i.getStringExtra("captured_data");
        Log.e("Captired Data",data);

        try {
            JSONObject obj=new JSONObject(data);
            JSONArray arr=obj.getJSONArray("Product");
            Log.e("Captired Data",obj.getString("generic_name"));
        } catch (JSONException e) {
            e.printStackTrace();  // Print the stack trace for debugging
            Log.e("Captured Data", "Error parsing JSON: " + e.getMessage());
        }

//        try {
//            assert data != null;
//            JSONObject obj=new JSONObject(data);
//           // displayData.setText(obj.getString("id"));
//            Log.e(TAG, "Example Item: " + obj.getString("Additives"));
//
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

    }
}