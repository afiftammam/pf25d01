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

    private JComboBox<String> boardSizeSelector;
    private JComboBox<GameMain.GameVariant> variantSelector;
    private JComboBox<GameMain.Difficulty> difficultySelector;

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
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton pvaButton = new JButton("Play Solo");
        stylePrimaryButton(pvaButton);
        pvaButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_AI));

        JButton pvpButton = new JButton("Play with a friend");
        stylePrimaryButton(pvpButton);
        pvpButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_PLAYER));

        JButton leaderboardButton = new JButton("Leaderboard");
        styleSecondaryButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> cardLayout.show(mainPanel, "LEADERBOARD"));

        JButton settingsButton = new JButton("Settings");
        styleSecondaryButton(settingsButton);
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "SETTINGS"));

        gbc.gridy = 0; buttonPanel.add(pvaButton, gbc);
        gbc.gridy = 1; buttonPanel.add(pvpButton, gbc);
        gbc.gridy = 2; buttonPanel.add(leaderboardButton, gbc);
        gbc.gridy = 3; buttonPanel.add(settingsButton, gbc);

        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        GridBagConstraints gbcOptions = new GridBagConstraints();
        gbcOptions.insets = new Insets(5, 5, 5, 5);
        gbcOptions.fill = GridBagConstraints.HORIZONTAL;
        gbcOptions.weightx = 1.0;

        gbcOptions.gridx = 0; gbcOptions.gridy = 0;
        JLabel sizeLabel = new JLabel("Grid Size:");
        sizeLabel.setFont(Theme.FONT_STATUS);
        sizeLabel.setForeground(Theme.TEXT_LIGHT);
        optionsPanel.add(sizeLabel, gbcOptions);

        gbcOptions.gridx = 1;
        boardSizeSelector = new JComboBox<>(new String[]{"3x3", "5x5", "7x7"});
        boardSizeSelector.setFont(Theme.FONT_STATUS);
        boardSizeSelector.setBackground(Theme.BG_PANEL);
        boardSizeSelector.setForeground(Theme.TEXT_LIGHT);
        optionsPanel.add(boardSizeSelector, gbcOptions);

        gbcOptions.gridx = 0; gbcOptions.gridy = 1;
        JLabel variantLabel = new JLabel("Game Rules:");
        variantLabel.setFont(Theme.FONT_STATUS);
        variantLabel.setForeground(Theme.TEXT_LIGHT);
        optionsPanel.add(variantLabel, gbcOptions);

        gbcOptions.gridx = 1;
        variantSelector = new JComboBox<>(GameMain.GameVariant.values());
        variantSelector.setFont(Theme.FONT_STATUS);
        variantSelector.setBackground(Theme.BG_PANEL);
        variantSelector.setForeground(Theme.TEXT_LIGHT);
        optionsPanel.add(variantSelector, gbcOptions);

        gbcOptions.gridx = 0; gbcOptions.gridy = 2;
        JLabel difficultyLabel = new JLabel("AI Difficulty:");
        difficultyLabel.setFont(Theme.FONT_STATUS);
        difficultyLabel.setForeground(Theme.TEXT_LIGHT);
        optionsPanel.add(difficultyLabel, gbcOptions);

        gbcOptions.gridx = 1;
        difficultySelector = new JComboBox<>(GameMain.Difficulty.values());
        difficultySelector.setFont(Theme.FONT_STATUS);
        difficultySelector.setBackground(Theme.BG_PANEL);
        difficultySelector.setForeground(Theme.TEXT_LIGHT);
        difficultySelector.setSelectedItem(GameMain.Difficulty.HARD);
        optionsPanel.add(difficultySelector, gbcOptions);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 10, 0);
        buttonPanel.add(optionsPanel, gbc);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void startGame(GameMain.GameMode mode) {
        try {
            if (gameMain == null) return;

            String selectedSizeStr = (String) boardSizeSelector.getSelectedItem();
            GameMain.GameVariant selectedVariant = (GameMain.GameVariant) variantSelector.getSelectedItem();
            GameMain.Difficulty selectedDifficulty = (GameMain.Difficulty) difficultySelector.getSelectedItem();

            int size;
            switch (selectedSizeStr) {
                case "5x5": size = 5; break;
                case "7x7": size = 7; break;
                default: size = 3; break;
            }

            gameMain.setDifficulty(selectedDifficulty);
            gameMain.startNewGame(mode, size, selectedVariant);
            cardLayout.show(mainPanel, "GAME");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}