package com.example.ubqprojectclient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public class NotificationService extends Service {
    private static final String mqttHost = "broker.hivemq.com";
    private static final String topic = "enviro_pulse_notifications";
    private Mqtt5BlockingClient client;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupMqttClient();
        return START_STICKY;
    }

    private void setupMqttClient() {
        client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(mqttHost)
                .buildBlocking();

        client.connect();

        client.toAsync().subscribeWith()
                .topicFilter(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    String receivedMessage = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                    try {
                        JSONObject json = new JSONObject(receivedMessage);
                        String title = json.getString("title");
                        String message = json.getString("message");

                        sendNotification(title, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).send();
    }

    public void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sensorAlerts", "Sensor Alerts", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sensorAlerts")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_device_thermostat_144) // replace with your icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't provide binding for this service
    }
}
