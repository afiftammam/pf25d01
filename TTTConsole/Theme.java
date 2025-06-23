// afiftammam/pf25d01/pf25d01-d5e914db64e716630e5da884f8aadbbd72a6a70b/TTTConsole/Theme.java
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


    public static void applyMainTheme() {
        BG_MAIN = new Color(13, 29, 39);
        BG_PANEL = new Color(30, 52, 69);
        ACCENT_COLOR = new Color(51, 224, 206);
        TEXT_LIGHT = new Color(255, 255, 255);
        TEXT_DARK = new Color(13, 29, 39);
        CROSS = new Color(175, 225, 126);
        NOUGHT = new Color(242, 169, 114);
        WIN_LINE = new Color(255, 255, 255, 220);
        HOVER = new Color(255, 255, 255, 30);


        // Muat aset untuk Main Theme
        AssetManager.loadImage("BACKGROUND", "TTTConsole/images/backgroundThemeMain.png");
        AssetManager.loadImage("MAIN_MENU_BG", "TTTConsole/images/mainMenuThemeMain.png");
        AssetManager.loadImage("CROSS", "TTTConsole/images/exThemeMain.png");
        AssetManager.loadImage("NOUGHT", "TTTConsole/images/bulatThemeMain.png");
    }


    public static void applyCyberTheme() {
        // Tema ini sebelumnya adalah "Space Theme"
        BG_MAIN = new Color(10, 15, 30);
        BG_PANEL = new Color(25, 40, 65);
        ACCENT_COLOR = new Color(190, 70, 255);
        TEXT_LIGHT = new Color(220, 220, 240);
        TEXT_DARK = new Color(10, 15, 30);
        CROSS = new Color(180, 255, 50);
        NOUGHT = new Color(255, 160, 50);
        WIN_LINE = new Color(255, 255, 255, 220);
        HOVER = new Color(190, 70, 255, 40);


        // Muat aset untuk Cyber Theme
        AssetManager.loadImage("BACKGROUND", "TTTConsole/images/backgroundThemeCyber.png");
        AssetManager.loadImage("MAIN_MENU_BG", "TTTConsole/images/mainMenuThemeCyber.png");
        AssetManager.loadImage("CROSS", "TTTConsole/images/exThemeCyber.png");
        AssetManager.loadImage("NOUGHT", "TTTConsole/images/bulatThemeCyber.png");
    }


    public static void updateUI(JFrame frame) {
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
    }
}

