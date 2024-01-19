package com.example.ubqprojectclient;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class SensorService extends Service {
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;

    private final Handler handler = new Handler();

    private BigDecimal lastNoise = null;
    private BigDecimal lastTemperature = null;
    private BigDecimal lastHumidity = null;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private static final long NOTIFICATION_INTERVAL = 600000; // 10 minutes in milliseconds
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This is a started service, not a bound service, so return null
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = location -> {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        };

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, getNotification());
        handler.post(collectSensorDataRunnable);
        return START_STICKY;
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, "sensorAlerts")
                .setContentTitle("Sensor Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.home) // Replace with your notification icon
                .setContentIntent(pendingIntent)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up resources here
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (isRecording) {
            stopAudioRecording();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(locationListener);
        }
        handler.removeCallbacks(collectSensorDataRunnable);
        sensorManager.unregisterListener(sensorEventListener);
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        handler.post(collectSensorDataRunnable);
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
                .setSmallIcon(R.drawable.home) // replace with your icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            boolean dataUpdated = false;

            BigDecimal bd = new BigDecimal(Float.toString(event.values[0]));
            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                lastTemperature = bd.setScale(2, RoundingMode.HALF_UP);
                dataUpdated = true;
            } else if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                lastTemperature = bd.setScale(2, RoundingMode.HALF_UP);
                dataUpdated = true;
            }

            // Check if both readings are available
            if (dataUpdated && lastTemperature != null && lastHumidity != null) {
                if (lastNoise.compareTo(BigDecimal.ZERO) != 0) {
                    SensorDataManager.saveSensorData(new SensorData(getNowDateInUTC(), lastTemperature, lastHumidity, lastNoise));
                    stopAudioRecording();
                }

                sensorManager.unregisterListener(this, temperatureSensor);
                sensorManager.unregisterListener(this, humiditySensor);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes if needed
        }
    };

    private String getNowDateInUTC() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.format(new Date());
    }
    private final Runnable collectSensorDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRecording) {
                startAudioRecording();
            }

            handler.postDelayed(() -> {
                lastNoise = getNoiseLevel();

                if (temperatureSensor != null) {
                    sensorManager.registerListener(sensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
                }

                if (humiditySensor != null) {
                    sensorManager.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
                }

                if (temperatureSensor == null || humiditySensor == null) {
                    fetchWeatherData(currentLatitude, currentLongitude);
                }

                handler.postDelayed(this, 10000); // Schedule the next run after 10 seconds
            }, 5000); // Delay for 5 seconds to allow brief audio recording
        }
    };


    private void startAudioRecording() {
        if (mediaRecorder == null) {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            int audioBitRate = profile.audioBitRate;
            int audioChannels = profile.audioChannels;
            int audioSampleRate = profile.audioSampleRate;

            mediaRecorder = new MediaRecorder();

            mediaRecorder.setAudioChannels(audioChannels);
            mediaRecorder.setAudioEncodingBitRate(audioBitRate);
            mediaRecorder.setAudioSamplingRate(audioSampleRate);

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            String filePath = this.getExternalFilesDir(null).getAbsolutePath() + "/audio.3gp";
            mediaRecorder.setOutputFile(filePath);


            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
            } catch (IOException e) {
                isRecording = false;
                if (mediaRecorder != null) {
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
            }
        }
    }

    private void stopAudioRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }

    private BigDecimal getNoiseLevel() {
        if (isRecording) {
            int amplitude = mediaRecorder.getMaxAmplitude();
            float noiseLevel = amplitude != 0 ? (20 * (float) Math.log10(amplitude)) : 0.0f;

            BigDecimal bd = new BigDecimal(Float.toString(noiseLevel));
            return bd.setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    private void fetchWeatherData(double latitude, double longitude) {
        new Thread(() -> {
            String location = latitude + "," + longitude;

            String apiKey = "5fa2c338473b48de9a2185228232812";
            String urlString = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + location;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    processWeatherData(stringBuilder.toString());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("WeatherAPI", "Error fetching weather data", e);
            }
        }).start();
    }

    private void processWeatherData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject current = jsonObject.getJSONObject("current");

            final float temp = (float) current.getDouble("temp_c");
            final float humidity = (float) current.getDouble("humidity");

            BigDecimal tempBD = new BigDecimal(Float.toString(temp));
            lastTemperature = tempBD.setScale(2, RoundingMode.HALF_UP);

            BigDecimal humidityBD = new BigDecimal(Float.toString(humidity));
            lastHumidity = humidityBD.setScale(2, RoundingMode.HALF_UP);

            if (lastNoise.compareTo(BigDecimal.ZERO) != 0) {
                SensorDataManager.saveSensorData(new SensorData(getNowDateInUTC(), lastTemperature, lastHumidity, lastNoise));
                stopAudioRecording();
            }
        } catch (JSONException e) {
            Log.e("WeatherAPI", "Error processing weather data", e);
        }
    }
}
