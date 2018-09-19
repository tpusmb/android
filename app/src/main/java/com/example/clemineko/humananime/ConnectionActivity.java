package com.example.clemineko.humananime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConnectionActivity extends AppCompatActivity {

    Button btnConfirm;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        btnConfirm = findViewById(R.id.btnConfirm);
        editText = findViewById(R.id.editText);
    }

    /**
     * Function called when the "Connect" button is clicked.
     * @param v
     */
    public void onConfirmButtonClicked(View v){

        String IP = editText.getText().toString();

        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(IP);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            Intent intent = new Intent(ConnectionActivity.this, MenuActivity.class);
            intent.putExtra("EXTRA_IP", IP);
            startActivity(intent);
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Unable to connect", Toast.LENGTH_LONG).show();
        }
    }
}
