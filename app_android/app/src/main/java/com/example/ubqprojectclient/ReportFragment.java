package com.example.ubqprojectclient;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ReportFragment extends Fragment {
    private FilterValues filterValues = new FilterValues();
    private final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
    private final SimpleDateFormat FILTER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

    private final String DEFAULT_START_DATETIME_BUTTON_TEXT = "Pick Start DateTime";
    private final String DEFAULT_END_DATETIME_BUTTON_TEXT = "Pick End DateTime";
    private TableLayout tableLayout;
    private Button showStartDateTimePickerButton;
    private Button showEndDateTimePickerButton;
    private Calendar startDateTime = Calendar.getInstance();
    private Calendar endDateTime = Calendar.getInstance();

    private EditText humidityEditText;
    private Spinner humiditySpinner;

    private EditText temperatureEditText;
    private Spinner temperatureSpinner;

    private EditText noiseEditText;
    private Spinner noiseSpinner;

    private EditText heartFrequencyEditText;
    private Spinner heartFrequencySpinner;

    private Button previousPageButton, nextPageButton;

    private TextView pageNumberTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        pageNumberTextView = (TextView) view.findViewById(R.id.pageNumberTextView);

        Button button = view.findViewById(R.id.buttonFilter);
        button.setOnClickListener(v -> {
            try {
                SensorDataService.resetPagination();
                filterData(true);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        nextPageButton = view.findViewById(R.id.nextPageButton);
        previousPageButton = view.findViewById(R.id.previousPageButton);

        // Set click listeners for the next and previous page buttons
        nextPageButton.setOnClickListener(v -> {
            SensorDataService.currentPage++;
            try {
                filterData(false);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        previousPageButton.setOnClickListener(v -> {
            if (SensorDataService.currentPage > 1) {
                SensorDataService.currentPage--;
                try {
                    filterData(false);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Button clearFilterButton = view.findViewById(R.id.clearFilter);
        clearFilterButton.setOnClickListener(v -> {
            try {
                SensorDataService.resetPagination();
                redefineFilters();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        humidityEditText = ((EditText) view.findViewById(R.id.editTextHumidity));
        humiditySpinner = ((Spinner) view.findViewById(R.id.spinnerHumidity));
        temperatureEditText = ((EditText) view.findViewById(R.id.editTextTemperature));
        temperatureSpinner = ((Spinner) view.findViewById(R.id.spinnerTemperature));
        noiseEditText = ((EditText) view.findViewById(R.id.editTextNoise));
        noiseSpinner = ((Spinner) view.findViewById(R.id.spinnerNoise));
        heartFrequencyEditText = ((EditText) view.findViewById(R.id.editTextHeartFrequency));
        heartFrequencySpinner = ((Spinner) view.findViewById(R.id.spinnerHeartFrequency));

        tableLayout = view.findViewById(R.id.tableSensorData);

        showStartDateTimePickerButton = view.findViewById(R.id.showStartDateTimePickerButton);
        showEndDateTimePickerButton = view.findViewById(R.id.showEndDateTimePickerButton);

        showStartDateTimePickerButton.setOnClickListener(v -> showDateTimePickerDialog(startDateTime, showStartDateTimePickerButton));

        showEndDateTimePickerButton.setOnClickListener(v -> showDateTimePickerDialog(endDateTime, showEndDateTimePickerButton));

        try {
            fillFiltersDataWithSessionData();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        try {
            filterData(true);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    private void fillFiltersDataWithSessionData() throws ParseException {
        Bundle bundle = getArguments();
        String lastReportQuery = bundle.getString("last_report_query", "");

        if (!lastReportQuery.equals("")) {
            if (lastReportQuery.startsWith("?")) {
                lastReportQuery = lastReportQuery.substring(1);
            }

            String[] params = lastReportQuery.split("&");
            Map<String, String> queryParams = new HashMap<>();

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }

            String pageValue = queryParams.get("page");
            if (pageValue != null) {
                SensorDataService.currentPage = Integer.parseInt(pageValue);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

            String startTimeValue = queryParams.get("start_date");
            if (startTimeValue != null) {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date date = sdf.parse(startTimeValue);
                startDateTime.setTime(date);
                startDateTime.set(Calendar.SECOND, 0);
                String formattedDateTime = OUTPUT_DATE_FORMAT.format(startDateTime.getTime());
                showStartDateTimePickerButton.setText(formattedDateTime);
            }

            String endTimeValue = queryParams.get("end_date");
            if (endTimeValue != null) {
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                java.util.Date date = sdf.parse(endTimeValue);
                endDateTime.setTime(date);
                endDateTime.set(Calendar.SECOND, 0);
                String formattedDateTime = OUTPUT_DATE_FORMAT.format(endDateTime.getTime());
                showEndDateTimePickerButton.setText(formattedDateTime);
            }

            String temperatureValue = queryParams.get("temperature");
            String temperatureCondition = queryParams.get("temperature_condition");
            if (temperatureValue != null && temperatureCondition != null) {
                temperatureSpinner.setSelection(getSelectionBasedOnCondition(temperatureCondition));
                temperatureEditText.setText(temperatureValue);
            }

            String humidityValue = queryParams.get("humidity");
            String humidityCondition = queryParams.get("humidity_condition");
            if (humidityValue != null && humidityCondition != null) {
                humiditySpinner.setSelection(getSelectionBasedOnCondition(humidityCondition));
                humidityEditText.setText(humidityValue);
            }

            String noiseLevelValue = queryParams.get("noise_level");
            String noiseLevelCondition = queryParams.get("noise_level_condition");
            if (noiseLevelValue != null && noiseLevelCondition != null) {
                noiseSpinner.setSelection(getSelectionBasedOnCondition(noiseLevelCondition));
                noiseEditText.setText(noiseLevelValue);
            }

            String heartFrequencyValue = queryParams.get("heart_frequency");
            String heartFrequencyCondition = queryParams.get("heart_frequency_condition");
            if (heartFrequencyValue != null && heartFrequencyCondition != null) {
                heartFrequencySpinner.setSelection(getSelectionBasedOnCondition(heartFrequencyCondition));
                heartFrequencyEditText.setText(heartFrequencyValue);
            }
        }
    }

    private Integer getSelectionBasedOnCondition(String condition) {
        switch (condition) {
            case "eq":
                return 1;
            case "lt":
                return 2;
            default:
                return 0;
        }
    }

    private void showDateTimePickerDialog(final Calendar dateTime, Button button) {
        int year = dateTime.get(Calendar.YEAR);
        int month = dateTime.get(Calendar.MONTH);
        int day = dateTime.get(Calendar.DAY_OF_MONTH);
        int hour = dateTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateTime.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (DatePickerDialog.OnDateSetListener) (view, year1, monthOfYear, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year1);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute1) -> {
                dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateTime.set(Calendar.MINUTE, minute1);

                updateLabel(button, dateTime);
            }, hour, minute, true);

            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void redefineFilters() throws ParseException {
        showStartDateTimePickerButton.setText(DEFAULT_START_DATETIME_BUTTON_TEXT);
        showEndDateTimePickerButton.setText(DEFAULT_END_DATETIME_BUTTON_TEXT);

        humidityEditText.setText("");
        temperatureEditText.setText("");
        noiseEditText.setText("");
        heartFrequencyEditText.setText("");

        filterData(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateLabel(Button button, Calendar calendar) {
        String formattedDateTime = OUTPUT_DATE_FORMAT.format(calendar.getTime());
        button.setText(formattedDateTime);
    }

    private void removeTableRows() {
        int childCount = tableLayout.getChildCount();
        // Start from the end and go backwards to avoid issues with dynamic changes
        for (int i = childCount - 1; i >= 1; i--) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) {
                tableLayout.removeViewAt(i);
            }
        }
    }

    private FilterValues getFilterData() throws ParseException {
        String humidity = humidityEditText.getText().toString();
        String humidityCondition = mapCondition(humiditySpinner.getSelectedItem().toString());

        String temperature = temperatureEditText.getText().toString();
        String temperatureCondition = mapCondition(temperatureSpinner.getSelectedItem().toString());

        String noise = noiseEditText.getText().toString();
        String noiseCondition = mapCondition(noiseSpinner.getSelectedItem().toString());

        String heartFrequency = heartFrequencyEditText.getText().toString();
        String heartFrequencyCondition = mapCondition(heartFrequencySpinner.getSelectedItem().toString());

        BigDecimal temperatureValue = temperature.isEmpty() ? null : new BigDecimal(temperature);
        BigDecimal humidityValue = humidity.isEmpty() ? null : new BigDecimal(humidity);
        BigDecimal noiseValue = noise.isEmpty() ? null : new BigDecimal(noise);
        Integer heartFrequencyValue = heartFrequency.isEmpty() ? null : Integer.parseInt(heartFrequency);

        String startDateTimeValue = null, endDateTimeValue = null;
        if (!showStartDateTimePickerButton.getText().toString().equals(DEFAULT_START_DATETIME_BUTTON_TEXT)) {
            SimpleDateFormat inputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
            }
            Date date = inputFormat.parse(startDateTime.getTime().toString());
            FILTER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            startDateTimeValue = FILTER_DATE_FORMAT.format(date);
            startDateTimeValue = startDateTimeValue.replaceFirst("\\+0000", "Z");
        }
        if (!showEndDateTimePickerButton.getText().toString().equals(DEFAULT_END_DATETIME_BUTTON_TEXT)) {
            SimpleDateFormat inputFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
            }
            Date date = inputFormat.parse(endDateTime.getTime().toString());
            FILTER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            endDateTimeValue = FILTER_DATE_FORMAT.format(date);
            endDateTimeValue = endDateTimeValue.replaceFirst("\\+0000", "Z");
        }

        return new FilterValues(startDateTimeValue, endDateTimeValue, temperatureValue,
                humidityValue, noiseValue, heartFrequencyValue,
                temperatureCondition, humidityCondition, noiseCondition,
                heartFrequencyCondition);
    }

    private void filterData(boolean readFilters) throws ParseException {
        removeTableRows();

        if (readFilters) {
            filterValues = getFilterData();
        }

        ArrayList<SensorData> sensorDataList = SensorDataService.getSensorData(
                filterValues.getStartDateTime(), filterValues.getEndDateTime(),
                filterValues.getTemperature(), filterValues.getTemperatureCondition(),
                filterValues.getHumidity(), filterValues.getHumidityCondition(),
                filterValues.getNoise(), filterValues.getNoiseCondition(),
                filterValues.getHeartFrequency(), filterValues.getHeartFrequencyCondition(),
                true);

        nextPageButton.setEnabled(SensorDataService.hasNext);
        previousPageButton.setEnabled(SensorDataService.hasPrevious);
        pageNumberTextView.setText("Page " + SensorDataService.currentPage + "/" + SensorDataService.totalPages);

        addSensorDataToTable(sensorDataList);
    }

    private void addSensorDataToTable(ArrayList<SensorData> sensorDataList) throws ParseException {
        for (SensorData data : sensorDataList) {
            String formattedTemperature = String.format(Locale.ENGLISH, "%.2f", data.getTemperature());
            String formattedHumidity = String.format(Locale.ENGLISH, "%.2f", data.getHumidity());
            String formattedNoiseLevel = !(data.getNoiseLevel() == null) ? String.format(Locale.ENGLISH, "%.2f", data.getNoiseLevel()) : "N/A";
            String formattedHeartFrequency = !(data.getHeartFrequency() == null) ? data.getHeartFrequency().toString() : "N/A";

            TableRow row = new TableRow(getActivity());
            Date date = INPUT_DATE_FORMAT.parse(data.getTimestamp());
            row.addView(makeTextView(OUTPUT_DATE_FORMAT.format(date)));
            row.addView(makeTextView(formattedTemperature + "ºC"));
            row.addView(makeTextView(formattedHumidity + "%"));
            row.addView(makeTextView(formattedNoiseLevel + "dB"));

            if (formattedHeartFrequency.equals("N/A")) {
                row.addView(makeTextView(formattedHeartFrequency));
            } else {
                row.addView(makeTextView(formattedHeartFrequency + "bpm"));
            }

            tableLayout.addView(row);
        }
    }

    private String mapCondition(String spinnerSelection) {
        switch (spinnerSelection) {
            case "Greater than":
                return "gt";
            case "Equals":
                return "eq";
            case "Lower than":
                return "lt";
            default:
                return "";
        }
    }

    private TextView makeTextView(String text) {
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

}

