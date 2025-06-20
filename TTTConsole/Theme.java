package TTTConsole;

import javax.swing.*;
import java.awt.*;

public class Theme {

    public static Color BG_MAIN = new Color(13, 29, 39);
    public static Color BG_PANEL = new Color(30, 52, 69);
    public static Color ACCENT_COLOR = new Color(51, 224, 206);
    public static Color TEXT_LIGHT = new Color(255, 255, 255);
    public static Color TEXT_DARK = new Color(13, 29, 39);
    public static Color CROSS = new Color(175, 225, 126);
    public static Color NOUGHT = new Color(242, 169, 114);
    public static Color WIN_LINE = new Color(255, 255, 255, 220);
    public static Color HOVER = new Color(255, 255, 255, 30);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 72);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_STATUS = new Font("Segoe UI", Font.BOLD, 18);

    public static void applyCyberTheme() {
        BG_MAIN = new Color(13, 29, 39);
        BG_PANEL = new Color(30, 52, 69);
        ACCENT_COLOR = new Color(51, 224, 206);
        TEXT_LIGHT = new Color(255, 255, 255);
        TEXT_DARK = new Color(13, 29, 39);
        CROSS = new Color(175, 225, 126);
        NOUGHT = new Color(242, 169, 114);
        WIN_LINE = new Color(255, 255, 255, 220);
        HOVER = new Color(255, 255, 255, 30);
    }

    public static void applyMintTheme() {
        BG_MAIN = new Color(240, 255, 240);
        BG_PANEL = new Color(204, 235, 221);
        ACCENT_COLOR = new Color(102, 204, 153);
        TEXT_LIGHT = new Color(10, 38, 10);
        TEXT_DARK = new Color(255, 255, 255);
        CROSS = new Color(255, 105, 97);
        NOUGHT = new Color(77, 121, 255);
        WIN_LINE = new Color(0, 0, 0, 200);
        HOVER = new Color(0, 0, 0, 20);
    }

    public static void updateUI(JFrame frame) {
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
    }
}