package com.example.ubqprojectclient;

import android.graphics.Color;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Dashboard {
    private String label;

    private String yValueSuffix;
    private ArrayList<Entry> values;
    private ArrayList<String> xLabels;

    public Dashboard(String label, String yValueSuffix) {
        this.label = label;
        this.yValueSuffix = yValueSuffix;
        this.values = new ArrayList<>();
        this.xLabels = new ArrayList<>();
    }

    public void addData(String timestamp, BigDecimal yValue) {
        if (yValue!= null) {
            xLabels.add(timestamp);
            float yValueFloat = yValue.floatValue();
            this.values.add(new Entry(xLabels.size() - 1, yValueFloat));
        }
    }

    public LineData getLineData(int color) {
        LineDataSet dataSet = new LineDataSet(this.values, this.label);
        dataSet.setCircleColor(color);
        dataSet.setColor(color);
        dataSet.setValueTextColor(color);

        dataSet.setDrawValues(false);
        return new LineData(dataSet);
    }

    public String getXLabel(int index) {
        if (index >= 0 && index < xLabels.size()) {
            return xLabels.get(index);
        } else {
            return "";
        }
    }

    public void setData(ArrayList<Entry> data) {
        this.values = data;
    }

    public String getyValueSuffix() {
        return yValueSuffix;
    }
}
