package com.example.clemineko.humananime;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbitmq.client.ConnectionFactory;

public class ConnectionActivity extends AppCompatActivity  {

    Button btnConfirm;
    EditText editText;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        btnConfirm = findViewById(R.id.btnConfirm);
        editText = findViewById(R.id.editText);

        mp = MediaPlayer.create(this, R.raw.eesound);

        if(!Global.IP_ADDRESS.equals("")){
            editText.setText(Global.IP_ADDRESS);
        }
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v Current View.
     */
    public void onConfirmButtonClicked(View v){
        // Easter Egg
        if(editText.getText().toString().equals("happy feet")){
            Toast.makeText(this, "Wombo combo!", Toast.LENGTH_LONG).show();
            if(!mp.isPlaying()) mp.start();
        }
        else{
            // create a thread to make a connection with a RabbitMQ server
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // get the entered text by the user
                        String IP = editText.getText().toString();

                        if (!Global.IP_ADDRESS.equals(IP)){
                            Global.FACTORY = new ConnectionFactory();
                            // connect to the IP
                            Global.FACTORY.setHost(IP);

                            // set a connection channel
                            Global.CONNECTION = Global.FACTORY.newConnection();
                            Global.CHANNEL = Global.CONNECTION.createChannel();
                            Global.CHANNEL.exchangeDeclare(Global.EXCHANGE_NAME, "direct");
                            Global.QUEUE_NAME = Global.CHANNEL.queueDeclare().getQueue();
                            Global.CHANNEL.queueBind(Global.QUEUE_NAME, Global.EXCHANGE_NAME, "result");

                            // save the IP address
                            Global.IP_ADDRESS = IP;
                        }

                        // call the menu activity
                        Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        }
    }
}
