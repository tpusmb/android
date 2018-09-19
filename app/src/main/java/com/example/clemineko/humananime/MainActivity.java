package com.example.clemineko.humananime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int GALLERY_REQUEST = 20;

    Button btnCamera;
    Button btnGallery;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        imgView = findViewById(R.id.imgView);
    }

    /**
     * Function called when the camera button is clicked.
     * @param v
     */
    public void onCameraButtonClicked(View v){
        // call the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //invoke the camera as an activity and get something back from it
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    /**
     * Function called when the gallery button is clicked.
     * @param v
     */
    public void onGalleryButtonClicked(View v){
        // call the image gallery
        Intent intent = new Intent(Intent.ACTION_PICK);

        // where we want to find our data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Uri data = Uri.parse(pictureDirectory.getPath());

        // set the data and type. Search for all image types
        intent.setDataAndType(data, "image/*");

        // invoke the gallery as an activity and get something back from it
        startActivityForResult(intent, GALLERY_REQUEST);
    }


    /**
     * Function used to handle the end of a request.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if everything processed successfully
        if(resultCode == RESULT_OK) {
            Bitmap bitmap;

            // if we are hearing back from using the camera
            if(requestCode == CAMERA_REQUEST){
                // get a bitmap from the camera activity
                bitmap = (Bitmap) data.getExtras().get("data");
                // display the image
                imgView.setImageBitmap(bitmap);
            }
            // if we are hearing back from the image gallery
            else if(requestCode == GALLERY_REQUEST){
                // get the image address
                Uri imageUri = data.getData();

                try{
                    // read the image data
                    InputStream IS = getContentResolver().openInputStream(imageUri);
                    // get a bitmap from the stream
                    bitmap = BitmapFactory.decodeStream(IS);
                    // display the image
                    imgView.setImageBitmap(bitmap);
                } catch (Exception e){
                    e.printStackTrace();
                    // display an alert to the user
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
