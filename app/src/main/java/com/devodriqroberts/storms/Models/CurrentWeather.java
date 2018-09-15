package com.devodriqroberts.storms.Models;

public class CurrentWeather {
    private String locationLabel;
    private String icon;
    private long time;
    private double temperature;
    private double humidity;
    private double precipProbability;
    private String summary;

    public CurrentWeather(String locationLabel, String icon, long time, double temperature, double humidity, double precipProbability, String summary) {
        this.locationLabel = locationLabel;
        this.icon = icon;
        this.time = time;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipProbability = precipProbability;
        this.summary = summary;
    }

    public String getLocationLabel() {
        return locationLabel;
    }

    public String getIcon() {
        return icon;
    }

    public long getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public String getSummary() {
        return summary;
    }
}
