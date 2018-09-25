package com.example.clemineko.humananime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    private Bitmap bitmap;

    Button btnSave;
    ImageView resultImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        btnSave = findViewById(R.id.btnSave);
        resultImageView = findViewById(R.id.resultImageView);

        // receiving the image through extra data of the intent
        byte[] byteArray = getIntent().getByteArrayExtra("imageBytes");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // display the image
        setBitmapToImageView();
    }

    /**
     * Function called when the button "Save the picture" is clicked.
     * @param v Current View.
     */
    public void onSaveButtonClicked(View v){
        // test if there is an image to save
        if(bitmap == null) Toast.makeText(this, "No image", Toast.LENGTH_LONG).show();
        else{
            // save the image
            MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    bitmap,
                    "HA-" + new Random().nextInt() +".jpg",
                    "HumanAnime app image."
            );

            // tell the user the work is done (not mine by the way...)
            Toast.makeText(this, "Picture saved", Toast.LENGTH_LONG).show();
        }

        // return to the main Activity
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // create and compress a copy of our image
        Bitmap compressedBitmap = bitmap;
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        // get the bitmap bytes
        byte[] byteArray = stream.toByteArray();

        // add these bytes to the intent and call the result activity
        intent.putExtra("imageBytes", byteArray);
        startActivity(intent);
    }

    /**
     * Function to display the bitmap using the ImageView.
     * Remove the background color if it's the first time an image is displayed through this ImageView.
     */
    protected void setBitmapToImageView(){
        // display the bitmap
        resultImageView.setImageBitmap(bitmap);

        // get the background of the ImageView
        ColorDrawable drawable = (ColorDrawable) resultImageView.getBackground();

        // make the background transparent if it was not
        if(drawable.getColor() != Color.TRANSPARENT) resultImageView.setBackgroundColor(Color.TRANSPARENT);
    }
}
