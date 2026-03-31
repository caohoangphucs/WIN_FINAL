package com.example.winfinal.view;

import java.awt.*;

/**
 * Centralized color/font constants for AgriChain app.
 * Use these everywhere to keep a consistent look.
 */
public final class AppTheme {

    private AppTheme() {}

    // ── Sidebar ───────────────────────────────────────────────
    public static final Color SIDEBAR_BG        = new Color(0x1B4332);
    public static final Color SIDEBAR_ITEM_HOVER= new Color(0x2D6A4F);
    public static final Color SIDEBAR_ITEM_ACTIVE= new Color(0x40916C);
    public static final Color SIDEBAR_TEXT      = new Color(0xB7E4C7);
    public static final Color SIDEBAR_TEXT_ACTIVE= Color.WHITE;

    // ── Content area ─────────────────────────────────────────
    public static final Color BG_MAIN           = new Color(0xF0F4F1);
    public static final Color BG_CARD           = Color.WHITE;
    public static final Color BG_TABLE_HEADER   = new Color(0xEEF7F1);
    public static final Color BG_TABLE_ROW_ALT  = new Color(0xF8FBF8);

    // ── Accent / Status ───────────────────────────────────────
    public static final Color PRIMARY           = new Color(0x52B788);
    public static final Color PRIMARY_DARK      = new Color(0x2D6A4F);
    public static final Color DANGER            = new Color(0xE63946);
    public static final Color WARNING           = new Color(0xFFB833);
    public static final Color SUCCESS           = new Color(0x52B788);
    public static final Color INFO              = new Color(0x4895EF);

    // ── Text ─────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY      = new Color(0x1C2B22);
    public static final Color TEXT_SECONDARY    = new Color(0x6B7280);
    public static final Color TEXT_MUTED        = new Color(0x9CA3AF);

    // ── Border ───────────────────────────────────────────────
    public static final Color BORDER            = new Color(0xD1E7DD);
    public static final Color BORDER_LIGHT      = new Color(0xECF5EC);

    // ── Fonts ─────────────────────────────────────────────────
    public static final Font FONT_TITLE         = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE      = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY          = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL         = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SIDEBAR       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SIDEBAR_ACTIVE= new Font("Segoe UI", Font.BOLD, 13);

    // ── Sizes ─────────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH       = 210;
    public static final int CARD_ARC            = 14;
    public static final int BUTTON_HEIGHT       = 34;
    public static final Insets CARD_PADDING     = new Insets(16, 20, 16, 20);
}
