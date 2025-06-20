package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private GameMain gameMain;
    private LeaderboardPanel leaderboardPanel;
    private JComboBox<String> difficultySelector;
    private JComboBox<String> boardSizeSelector;

    public MainMenuPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        setPreferredSize(new Dimension(450, 600));
        setBackground(Theme.BG_MAIN);
        initUI();
    }

    public void setGamePanel(GameMain gameMain) { this.gameMain = gameMain; }
    public void setLeaderboardPanel(LeaderboardPanel leaderboardPanel) { this.leaderboardPanel = leaderboardPanel; }

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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL);
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Tic-Tac-Toe");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        boardSizeSelector = new JComboBox<>(new String[]{"3x3", "4x4"});
        boardSizeSelector.setFont(Theme.FONT_STATUS);

        JButton pvaButton = new JButton("Play Solo");
        stylePrimaryButton(pvaButton);
        pvaButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_AI));

        JButton pvpButton = new JButton("Play with a friend");
        stylePrimaryButton(pvpButton);
        pvpButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_PLAYER));

        JButton leaderboardButton = new JButton("Leaderboard");
        styleSecondaryButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> {
            if (leaderboardPanel != null) {
                leaderboardPanel.refreshLeaderboard();
                cardLayout.show(mainPanel, "LEADERBOARD");
            }
        });

        gbc.gridy = 0;
        buttonPanel.add(pvaButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(pvpButton, gbc);

        gbc.gridy = 2;
        buttonPanel.add(leaderboardButton, gbc);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void startGame(GameMain.GameMode mode) {
        String selectedSize = (String) boardSizeSelector.getSelectedItem();
        int size = "3x3".equals(selectedSize) ? 3 : 4;

        GameMain.Difficulty diff = GameMain.Difficulty.HARD;

        gameMain.setDifficulty(diff);
        gameMain.startNewGame(mode, size);
        cardLayout.show(mainPanel, "GAME");
    }
}