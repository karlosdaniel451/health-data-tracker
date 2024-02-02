package com.example.ubqprojectclient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            SessionQueriesService.fetchSessionQueries(new SessionQueriesService.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> {
                        Fragment selectedFragment = null;
                        if (item.getItemId() == R.id.nav_home) {
                            selectedFragment = new HomeFragment();
                        } else if (item.getItemId() == R.id.nav_report) {
                            selectedFragment = new ReportFragment();
                        }

                        if (selectedFragment != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(result);

                                if (jsonArray.length() > 0) {
                                    JSONObject firstObject = jsonArray.getJSONObject(0);

                                    Bundle bundle = new Bundle();

                                    Iterator<String> keys = firstObject.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        String value = firstObject.getString(key);
                                        bundle.putString(key, value); // Add each key-value pair to the bundle
                                    }

                                    selectedFragment.setArguments(bundle);

                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, selectedFragment)
                                            .commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        // Handle the error, perhaps show a message to the user
                    });
                }
            });
            return true;
        });
        bottomNav.setSelectedItemId(R.id.nav_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "sensorAlerts",
                    "Sensor Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        // Check and request multiple permissions together
        List<String> permissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS_REQUEST_CODE);
        } else {
            Intent serviceNotificationIntent = new Intent(this, NotificationService.class);
            ContextCompat.startForegroundService(this, serviceNotificationIntent);

            Intent serviceIntent = new Intent(this, SensorService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }

    int MULTIPLE_PERMISSIONS_REQUEST_CODE = 84984984;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Intent serviceNotificationIntent = new Intent(this, NotificationService.class);
                ContextCompat.startForegroundService(this, serviceNotificationIntent);

                Intent serviceIntent = new Intent(this, SensorService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
