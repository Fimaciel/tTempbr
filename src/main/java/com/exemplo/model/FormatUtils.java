package com.exemplo.model;

public final class FormatUtils {

    private FormatUtils() {}

    public static String formatBytes(long bytes) {
        if (bytes < 0) return "0 B";
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format("%.1f MB", mb);
        double gb = mb / 1024.0;
        if (gb < 1024) return String.format("%.1f GB", gb);
        double tb = gb / 1024.0;
        return String.format("%.2f TB", tb);
    }
}
