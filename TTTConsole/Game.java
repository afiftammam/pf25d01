package TTTConsole;

import javax.swing.*;
import java.awt.*;

public class Game {
    public static void main(String[] args) {
        // Jalankan di Event Dispatch Thread untuk thread-safety Swing
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tic Tac Toe International");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // CardLayout untuk beralih antar panel (menu dan game)
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Buat instance dari setiap panel
            MainMenuPanel mainMenu = new MainMenuPanel(mainPanel, cardLayout);
            GameMain gameMain = new GameMain(mainPanel, cardLayout); // Diubah dari GamePanel

            // Tambahkan panel ke CardLayout
            mainPanel.add(mainMenu, "MENU");
            mainPanel.add(gameMain, "GAME"); // Diubah dari GamePanel

            // Berikan referensi gameMain ke mainMenu agar bisa memulai game
            mainMenu.setGamePanel(gameMain); // Diubah dari GamePanel

            frame.add(mainPanel);
            frame.pack();
            frame.setLocationRelativeTo(null); // Tampilkan di tengah layar
            frame.setVisible(true);
        });
    }
}