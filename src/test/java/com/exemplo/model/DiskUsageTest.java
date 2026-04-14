package com.exemplo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiskUsageTest {

    @Test
    void usedBytes_calculatesCorrectly() {
        var disk = new DiskUsage("sda1", "/", 1000, 400);
        assertEquals(600, disk.usedBytes());
    }

    @Test
    void usedBytes_returnsZero_whenUsableExceedsTotal() {
        var disk = new DiskUsage("sda1", "/", 1000, 1200);
        assertEquals(0, disk.usedBytes());
    }

    @Test
    void usedPercent_calculatesCorrectly() {
        var disk = new DiskUsage("sda1", "/", 1000, 400);
        assertEquals(60.0, disk.usedPercent(), 0.01);
    }

    @Test
    void usedPercent_returnsZero_whenTotalIsZero() {
        var disk = new DiskUsage("sda1", "/", 0, 0);
        assertEquals(0.0, disk.usedPercent());
    }

    @Test
    void usedPercent_cappedAt100() {
        var disk = new DiskUsage("sda1", "/", 100, 0);
        assertEquals(100.0, disk.usedPercent(), 0.01);
    }

    @Test
    void usedPercent_returnsZero_whenNegativeTotal() {
        var disk = new DiskUsage("sda1", "/", -1, 0);
        assertEquals(0.0, disk.usedPercent());
    }
}
