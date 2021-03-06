package com.example.clemineko.humananime;

import android.graphics.Bitmap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import java.util.concurrent.Semaphore;

/**
 * Class for global variables.
 */
public class Global {

    public static String IP_ADDRESS = "";
    public static String EXCHANGE_NAME = "task";

    public static ConnectionFactory FACTORY;
    public static Connection CONNECTION;
    public static String QUEUE_NAME;
    public static Channel CHANNEL = null;
    public static Consumer CONSUMER = null;

    public static Bitmap RESULT_BITMAP;
    public static Semaphore SEMAPHORE;
}
