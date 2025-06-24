// C:\Users\Akhtar\pf25d01\TTTConsole\Game.java
package TTTConsole;


import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


public class Game {
    private static boolean isFullscreen = false;
    private static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Memanggil kelas dari package yang sama
            AssetManager.loadAssets();
            Theme.applyMainTheme();

            // Inisialisasi AudioManager dengan volume awal
            AudioManager.setMasterVolume(0.8f);
            AudioManager.setMusicVolume(0.7f);
            AudioManager.setSfxVolume(1.0f);

            // Pengaturan UIManager
            UIManager.put("Panel.background", Theme.BG_MAIN);
            // ... (sisa kode UIManager.put)

            JFrame frame = new JFrame("Tic Tac Toe International");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);


            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);


            DatabaseManager dbManager = new DatabaseManager();


            // Inisialisasi semua panel
            MainMenuPanel mainMenu = new MainMenuPanel(mainPanel, cardLayout);
            GameMain gameMain = new GameMain(mainPanel, cardLayout, dbManager);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(mainPanel, cardLayout, dbManager);
            SettingsPanel settingsPanel = new SettingsPanel(mainPanel, cardLayout, frame);
            OnlineMenuPanel onlineMenu = new OnlineMenuPanel(mainPanel, cardLayout, gameMain, dbManager);
            // Tambahkan panel audio
            AudioSettingsPanel audioSettingsPanel = new AudioSettingsPanel(mainPanel, cardLayout);


            // Menambahkan panel ke CardLayout
            mainPanel.add(mainMenu, "MENU");
            mainPanel.add(gameMain, "GAME");
            mainPanel.add(leaderboardPanel, "LEADERBOARD");
            mainPanel.add(settingsPanel, "SETTINGS");
            mainPanel.add(onlineMenu, "ONLINE_MENU");
            // Tambahkan panel audio ke layout
            mainPanel.add(audioSettingsPanel, "AUDIO_SETTINGS");

            // Menghubungkan panel
            mainMenu.setGamePanel(gameMain);
            mainMenu.setLeaderboardPanel(leaderboardPanel);

            frame.add(mainPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Listener untuk fullscreen
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_F11 || (e.getKeyCode() == KeyEvent.VK_ENTER && e.isAltDown())) {
                        toggleFullscreen(frame);
                    }
                }
            });
            frame.setFocusable(true);
            frame.requestFocusInWindow();
        });
    }


    private static void toggleFullscreen(JFrame frame) {
        frame.dispose();
        if (!isFullscreen) {
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
        } else {
            device.setFullScreenWindow(null);
            frame.setUndecorated(false);
            frame.setVisible(true);
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        isFullscreen = !isFullscreen;
    }
}

