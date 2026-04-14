package com.exemplo.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class SensorReadingTest {

    @Test
    void isAvailable_returnsTrue_forPositiveValue() {
        var reading = new SensorReading("CPU", 45.0, SensorType.TEMPERATURE);
        assertTrue(reading.isAvailable());
    }

    @Test
    void isAvailable_returnsTrue_forZeroValue() {
        var reading = new SensorReading("CPU", 0.0, SensorType.TEMPERATURE);
        assertTrue(reading.isAvailable());
    }

    @Test
    void isAvailable_returnsFalse_forNegativeValue() {
        var reading = new SensorReading("CPU", -1.0, SensorType.TEMPERATURE);
        assertFalse(reading.isAvailable());
    }

    @Test
    void formattedValue_returnsNA_whenUnavailable() {
        var reading = new SensorReading("CPU", -1.0, SensorType.TEMPERATURE);
        assertEquals("N/A", reading.formattedValue());
    }

    @Test
    void formattedValue_formatsTemperature() {
        var reading = new SensorReading("CPU", 55.3, SensorType.TEMPERATURE);
        assertTrue(reading.formattedValue().contains("55") && reading.formattedValue().contains("°C"));
    }

    @Test
    void formattedValue_formatsFanSpeed() {
        var reading = new SensorReading("Fan 1", 1200.0, SensorType.FAN_SPEED);
        assertTrue(reading.formattedValue().contains("1200") && reading.formattedValue().contains("RPM"));
    }

    @Test
    void formattedValue_formatsVoltage() {
        var reading = new SensorReading("CPU Voltage", 1.25, SensorType.VOLTAGE);
        assertTrue(reading.formattedValue().contains("1") && reading.formattedValue().contains("V"));
    }

    @ParameterizedTest
    @CsvSource({
            "TEMPERATURE, -1.0, N/A",
            "FAN_SPEED, -1.0, N/A",
            "VOLTAGE, -1.0, N/A"
    })
    void formattedValue_returnsNA_forAllUnavailableTypes(SensorType type, double value, String expected) {
        var reading = new SensorReading("Test", value, type);
        assertEquals(expected, reading.formattedValue());
    }

    @Test
    void formattedValue_handlesZeroTemperature() {
        var reading = new SensorReading("CPU", 0.0, SensorType.TEMPERATURE);
        assertTrue(reading.formattedValue().contains("0") && reading.formattedValue().endsWith("°C"));
    }

    @Test
    void formattedValue_handlesLargeTemperature() {
        var reading = new SensorReading("CPU", 105.7, SensorType.TEMPERATURE);
        assertTrue(reading.formattedValue().contains("105") && reading.formattedValue().contains("°C"));
    }
}
