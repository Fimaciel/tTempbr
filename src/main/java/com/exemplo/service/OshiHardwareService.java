package com.exemplo.service;

import com.exemplo.model.DiskUsage;
import com.exemplo.model.SensorReading;
import com.exemplo.model.SensorType;
import com.exemplo.model.SystemSnapshot;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.ArrayList;
import java.util.List;

public class OshiHardwareService implements HardwareService {

    private static final int MAX_STRING_LENGTH = 200;

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final Sensors sensors;
    private final CentralProcessor processor;
    private final GlobalMemory memory;

    private volatile long[] prevSystemTicks;
    private volatile long[][] prevCoreTicks;

    public OshiHardwareService() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.sensors = hardware.getSensors();
        this.processor = hardware.getProcessor();
        this.memory = hardware.getMemory();

        this.prevSystemTicks = processor.getSystemCpuLoadTicks();
        this.prevCoreTicks = processor.getProcessorCpuLoadTicks();
    }

    @Override
    public synchronized SystemSnapshot snapshot() {
        List<SensorReading> readings = collectSensorReadings();
        double cpuLoad = collectCpuLoad();
        double[] perCoreLoads = collectPerCoreLoads();
        long memTotal = memory.getTotal();
        long memUsed = memTotal - memory.getAvailable();
        List<DiskUsage> disks = collectDiskUsage();
        long uptime = systemInfo.getOperatingSystem().getSystemUptime();

        return new SystemSnapshot(
                readings,
                clampPercent(cpuLoad),
                perCoreLoads,
                Math.max(0, memTotal),
                clamp(memUsed, 0, memTotal),
                disks,
                Math.max(0, uptime),
                sanitize(getProcessorInfo()),
                sanitize(getOSInfo()),
                processor.getLogicalProcessorCount(),
                processor.getPhysicalProcessorCount()
        );
    }

    private List<SensorReading> collectSensorReadings() {
        List<SensorReading> readings = new ArrayList<>();

        double cpuTemp = sanitizeDouble(sensors.getCpuTemperature());
        readings.add(new SensorReading(
                "CPU",
                cpuTemp > 0 ? cpuTemp : -1.0,
                SensorType.TEMPERATURE
        ));

        double cpuVoltage = sanitizeDouble(sensors.getCpuVoltage());
        if (cpuVoltage > 0) {
            readings.add(new SensorReading("CPU Voltage", cpuVoltage, SensorType.VOLTAGE));
        }

        int[] fanSpeeds = sensors.getFanSpeeds();
        if (fanSpeeds != null) {
            for (int i = 0; i < fanSpeeds.length; i++) {
                if (fanSpeeds[i] > 0) {
                    readings.add(new SensorReading(
                            "Fan " + (i + 1),
                            fanSpeeds[i],
                            SensorType.FAN_SPEED
                    ));
                }
            }
        }

        return readings;
    }

    private double collectCpuLoad() {
        long[] prev = prevSystemTicks;
        double load = processor.getSystemCpuLoadBetweenTicks(prev) * 100;
        prevSystemTicks = processor.getSystemCpuLoadTicks();
        return sanitizeDouble(load);
    }

    private double[] collectPerCoreLoads() {
        long[][] prev = prevCoreTicks;
        double[] loads = processor.getProcessorCpuLoadBetweenTicks(prev);
        prevCoreTicks = processor.getProcessorCpuLoadTicks();

        double[] percentages = new double[loads.length];
        for (int i = 0; i < loads.length; i++) {
            percentages[i] = clampPercent(sanitizeDouble(loads[i] * 100));
        }
        return percentages;
    }

    private List<DiskUsage> collectDiskUsage() {
        List<DiskUsage> disks = new ArrayList<>();
        try {
            FileSystem fs = systemInfo.getOperatingSystem().getFileSystem();
            for (OSFileStore store : fs.getFileStores()) {
                long total = store.getTotalSpace();
                if (total <= 0) continue;

                long usable = Math.max(0, Math.min(store.getUsableSpace(), total));
                disks.add(new DiskUsage(
                        sanitize(store.getName()),
                        sanitize(store.getMount()),
                        total,
                        usable
                ));
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler discos: " + e.getMessage());
        }
        return disks;
    }

    @Override
    public String getProcessorInfo() {
        return processor.getProcessorIdentifier().getName();
    }

    @Override
    public String getOSInfo() {
        return systemInfo.getOperatingSystem().toString();
    }

    private static String sanitize(String input) {
        if (input == null) return "N/A";
        if (input.length() > MAX_STRING_LENGTH) {
            return input.substring(0, MAX_STRING_LENGTH) + "...";
        }
        return input;
    }

    private static double sanitizeDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return -1.0;
        return value;
    }

    private static double clampPercent(double value) {
        return clamp(value, 0.0, 100.0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }
}
