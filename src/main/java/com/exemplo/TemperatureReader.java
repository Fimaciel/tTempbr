package com.exemplo;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

import java.util.HashMap;
import java.util.Map;

public class TemperatureReader {
    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final Sensors sensors;
    private final CentralProcessor processor;

    public TemperatureReader() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.sensors = hardware.getSensors();
        this.processor = hardware.getProcessor();
    }

    public Map<String, Double> getTemperatures() {
        Map<String, Double> temps = new HashMap<>();

        // CPU Temperature
        double cpuTemp = sensors.getCpuTemperature();
        if (cpuTemp > 0) {
            temps.put("CPU", cpuTemp);
        } else {
            temps.put("CPU", -1.0); // Indica que não está disponível
        }

        // CPU Voltage (informação adicional)
        double cpuVoltage = sensors.getCpuVoltage();
        if (cpuVoltage > 0) {
            temps.put("CPU Voltage", cpuVoltage);
        }

        // Fan Speeds
        int[] fanSpeeds = sensors.getFanSpeeds();
        if (fanSpeeds != null && fanSpeeds.length > 0) {
            for (int i = 0; i < fanSpeeds.length; i++) {
                if (fanSpeeds[i] > 0) {
                    temps.put("Fan " + (i + 1) + " (RPM)", (double) fanSpeeds[i]);
                }
            }
        }

        return temps;
    }

    public String getProcessorInfo() {
        return processor.getProcessorIdentifier().getName();
    }

    public String getOSInfo() {
        return systemInfo.getOperatingSystem().toString();
    }

    public double getCpuLoad() {
        return processor.getSystemCpuLoadBetweenTicks(processor.getSystemCpuLoadTicks()) * 100;
    }
}
