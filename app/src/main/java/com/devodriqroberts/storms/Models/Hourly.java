package com.devodriqroberts.storms.Models;

public class Hourly {
    private long time;
    private String summary;
    private double temperature;
    private String icon;

    public Hourly(long time, String summary, double temperature, String icon) {
        this.time = time;
        this.summary = summary;
        this.temperature = temperature;
        this.icon = icon;
    }

    public long getTime() {
        return time;
    }

    public String getSummary() {
        return summary;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

}
