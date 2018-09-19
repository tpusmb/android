package com.example.clemineko.humananime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnGallery;

    String IP_ADRESS = "nothing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        Intent intent = getIntent();
        if (intent != null) {
            IP_ADRESS = intent.getStringExtra("EXTRA_IP");
        }
    }


    /**
     * Function called when the "Work with picture" button is clicked.
     * @param v
     */
    public void onPictureButtonClicked(View v){
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v
     */
    public void onConnectButtonClicked(View v){
        Intent intent = new Intent(MenuActivity.this, ConnectionActivity.class);
        startActivity(intent);
    }
}
