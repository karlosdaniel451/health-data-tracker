package com.example.ubqprojectclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static String getNowDateStringInUTC() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return isoFormat.format(new Date());
    }

    public static Date dateStringToDate(String timestampString) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date timestamp = null;
        try {
            timestamp = isoFormat.parse(timestampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static String dateToString(Date timestamp) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getDefault());
        return isoFormat.format(timestamp);
    }

    public static String formatDateString(String dateTime) {
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(dateTime, inputFormatter);

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return date.format(outputFormatter);
        }
        return "";
    }
}
