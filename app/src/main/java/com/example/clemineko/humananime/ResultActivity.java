package com.example.clemineko.humananime;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

        // set imageView alpha to 0. An animation will set it back to 1
        resultImageView.setAlpha(0.0f);
        // display the image
        setBitmapToImageView();

        // create animation for imageView alpha and scale
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(resultImageView, View.ALPHA, 0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(resultImageView, View.SCALE_X, 1.3f, 1.0f);
        scaleXAnimator.setDuration(2000);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(resultImageView, View.SCALE_Y, 1.3f, 1.0f);
        scaleYAnimator.setDuration(2000);

        // gather animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimation, scaleXAnimator, scaleYAnimator);

        // start animations
        animatorSet.start();
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
