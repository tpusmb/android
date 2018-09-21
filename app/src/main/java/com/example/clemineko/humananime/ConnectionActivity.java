package com.example.clemineko.humananime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class ConnectionActivity extends AppCompatActivity  {

    Button btnConfirm;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        btnConfirm = findViewById(R.id.btnConfirm);
        editText = findViewById(R.id.editText);

        if(Global.IP_ADRESS != ""){
            editText.setText(Global.IP_ADRESS);
        }
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v
     */
    public void onConfirmButtonClicked(View v){

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    // get the entered text by the user
                    String IP = editText.getText().toString();

                    try{
                        // connect to the specified IP
                        ConnectionFactory factory = new ConnectionFactory();
                        factory.setHost(IP);
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();

                        String message = "Hello World!";
                        channel.basicPublish("", "task", null, message.getBytes());

                        /*Consumer consumer = new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                                    throws IOException {
                                String message = new String(body, "UTF-8");
                                editText.setText("Received: " + message);
                            }
                        };
                        channel.basicConsume("task", true, consumer);*/

                        Global.IP_ADRESS = IP;

                        // call the menu activity with the IP address as an extra data
                        Intent intent = new Intent(ConnectionActivity.this, MenuActivity.class);
                        startActivity(intent);

                    } catch(Exception e){
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
