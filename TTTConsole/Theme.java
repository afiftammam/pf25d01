package TTTConsole;

import javax.swing.*;
import java.awt.*;

public class Theme {

    // Palet Warna Cyber (Dark)
    private static final Color CYBER_BG_MAIN = new Color(13, 29, 39);
    private static final Color CYBER_BG_PANEL = new Color(30, 52, 69);
    private static final Color CYBER_ACCENT = new Color(51, 224, 206);
    private static final Color CYBER_TEXT_LIGHT = Color.WHITE;
    private static final Color CYBER_TEXT_DARK = new Color(13, 29, 39);

    // Palet Warna Mint (Light)
    private static final Color MINT_BG_MAIN = new Color(236, 247, 245);
    private static final Color MINT_BG_PANEL = new Color(255, 255, 255);
    private static final Color MINT_ACCENT = new Color(38, 166, 154);
    private static final Color MINT_TEXT_LIGHT = Color.WHITE;
    private static final Color MINT_TEXT_DARK = new Color(20, 50, 50);

    // Warna Simbol Pemain
    public static final Color CROSS_COLOR = new Color(175, 225, 126);
    public static final Color NOUGHT_COLOR = new Color(242, 169, 114);

    // Warna UI Lainnya
    public static final Color WIN_LINE_COLOR = new Color(255, 255, 255, 220);
    public static final Color HOVER_COLOR = new Color(255, 255, 255, 30);


    // --- Variabel Warna Dinamis yang Digunakan di Seluruh Aplikasi ---
    public static Color bgMain = CYBER_BG_MAIN;
    public static Color bgPanel = CYBER_BG_PANEL;
    public static Color accentColor = CYBER_ACCENT;
    public static Color textLight = CYBER_TEXT_LIGHT;
    public static Color textDark = CYBER_TEXT_DARK;

    // Font (tetap sama)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 72);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_STATUS = new Font("Segoe UI", Font.BOLD, 18);

    public static void applyCyberTheme() {
        bgMain = CYBER_BG_MAIN;
        bgPanel = CYBER_BG_PANEL;
        accentColor = CYBER_ACCENT;
        textLight = CYBER_TEXT_LIGHT;
        textDark = CYBER_TEXT_DARK;
    }

    public static void applyMintTheme() {
        bgMain = MINT_BG_MAIN;
        bgPanel = MINT_BG_PANEL;
        accentColor = MINT_ACCENT;
        textLight = MINT_TEXT_LIGHT;
        textDark = MINT_TEXT_DARK;
    }

    public static void updateUI(JFrame frame) {
        // Ini akan memaksa seluruh UI untuk menggambar ulang dirinya sendiri
        SwingUtilities.updateComponentTreeUI(frame);
    }
}