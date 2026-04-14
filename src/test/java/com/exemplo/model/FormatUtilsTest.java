package com.exemplo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatUtilsTest {

    @Test
    void formatBytes_zero() {
        assertEquals("0 B", FormatUtils.formatBytes(0));
    }

    @Test
    void formatBytes_negative() {
        assertEquals("0 B", FormatUtils.formatBytes(-1));
    }

    @Test
    void formatBytes_bytes() {
        assertEquals("512 B", FormatUtils.formatBytes(512));
    }

    @Test
    void formatBytes_kilobytes() {
        String result = FormatUtils.formatBytes(1024);
        assertTrue(result.contains("1") && result.contains("KB"));
    }

    @Test
    void formatBytes_megabytes() {
        String result = FormatUtils.formatBytes(1024 * 1024);
        assertTrue(result.contains("1") && result.contains("MB"));
    }

    @Test
    void formatBytes_gigabytes() {
        String result = FormatUtils.formatBytes(1024L * 1024 * 1024);
        assertTrue(result.contains("1") && result.contains("GB"));
    }

    @Test
    void formatBytes_terabytes() {
        String result = FormatUtils.formatBytes(1024L * 1024 * 1024 * 1024);
        assertTrue(result.contains("1") && result.contains("TB"));
    }

    @Test
    void formatBytes_realWorldRAM() {
        String result = FormatUtils.formatBytes(17_179_869_184L);
        assertTrue(result.contains("16") && result.contains("GB"));
    }
}
