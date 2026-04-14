package com.exemplo.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemSnapshotTest {

    private SystemSnapshot createSnapshot(List<SensorReading> readings,
                                          long memTotal, long memUsed,
                                          long uptime) {
        return new SystemSnapshot(
                readings, 50.0, new double[]{50.0}, memTotal, memUsed,
                List.of(), uptime, "Test CPU", "Linux", 4, 2
        );
    }

    // --- memoryUsedPercent ---

    @Test
    void memoryUsedPercent_calculatesCorrectly() {
        var snap = createSnapshot(List.of(), 16_000_000_000L, 8_000_000_000L, 3600);
        assertEquals(50.0, snap.memoryUsedPercent(), 0.01);
    }

    @Test
    void memoryUsedPercent_returnsZero_whenTotalIsZero() {
        var snap = createSnapshot(List.of(), 0, 0, 3600);
        assertEquals(0.0, snap.memoryUsedPercent());
    }

    @Test
    void memoryUsedPercent_cappedAt100() {
        var snap = createSnapshot(List.of(), 100, 200, 3600);
        assertEquals(100.0, snap.memoryUsedPercent(), 0.01);
    }

    // --- formattedUptime ---

    @Test
    void formattedUptime_showsMinutes() {
        var snap = createSnapshot(List.of(), 0, 0, 300);
        assertEquals("5min", snap.formattedUptime());
    }

    @Test
    void formattedUptime_showsHoursAndMinutes() {
        var snap = createSnapshot(List.of(), 0, 0, 3661);
        assertEquals("1h 1min", snap.formattedUptime());
    }

    @Test
    void formattedUptime_showsDaysHoursMinutes() {
        var snap = createSnapshot(List.of(), 0, 0, 90061);
        assertEquals("1d 1h 1min", snap.formattedUptime());
    }

    @Test
    void formattedUptime_returnsNA_forZero() {
        var snap = createSnapshot(List.of(), 0, 0, 0);
        assertEquals("N/A", snap.formattedUptime());
    }

    @Test
    void formattedUptime_returnsNA_forNegative() {
        var snap = createSnapshot(List.of(), 0, 0, -1);
        assertEquals("N/A", snap.formattedUptime());
    }

    // --- formattedCpuLoad ---

    @Test
    void formattedCpuLoad_containsPercentage() {
        var snap = new SystemSnapshot(
                List.of(), 75.5, new double[]{}, 0, 0,
                List.of(), 0, "", "", 0, 0
        );
        String result = snap.formattedCpuLoad();
        assertTrue(result.contains("75") && result.contains("de uso"));
    }

    // --- formattedMemoryDetail ---

    @Test
    void formattedMemoryDetail_containsUsedAndTotal() {
        var snap = createSnapshot(List.of(), 17_179_869_184L, 8_589_934_592L, 0);
        String detail = snap.formattedMemoryDetail();
        assertTrue(detail.contains("GB") && detail.contains("usado"));
    }

    // --- filtering methods ---

    @Test
    void temperatures_filtersCorrectly() {
        var readings = List.of(
                new SensorReading("CPU", 55.0, SensorType.TEMPERATURE),
                new SensorReading("Fan 1", 1200.0, SensorType.FAN_SPEED),
                new SensorReading("Voltage", 1.2, SensorType.VOLTAGE)
        );
        var snap = createSnapshot(readings, 0, 0, 0);

        assertEquals(1, snap.temperatures().size());
        assertEquals("CPU", snap.temperatures().getFirst().name());
    }

    @Test
    void fans_filtersCorrectly() {
        var readings = List.of(
                new SensorReading("CPU", 55.0, SensorType.TEMPERATURE),
                new SensorReading("Fan 1", 1200.0, SensorType.FAN_SPEED)
        );
        var snap = createSnapshot(readings, 0, 0, 0);

        assertEquals(1, snap.fans().size());
        assertEquals("Fan 1", snap.fans().getFirst().name());
    }

    @Test
    void voltages_filtersCorrectly() {
        var readings = List.of(
                new SensorReading("CPU Voltage", 1.2, SensorType.VOLTAGE)
        );
        var snap = createSnapshot(readings, 0, 0, 0);

        assertEquals(1, snap.voltages().size());
    }

    @Test
    void hasAvailableTemperatures_returnsFalse_whenAllUnavailable() {
        var readings = List.of(
                new SensorReading("CPU", -1.0, SensorType.TEMPERATURE)
        );
        var snap = createSnapshot(readings, 0, 0, 0);

        assertFalse(snap.hasAvailableTemperatures());
    }

    @Test
    void hasAvailableTemperatures_returnsTrue_whenOneAvailable() {
        var readings = List.of(
                new SensorReading("CPU", 55.0, SensorType.TEMPERATURE),
                new SensorReading("GPU", -1.0, SensorType.TEMPERATURE)
        );
        var snap = createSnapshot(readings, 0, 0, 0);

        assertTrue(snap.hasAvailableTemperatures());
    }
}
