package TTTConsole;

import javax.swing.*;
import java.awt.*;

public class Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // UIManager settings bisa dihapus jika kita mengontrol semua warna secara manual
            // ...

            JFrame frame = new JFrame("Tic Tac Toe International");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout) {
                // Override paintComponent untuk menggambar background sesuai tema
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Theme.bgMain);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            DatabaseManager dbManager = new DatabaseManager();

            // Buat semua panel
            MainMenuPanel mainMenu = new MainMenuPanel(mainPanel, cardLayout);
            GameMain gameMain = new GameMain(mainPanel, cardLayout, dbManager);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(mainPanel, cardLayout, dbManager);
            // PERUBAHAN: Berikan referensi 'frame' ke SettingsPanel
            SettingsPanel settingsPanel = new SettingsPanel(mainPanel, cardLayout, frame);

            // Tambahkan semua panel ke CardLayout
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