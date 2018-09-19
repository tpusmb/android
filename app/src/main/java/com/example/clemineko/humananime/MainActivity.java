package com.example.clemineko.humananime;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int GALLERY_REQUEST = 20;

    private Bitmap bitmap;
    private Uri imageUri;

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


        if(!hasPermission()){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // call the camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            //invoke the camera as an activity and get something back from it
            startActivityForResult(intent, CAMERA_REQUEST);
        }

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

            // if we are hearing back from using the camera
            if(requestCode == CAMERA_REQUEST){
                try{
                    // get a bitmap thanks to his uri
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    // display the image
                    imgView.setImageBitmap(bitmap);

                } catch (Exception e){
                    e.printStackTrace();
                }
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


    /**
     * Function to convert a bitmap to Base64 format
     * @param bitmap
     * @return A string containing a Base64 image
     */
    protected String bitmapToBase64(Bitmap bitmap){
        // read bytes of the bitmap
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // encode them into Base64 string and return this string
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    /**
     * Function to convert an Base64 encoded image to a bitmap
     * @param encodedImage
     * @return A bitmap representing the decoded string
     */
    protected Bitmap base64ToBitmap(String encodedImage){
        // decode and convert the encoded image to bytes
        byte[] decodedString = Base64.decode(encodedImage, Base64.NO_WRAP);

        // create a bitmap from these bytes
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private Boolean hasPermission(){
        PackageManager pm = getBaseContext().getPackageManager();
        int hasPerm = pm.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, getBaseContext().getPackageName());
        return hasPerm == PackageManager.PERMISSION_GRANTED;
    }
}
