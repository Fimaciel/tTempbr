package com.exemplo.model;

public record DiskUsage(String name, String mountPoint, long totalBytes, long usableBytes) {

    public long usedBytes() {
        long used = totalBytes - usableBytes;
        return Math.max(0, used);
    }

    public double usedPercent() {
        if (totalBytes <= 0) return 0;
        return Math.min(100.0, ((double) usedBytes() / totalBytes) * 100);
    }
}
