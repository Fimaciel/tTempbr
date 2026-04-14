package com.exemplo.model;

public record SensorReading(String name, double value, SensorType type) {

    public boolean isAvailable() {
        return value >= 0;
    }

    public String formattedValue() {
        if (!isAvailable()) return "N/A";
        return switch (type) {
            case TEMPERATURE -> String.format("%.1f °C", value);
            case FAN_SPEED -> String.format("%.0f RPM", value);
            case VOLTAGE -> String.format("%.2f V", value);
        };
    }
}
