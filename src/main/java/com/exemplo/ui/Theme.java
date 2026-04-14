package com.exemplo.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centraliza todas as constantes visuais da aplicação.
 * Usa "SansSerif" (logical font Java) para compatibilidade cross-platform (Windows + Linux).
 */
public final class Theme {

    private Theme() {}

    // Logical font — maps to Arial (Windows), DejaVu Sans / Noto Sans (Linux)
    private static final String FONT_FAMILY = "SansSerif";

    // --- Colors ---
    public static final Color BACKGROUND = Color.web("#1e1e2e");
    public static final Color SURFACE = Color.web("#2b2b3d");
    public static final Color CARD_BACKGROUND = Color.web("#363649");
    public static final Color TEXT_PRIMARY = Color.web("#ffffff");
    public static final Color TEXT_SECONDARY = Color.web("#b0b0c0");
    public static final Color TEXT_FOOTER = Color.web("#707080");
    public static final Color SECTION_TITLE = Color.web("#a0a0d0");
    public static final Color DIVIDER = Color.web("#404055");

    public static final Color TEMP_NORMAL = Color.web("#50fa7b");
    public static final Color TEMP_ELEVATED = Color.web("#ffb86c");
    public static final Color TEMP_CRITICAL = Color.web("#ff5555");
    public static final Color INFO_VALUE = Color.web("#8be9fd");
    public static final Color UNAVAILABLE = Color.web("#6272a4");

    public static final Color PROGRESS_BG = Color.web("#44475a");
    public static final Color PROGRESS_GOOD = Color.web("#50fa7b");
    public static final Color PROGRESS_WARN = Color.web("#ffb86c");
    public static final Color PROGRESS_DANGER = Color.web("#ff5555");

    public static final Color ACCENT = Color.web("#bd93f9");

    // --- CSS Styles ---
    public static final String BACKGROUND_STYLE = "-fx-background-color: #1e1e2e;";
    public static final String SURFACE_STYLE = "-fx-background-color: #2b2b3d; -fx-background-radius: 8;";
    public static final String CARD_STYLE = "-fx-background-color: #363649; -fx-background-radius: 6;";
    public static final String SCROLL_STYLE = "-fx-background: #1e1e2e; -fx-background-color: #1e1e2e;";
    public static final String SECTION_STYLE = "-fx-background-color: #2b2b3d; -fx-background-radius: 10; -fx-padding: 15;";
    public static final String DIVIDER_STYLE = "-fx-background-color: #404055; -fx-pref-height: 1;";
    public static final String PROGRESS_TRACK_STYLE = "-fx-background-color: #44475a; -fx-background-radius: 4;";

    // --- Fonts ---
    public static final Font TITLE = Font.font(FONT_FAMILY, FontWeight.BOLD, 22);
    public static final Font SECTION_HEADER = Font.font(FONT_FAMILY, FontWeight.BOLD, 15);
    public static final Font HEADING = Font.font(FONT_FAMILY, FontWeight.BOLD, 16);
    public static final Font BODY = Font.font(FONT_FAMILY, 12);
    public static final Font BODY_BOLD = Font.font(FONT_FAMILY, FontWeight.BOLD, 13);
    public static final Font CAPTION = Font.font(FONT_FAMILY, 10);
    public static final Font MONO = Font.font("Monospaced", 12);

    // --- Temperature Thresholds ---
    public static final double TEMP_THRESHOLD_ELEVATED = 50.0;
    public static final double TEMP_THRESHOLD_CRITICAL = 70.0;

    public static Color temperatureColor(double celsius) {
        if (celsius < TEMP_THRESHOLD_ELEVATED) return TEMP_NORMAL;
        if (celsius < TEMP_THRESHOLD_CRITICAL) return TEMP_ELEVATED;
        return TEMP_CRITICAL;
    }

    public static Color progressColor(double percent) {
        if (percent < 60) return PROGRESS_GOOD;
        if (percent < 85) return PROGRESS_WARN;
        return PROGRESS_DANGER;
    }
}
