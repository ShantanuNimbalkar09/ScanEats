package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        MaterialButton barcode=findViewById(R.id.barcode);

        MaterialButton ingredients=findViewById(R.id.ingredients);

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StartScreen.this, MainActivity.class);
                startActivity(i);

            }
        });
    }
}