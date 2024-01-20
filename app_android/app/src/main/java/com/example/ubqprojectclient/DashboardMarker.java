package com.example.ubqprojectclient;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class DashboardMarker extends MarkerView {
    private TextView tvContent;
    private String dashboardTitle;

    public DashboardMarker(Context context, String dashboardTitle, int layoutResource) {
        super(context, layoutResource);
        this.dashboardTitle = dashboardTitle;
        tvContent = findViewById(R.id.tvContent);
    }

    // Called every time the MarkerView is redrawn, can be used to update the content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        String xLabel = "", yValueSuffix = "";
        switch (this.dashboardTitle) {
            case "Temperature":
                xLabel = HomeFragment.temperatureDashboard.getXLabel((int) e.getX());
                yValueSuffix = HomeFragment.temperatureDashboard.getyValueSuffix();
                break;
            case "Humidity":
                xLabel = HomeFragment.humidityDashboard.getXLabel((int) e.getX());
                yValueSuffix = HomeFragment.humidityDashboard.getyValueSuffix();
                break;
            case "Noise Level":
                xLabel = HomeFragment.noiseLevelDashboard.getXLabel((int) e.getX());
                yValueSuffix = HomeFragment.noiseLevelDashboard.getyValueSuffix();
                break;
            case "Heart Frequency":
                xLabel = HomeFragment.heartFrequencyDashboard.getXLabel((int) e.getX());
                yValueSuffix = HomeFragment.heartFrequencyDashboard.getyValueSuffix();
                break;
        }

        tvContent.setText("X: " + DateUtils.formatDateString(xLabel) + ", Y: " + e.getY() + yValueSuffix);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        // This will center the MarkerView on top of the dot
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
