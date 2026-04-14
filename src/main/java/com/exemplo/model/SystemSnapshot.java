package com.exemplo.model;

import java.util.List;

public record SystemSnapshot(
        List<SensorReading> readings,
        double cpuLoadPercent,
        double[] perCoreLoads,
        long memoryTotal,
        long memoryUsed,
        List<DiskUsage> disks,
        long uptimeSeconds,
        String processorName,
        String osInfo,
        int logicalCores,
        int physicalCores
) {

    public double memoryUsedPercent() {
        if (memoryTotal <= 0) return 0;
        return Math.min(100.0, ((double) memoryUsed / memoryTotal) * 100);
    }

    public String formattedUptime() {
        if (uptimeSeconds <= 0) return "N/A";
        long days = uptimeSeconds / 86400;
        long hours = (uptimeSeconds % 86400) / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;

        if (days > 0) return String.format("%dd %dh %dmin", days, hours, minutes);
        if (hours > 0) return String.format("%dh %dmin", hours, minutes);
        return String.format("%dmin", minutes);
    }

    public String formattedCpuLoad() {
        return String.format("%.1f%% de uso", cpuLoadPercent);
    }

    public String formattedMemoryDetail() {
        return String.format("%s usado de %s",
                FormatUtils.formatBytes(memoryUsed),
                FormatUtils.formatBytes(memoryTotal));
    }

    public List<SensorReading> temperatures() {
        return readings.stream()
                .filter(r -> r.type() == SensorType.TEMPERATURE)
                .toList();
    }

    public List<SensorReading> fans() {
        return readings.stream()
                .filter(r -> r.type() == SensorType.FAN_SPEED)
                .toList();
    }

    public List<SensorReading> voltages() {
        return readings.stream()
                .filter(r -> r.type() == SensorType.VOLTAGE)
                .toList();
    }

    public boolean hasAvailableTemperatures() {
        return temperatures().stream().anyMatch(SensorReading::isAvailable);
    }
}
