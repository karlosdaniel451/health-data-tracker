package com.example.ubqprojectclient;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {

    public static Dashboard temperatureDashboard;
    public static Dashboard humidityDashboard;
    public static Dashboard noiseLevelDashboard;
    public static Dashboard heartFrequencyDashboard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize charts
        LineChart lineChartTemperature = view.findViewById(R.id.chartTemperature);
        LineChart lineChartHumidity = view.findViewById(R.id.chartHumidity);
        LineChart lineChartNoiseLevel = view.findViewById(R.id.chartNoiseLevel);
        LineChart lineChartHeartFrequency = view.findViewById(R.id.chartHeartFrequency);

        // Initialize spinners
        Spinner spinnerTemperature = view.findViewById(R.id.spinnerTemperature);
        Spinner spinnerHumidity = view.findViewById(R.id.spinnerHumidity);
        Spinner spinnerNoiseLevel = view.findViewById(R.id.spinnerNoiseLevel);
        Spinner spinnerHeartFrequency = view.findViewById(R.id.spinnerHeartFrequency);

        temperatureDashboard = new Dashboard("Temperature", "ºC");
        humidityDashboard = new Dashboard("Humidity", "%");
        noiseLevelDashboard = new Dashboard("Noise Level", "dB");
        heartFrequencyDashboard = new Dashboard("Heart Frequency", "bpm");

        // Set up listeners for each spinner
        setupChartCard(temperatureDashboard, spinnerTemperature, lineChartTemperature, "Temperature", Color.rgb(255, 70, 50));
        setupChartCard(humidityDashboard, spinnerHumidity, lineChartHumidity, "Humidity", Color.rgb(70, 130, 180));
        setupChartCard(noiseLevelDashboard, spinnerNoiseLevel, lineChartNoiseLevel, "Noise Level", Color.rgb(240, 200, 80));
        setupChartCard(heartFrequencyDashboard, spinnerHeartFrequency, lineChartHeartFrequency, "Heart Frequency", Color.rgb(220, 20, 60));

        return view;
    }

    private void setupChartCard(Dashboard dashboard, Spinner spinner, LineChart lineChart, String dashboardTitle, int color) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                ArrayList<SensorData> sensorData = getSensorData(selectedOption);

                if (lineChart.getData() != null) {
                    lineChart.getData().clearValues();
                    dashboard.setData(new ArrayList<>());
                }
                lineChart.invalidate();
                for (SensorData data : sensorData) {
                    dashboard.addData(data.getTimestamp(), data.getSensorValue(dashboardTitle));
                }

                lineChart.setData(dashboard.getLineData(color));

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return "";
                    }
                });

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                DashboardMarker mv = new DashboardMarker(getContext(), dashboardTitle, R.layout.dashboard_marker);
                lineChart.setMarker(mv);

                lineChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private ArrayList<SensorData> getSensorData(String timeInterval) {
        String endDateStr = DateUtils.getNowDateStringInUTC();
        String startDateStr = "";

        switch (timeInterval) {
            case "Last hour":
                DateTimeFormatter formatter;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime dateTime = LocalDateTime.parse(endDateStr, formatter);

                    startDateStr = dateTime.minusHours(1).toString();
                }
                break;
            case "Last day":
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime dateTime = LocalDateTime.parse(endDateStr, formatter);

                    startDateStr = dateTime.minusDays(1).toString();
                }
                break;
            case "Last week":
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    LocalDateTime dateTime = LocalDateTime.parse(endDateStr, formatter);

                    startDateStr = dateTime.minusWeeks(1).toString();
                }
                break;
        }

        ArrayList<SensorData> sensorData = SensorDataManager.getSensorData(
                startDateStr + "Z", endDateStr + "Z",
                null, null,
                null, null,
                null, null,
                null, null, false);

        Collections.reverse(sensorData);
        return sensorData;
    }
}

