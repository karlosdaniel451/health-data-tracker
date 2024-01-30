package com.example.ubqprojectclient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SensorDataService {

    private static final String API_URL = "http://3.213.156.15:8000/sensor-data/register/";
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static Integer pageSize = 10;
    protected static Integer currentPage = 1;
    protected static Integer totalPages = 1;
    protected static boolean hasNext = false;
    protected static boolean hasPrevious = false;

    public static void resetPagination() {
        SensorDataService.currentPage = 1;
        SensorDataService.totalPages = 1;
        SensorDataService.hasNext = false;
        SensorDataService.hasPrevious = false;
    }

    public static void saveSensorData(SensorData data) {
        if (data.getNoiseLevel().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        executorService.execute(() -> saveSensorDataRequest(data));
    }

    private static void saveSensorDataRequest(SensorData data) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();

            json.put("timestamp", data.getTimestamp());
            json.put("temperature", data.getTemperature());
            json.put("humidity", data.getHumidity());
            json.put("noise_level", data.getNoiseLevel());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes());
                os.flush();
            }

            conn.getResponseCode(); // You can handle the response code here
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to add sensor data
    public static ArrayList<SensorData> getSensorData(
            String startDate, String endDate,
            BigDecimal temperature, String temperatureCondition,
            BigDecimal humidity, String humidityCondition,
            BigDecimal noise, String noiseCondition,
            Integer heartFrequency, String heartFrequencyCondition,
            boolean paginated) {
        Future<ArrayList<SensorData>> future = executorService.submit(() -> getSensorDataRequest(
                startDate, endDate,
                temperature, temperatureCondition,
                humidity, humidityCondition,
                noise, noiseCondition,
                heartFrequency, heartFrequencyCondition, paginated));

        ArrayList<SensorData> sensorData = new ArrayList<>();
        try {
            sensorData = future.get();
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
        }
        return sensorData;
    }


    private static ArrayList<SensorData> getSensorDataRequest(
            String startDate, String endDate,
            BigDecimal temperature, String temperatureCondition,
            BigDecimal humidity, String humidityCondition,
            BigDecimal noise, String noiseCondition,
            Integer heartFrequency, String heartFrequencyCondition, boolean paginated) {
        ArrayList<SensorData> sensorData = new ArrayList<>();
        try {
            // Construct the URL with query parameters
            StringBuilder urlBuilder = new StringBuilder(API_URL);

            if (paginated) {
                urlBuilder.append("?page=").append(SensorDataService.currentPage.toString());
                urlBuilder.append("&page_size=").append(SensorDataService.pageSize.toString());
            } else {
                urlBuilder.append("?page=").append("1");
                urlBuilder.append("&page_size=").append("100000");
            }

            if (startDate != null && !startDate.isEmpty()) {
                urlBuilder.append("&start_date=").append(startDate);
            }

            if (endDate != null && !endDate.isEmpty()) {
                urlBuilder.append("&end_date=").append(endDate);
            }

            // Append other filter parameters with conditions
            if (temperature != null && temperatureCondition != null) {
                temperature = temperature.setScale(2, RoundingMode.HALF_UP);
                urlBuilder.append("&temperature=").append(temperature)
                        .append("&temperature_condition=").append(temperatureCondition);
            }

            if (humidity != null && humidityCondition != null) {
                humidity = humidity.setScale(2, RoundingMode.HALF_UP);
                urlBuilder.append("&humidity=").append(humidity)
                        .append("&humidity_condition=").append(humidityCondition);
            }

            if (noise != null && noiseCondition != null) {
                noise = noise.setScale(2, RoundingMode.HALF_UP);
                urlBuilder.append("&noise_level=").append(noise)
                        .append("&noise_level_condition=").append(noiseCondition);
            }

            if (heartFrequency != null && heartFrequencyCondition != null) {
                urlBuilder.append("&heart_frequency=").append(heartFrequency)
                        .append("&heart_frequency_condition=").append(heartFrequencyCondition);
            }

            // Create a URL object
            URL url = new URL(urlBuilder.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(response.toString());
            SensorDataService.hasPrevious = !jsonResponse.isNull("previous");
            SensorDataService.hasNext = !jsonResponse.isNull("next");

            int itemCount = jsonResponse.getInt("count");
            int pageSize = SensorDataService.pageSize;
            int pageCount = itemCount / pageSize;

            // Check if there are any remaining items on a partial page
            if (itemCount % pageSize != 0) {
                pageCount++;
            }

            SensorDataService.totalPages = pageCount;
            JSONArray sensorDataArray = jsonResponse.getJSONArray("results");
            for (int i = 0; i < sensorDataArray.length(); i++) {
                JSONObject jsonSensorData = sensorDataArray.getJSONObject(i);

                Integer hFrequency = null;
                if (!jsonSensorData.isNull("heart_frequency")) {
                    hFrequency = jsonSensorData.getInt("heart_frequency");
                }
                Date date = DateUtils.dateStringToDate(jsonSensorData.getString("timestamp"));
                sensorData.add(new SensorData(
                        date,
                        BigDecimal.valueOf(jsonSensorData.getDouble("temperature")),
                        BigDecimal.valueOf(jsonSensorData.getDouble("humidity")),
                        BigDecimal.valueOf(jsonSensorData.getDouble("noise_level")),
                        hFrequency
                ));
            }


            if (paginated) {
                try {
                    String sessionQueryJson = "{\"last_report_query\": \"?" + urlBuilder.toString().split("\\?")[1] + "\"}";

                    // Parse the JSON string
                    JSONObject jsonObject = new JSONObject(sessionQueryJson);

                    // Get the "last_report_query" value
                    String lastReportQuery = jsonObject.getString("last_report_query");

                    // Split the query string by '&' to separate parameters
                    String[] queryParams = lastReportQuery.split("&");

                    // Create a StringBuilder to build the modified query string
                    StringBuilder modifiedQuery = new StringBuilder();

                    for (String param : queryParams) {
                        if (!param.startsWith("page_size=")) {
                            if (modifiedQuery.length() > 0) {
                                modifiedQuery.append('&');
                            }
                            modifiedQuery.append(param);
                        }
                    }

                    // Update the "last_report_query" value with the modified query string
                    jsonObject.put("last_report_query", modifiedQuery.toString());

                    // Convert the modified JSON object back to a JSON string
                    String modifiedJsonString = jsonObject.toString();

                    // Save the session query
                    SessionQueriesService.saveSessionQuery(modifiedJsonString, new SessionQueriesService.Callback<String>() {
                        @Override
                        public void onSuccess(String result) {
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensorData;
    }
}

