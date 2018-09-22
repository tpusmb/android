package com.example.clemineko.humananime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionActivity extends AppCompatActivity  {

    Button btnConfirm;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        btnConfirm = findViewById(R.id.btnConfirm);
        editText = findViewById(R.id.editText);

        if(!Global.IP_ADDRESS.equals("")){
            editText.setText(Global.IP_ADDRESS);
        }
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v Current View.
     */
    public void onConfirmButtonClicked(View v){
        // create a thread to make a connection with a RabbitMQ server
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // get the entered text by the user
                    String IP = editText.getText().toString();

                    if (!Global.IP_ADDRESS.equals(IP)){
                        // connect to the IP
                        Global.FACTORY.setHost(IP);
                        Global.CONNECTION = Global.FACTORY.newConnection();

                        // set a channel if it's the first time the user is trying a connection
                        if(Global.CHANNEL == null) {
                            Global.CHANNEL.exchangeDeclare(Global.EXCHANGE_NAME, "direct");
                            Global.QUEUE_NAME = Global.CHANNEL.queueDeclare().getQueue();
                            Global.CHANNEL.queueBind(Global.QUEUE_NAME, Global.EXCHANGE_NAME, "result");
                            Global.CHANNEL.basicConsume(Global.QUEUE_NAME, true, Global.CONSUMER);
                        }

                        // save the IP address
                        Global.IP_ADDRESS = IP;
                    }

                    // quick send to test the connection
                    String message = "Connection from android application";
                    Global.CHANNEL.basicPublish("", "Test", null, message.getBytes());

                    // call the menu activity
                    Intent intent = new Intent(ConnectionActivity.this, MenuActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
