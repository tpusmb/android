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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int GALLERY_REQUEST = 20;

    private Uri imageUri;
    private Bitmap bitmap;
    private Semaphore semaphore;

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

        // a semaphore that will be unlocked with 1 authorization
        semaphore = new Semaphore(0);

        Thread thread;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Global.CONSUMER = new DefaultConsumer(Global.CHANNEL) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                                throws IOException {
                            // get the transformed image from the server
                            String newB64Image = new String(body, "UTF-8");
                            bitmap = base64ToBitmap(newB64Image);

                            // unlock the semaphore
                            semaphore.release();
                        }
                    };

                    Global.CHANNEL.basicConsume(Global.QUEUE_NAME, true, Global.CONSUMER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();
        // create a function to receive the server response

    }

    /**
     * Function called when the button "Take a picture" is clicked.
     * @param v Current View.
     */
    public void onCameraButtonClicked(View v){
        // check if app is allowed to access local storage
        if(hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            // if the permission is allowed, we prepare the image data to find his uri
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");

            // get the image uri
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // call the camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            //invoke the camera as an activity and get something back from it
            startActivityForResult(intent, CAMERA_REQUEST);
        }
        else{
            // if not, we make a request to the user to grant it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * Function called when the button "Import a picture" is clicked.
     * @param v Current View.
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
     * Function called when the button "Convert into anime" is clicked.
     * @param v Current View.
     */
    public void onConvertAnimeButtonClicked(View v){
        convertImage("anime");
    }

    /**
     * Function called when the button "Convert into cat" is clicked.
     * @param v Current View.
     */
    public void onConvertCatButtonClicked(View v){
        convertImage("cat");
    }


    /**
     * Function used to handle the end of a request.
     * @param requestCode An int which identify who the result came from.
     * @param resultCode An int code returned by the child activity.
     * @param data An Intent which can have various data attached to it.
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
     * Function to convert the image.
     * @param conversionType The type of conversion we want to apply to the image.
     */
    protected void convertImage(final String conversionType){
        // test if there is an image selected and if the app has been connected with the server.
        if(bitmap == null) Toast.makeText(this, "No image", Toast.LENGTH_LONG).show();
        else if (Global.CHANNEL == null) Toast.makeText(this, "No connection configured", Toast.LENGTH_LONG).show();
        else{
            // create a thread to communicate with the server
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // reduce the image size and convert it into base64. A large image is slower to send and unnecessary
                        String base64Image = bitmapToBase64(resizeBitmap(bitmap));
                        // send the base64 image to the server
                        Global.CHANNEL.basicPublish(Global.EXCHANGE_NAME, conversionType, null, base64Image.getBytes());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            // wait for the semaphore to be unlocked
            try {
                semaphore.acquire();
                // if the process terminated correctly: display the transformed image
                imgView.setImageBitmap(bitmap);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Function to convert a bitmap to Base64 format.
     * @param bitmap The bitmap we want to convert.
     * @return A string containing a Base64 image.
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
     * Function to convert a Base64 encoded image to a Bitmap.
     * @param encodedImage The String we want to decode.
     * @return A Bitmap representing the decoded string.
     */
    protected Bitmap base64ToBitmap(String encodedImage){
        // decode and convert the encoded image to bytes
        byte[] decodedString = Base64.decode(encodedImage, Base64.NO_WRAP);

        // create a bitmap from these bytes
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Function to resize a Bitmap if it is too large
     * @param bitmapToResize The Bitmap we want to resize
     * @return A resized Bitmap
     */
    protected Bitmap resizeBitmap(Bitmap bitmapToResize){
        // get the image size
        int bitmapSize = bitmapToResize.getWidth() * bitmapToResize.getHeight();

        // set the reduction ratio depending of the image size
        double ratio = 1.0;
        if(bitmapSize > 4000000) ratio = 0.2;
        else if(bitmapSize > 1000000) ratio = 0.4;
        else if(bitmapSize > 500000) ratio = 0.6;
        else if(bitmapSize > 250000) ratio = 0.8;

        // apply the ratio to both image's width and height
        return  Bitmap.createScaledBitmap(bitmapToResize,(int)(bitmap.getWidth()*ratio), (int)(bitmapToResize.getHeight()*ratio), true);
    }

    /**
     * Function to check if a specific permission is allowed.
     * @param permission The permission we want to check.
     * @return True if the permission is allowed. False if it doesn't.
     */
    private Boolean hasPermission(String permission){
        PackageManager pm = getBaseContext().getPackageManager();
        int hasPerm = pm.checkPermission(permission, getBaseContext().getPackageName());
        return hasPerm == PackageManager.PERMISSION_GRANTED;
    }
}
