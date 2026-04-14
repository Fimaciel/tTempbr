package com.exemplo.ui;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThemeTest {

    @Test
    void temperatureColor_normal_belowThreshold() {
        assertEquals(Theme.TEMP_NORMAL, Theme.temperatureColor(49.9));
    }

    @Test
    void temperatureColor_elevated_atThreshold() {
        assertEquals(Theme.TEMP_ELEVATED, Theme.temperatureColor(50.0));
    }

    @Test
    void temperatureColor_elevated_belowCritical() {
        assertEquals(Theme.TEMP_ELEVATED, Theme.temperatureColor(69.9));
    }

    @Test
    void temperatureColor_critical_atThreshold() {
        assertEquals(Theme.TEMP_CRITICAL, Theme.temperatureColor(70.0));
    }

    @Test
    void temperatureColor_critical_above() {
        assertEquals(Theme.TEMP_CRITICAL, Theme.temperatureColor(100.0));
    }

    @Test
    void temperatureColor_normal_atZero() {
        assertEquals(Theme.TEMP_NORMAL, Theme.temperatureColor(0.0));
    }

    @Test
    void progressColor_good_belowThreshold() {
        assertEquals(Theme.PROGRESS_GOOD, Theme.progressColor(59.9));
    }

    @Test
    void progressColor_warn_atThreshold() {
        assertEquals(Theme.PROGRESS_WARN, Theme.progressColor(60.0));
    }

    @Test
    void progressColor_warn_belowDanger() {
        assertEquals(Theme.PROGRESS_WARN, Theme.progressColor(84.9));
    }

    @Test
    void progressColor_danger_atThreshold() {
        assertEquals(Theme.PROGRESS_DANGER, Theme.progressColor(85.0));
    }

    @Test
    void progressColor_danger_at100() {
        assertEquals(Theme.PROGRESS_DANGER, Theme.progressColor(100.0));
    }
}
