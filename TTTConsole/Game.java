package TTTConsole;

import javax.swing.*;
import java.awt.*;

/**
 * Kelas utama yang menjalankan seluruh permainan.
 * Menginisialisasi frame, panel utama dengan CardLayout, dan semua komponen lainnya.
 */
public class Game {
    public static void main(String[] args) {
        // Jalankan di Event Dispatch Thread untuk thread-safety Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tic Tac Toe International");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // CardLayout untuk beralih antar panel (menu, game, dan leaderboard)
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Inisialisasi DatabaseManager
            DatabaseManager dbManager = new DatabaseManager();

            // Buat instance dari setiap panel
            MainMenuPanel mainMenu = new MainMenuPanel(mainPanel, cardLayout);
            GameMain gameMain = new GameMain(mainPanel, cardLayout, dbManager);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(mainPanel, cardLayout, dbManager);

            // Tambahkan panel ke CardLayout dengan nama yang unik
            mainPanel.add(mainMenu, "MENU");
            mainPanel.add(gameMain, "GAME");
            mainPanel.add(leaderboardPanel, "LEADERBOARD");

            // Berikan referensi panel ke mainMenu agar bisa saling berinteraksi
            mainMenu.setGamePanel(gameMain);
            mainMenu.setLeaderboardPanel(leaderboardPanel);

            frame.add(mainPanel);
            frame.pack();
            frame.setLocationRelativeTo(null); // Tampilkan di tengah layar
            frame.setVisible(true);
        });
    }
}