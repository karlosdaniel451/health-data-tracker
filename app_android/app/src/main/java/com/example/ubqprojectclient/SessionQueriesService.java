package com.example.ubqprojectclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionQueriesService {

    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final String baseURL = "http://192.168.0.166:8000/session-queries/";


    public static void saveSessionQuery(final String sessionQueryJson, final Callback<String> callback) {
        executorService.submit(() -> {
            try {
                URL url = new URL(baseURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = sessionQueryJson.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed : HTTP error code : " + responseCode);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    callback.onSuccess(response.toString());
                }

            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void fetchSessionQueries(final Callback<String> callback) {
        executorService.submit(() -> {
            try {
                URL url = new URL(baseURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed : HTTP error code : " + responseCode);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    callback.onSuccess(response.toString());
                }

            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }
}
