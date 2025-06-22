package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OnlineMenuPanel extends JPanel {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final GameMain gameMain;
    private final DatabaseManager dbManager;

    public OnlineMenuPanel(JPanel mainPanel, CardLayout cardLayout, GameMain gameMain, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.gameMain = gameMain;
        this.dbManager = dbManager;
        setPreferredSize(new Dimension(450, 600));
        setBackground(Theme.BG_MAIN);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Online Play", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE.deriveFont(60f));
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton hostButton = new JButton("Host a New Game");
        stylePrimaryButton(hostButton);
        hostButton.addActionListener(e -> hostGame());

        JButton joinButton = new JButton("Join a Game");
        stylePrimaryButton(joinButton);
        joinButton.addActionListener(e -> joinGame());

        JButton backButton = new JButton("Back to Main Menu");
        styleSecondaryButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        centerPanel.add(hostButton, gbc);
        centerPanel.add(joinButton, gbc);
        gbc.insets = new Insets(50, 0, 10, 0); // Add some space before back button
        centerPanel.add(backButton, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void hostGame() {
        String username = JOptionPane.showInputDialog(this, "Enter your name:", "Host Game", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String gameId = dbManager.createNewGame(username);
        if (gameId != null) {
            JOptionPane.showMessageDialog(this, "Game created! Your Game ID is: " + gameId + "\nShare this ID with your friend to join.", "Game Hosted", JOptionPane.INFORMATION_MESSAGE);
            // Mulai game untuk host sebagai Player X (Cross)
            gameMain.startNewGame(GameMain.GameMode.ONLINE_MULTIPLAYER, 3, GameMain.GameVariant.STANDARD, gameId, Seed.CROSS, username, "Waiting...");
            cardLayout.show(mainPanel, "GAME");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create game. Please check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void joinGame() {
        String username = JOptionPane.showInputDialog(this, "Enter your name:", "Join Game", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String gameId = JOptionPane.showInputDialog(this, "Enter the Game ID:", "Join Game", JOptionPane.PLAIN_MESSAGE);
        if (gameId == null || gameId.trim().isEmpty()) {
            return;
        }

        if (dbManager.joinGame(gameId, username)) {
            // Mulai game untuk client sebagai Player O (Nought)
            gameMain.startNewGame(GameMain.GameMode.ONLINE_MULTIPLAYER, 3, GameMain.GameVariant.STANDARD, gameId, Seed.NOUGHT, username, "Opponent"); // Opponent name will be fetched later if needed
            cardLayout.show(mainPanel, "GAME");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to join game. Invalid Game ID or the game is already full.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Copy-paste styling methods from MainMenuPanel
    private void stylePrimaryButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.ACCENT_COLOR);
        button.setForeground(Theme.TEXT_DARK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Dimension buttonSize = new Dimension(320, 65);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.ACCENT_COLOR.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.ACCENT_COLOR);
            }
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BG_PANEL);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Dimension buttonSize = new Dimension(320, 65);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
    }
}