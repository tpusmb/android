package com.example.clemineko.humananime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
    }


    /**
     * Function called when the "Work with picture" button is clicked.
     * @param v
     */
    public void onPictureButtonClicked(View v){
        // call the main activity
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v
     */
    public void onConnectButtonClicked(View v){
        // call the connection activity
        Intent intent = new Intent(MenuActivity.this, ConnectionActivity.class);
        startActivity(intent);
    }
}
