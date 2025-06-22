package TTTConsole;

import javax.swing.*;
import java.awt.*;

public class Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // PERUBAHAN: Inisialisasi semua aset (gambar & suara) di awal.
            AssetManager.loadAssets();
            AudioManager.setVolume(AudioManager.Volume.LOW); // Atur volume default

            UIManager.put("Panel.background", Theme.BG_MAIN);
            UIManager.put("OptionPane.background", Theme.BG_MAIN);
            UIManager.put("OptionPane.messageForeground", Theme.TEXT_LIGHT);
            UIManager.put("Button.background", Theme.BG_PANEL);
            UIManager.put("Button.foreground", Theme.TEXT_LIGHT);
            UIManager.put("Button.select", Theme.BG_PANEL.brighter());
            UIManager.put("Button.focus", new Color(0,0,0,0));
            UIManager.put("Button.border", BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 1));
            UIManager.put("TextField.background", Theme.BG_PANEL);
            UIManager.put("TextField.foreground", Theme.TEXT_LIGHT);
            UIManager.put("TextField.caretForeground", Theme.TEXT_LIGHT);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            JFrame frame = new JFrame("Tic Tac Toe International");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            DatabaseManager dbManager = new DatabaseManager();

            MainMenuPanel mainMenu = new MainMenuPanel(mainPanel, cardLayout);
            GameMain gameMain = new GameMain(mainPanel, cardLayout, dbManager);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(mainPanel, cardLayout, dbManager);
            SettingsPanel settingsPanel = new SettingsPanel(mainPanel, cardLayout, frame);

            mainPanel.add(mainMenu, "MENU");
            mainPanel.add(gameMain, "GAME");
            mainPanel.add(leaderboardPanel, "LEADERBOARD");
            mainPanel.add(settingsPanel, "SETTINGS");

            mainMenu.setGamePanel(gameMain);
            mainMenu.setLeaderboardPanel(leaderboardPanel);

            frame.add(mainPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}