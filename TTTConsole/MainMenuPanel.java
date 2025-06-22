package TTTConsole;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
    private JLabel difficultyLabel;

    public MainMenuPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        setPreferredSize(new Dimension(450, 650));
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
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 65));
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
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(350, 55));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL);
            }
        });
    }

    private <E> JComboBox<E> createStyledComboBox(E[] items) {
        JComboBox<E> comboBox = new JComboBox<>(items);
        comboBox.setFont(Theme.FONT_STATUS);
        comboBox.setBackground(Theme.BG_PANEL);
        comboBox.setForeground(Theme.TEXT_LIGHT);
        ((JComponent) comboBox.getRenderer()).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return comboBox;
    }

    /**
     * PERBAIKAN: Menggunakan CompoundBorder untuk memberikan ruang (padding) di dalam
     * TitledBorder, sehingga judul tidak tertimpa oleh komponen di dalamnya.
     */
    private JPanel createOptionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        // Border luar dengan judul
        Border lineBorder = BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 1, true);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, " " + title + " ", TitledBorder.LEFT, TitledBorder.TOP, Theme.FONT_STATUS.deriveFont(16f), Theme.TEXT_LIGHT);

        // Border dalam untuk padding
        Border innerPadding = new EmptyBorder(15, 10, 10, 10); // top, left, bottom, right

        // Menggabungkan keduanya
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, innerPadding));

        return panel;
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Tic-Tac-Toe");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel();
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        add(centerContainer, BorderLayout.CENTER);

        // --- Tombol-tombol ---
        JButton onlineButton = new JButton("Play Online");
        stylePrimaryButton(onlineButton);
        onlineButton.addActionListener(e -> cardLayout.show(mainPanel, "ONLINE_MENU"));

        JButton pvaButton = new JButton("Play Solo (vs AI)");
        stylePrimaryButton(pvaButton);
        pvaButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_AI));

        JButton pvpButton = new JButton("Play Local (2 Players)");
        stylePrimaryButton(pvpButton);
        pvpButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_PLAYER));

        JButton leaderboardButton = new JButton("Leaderboard");
        styleSecondaryButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> {
            if (leaderboardPanel != null) leaderboardPanel.refreshLeaderboard();
            cardLayout.show(mainPanel, "LEADERBOARD");
        });

        JButton settingsButton = new JButton("Settings");
        styleSecondaryButton(settingsButton);
        settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "SETTINGS"));

        // --- Panel Opsi (Untuk Local & Solo) ---
        JPanel optionsPanel = createOptionPanel("Game Options (Solo & Local)");
        optionsPanel.setMaximumSize(new Dimension(350, 180));
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbcOptions = new GridBagConstraints();
        gbcOptions.fill = GridBagConstraints.HORIZONTAL;
        gbcOptions.insets = new Insets(2, 5, 2, 5);
        gbcOptions.weightx = 1.0;

        boardSizeSelector = createStyledComboBox(new String[]{"3x3", "5x5", "7x7"});
        variantSelector = createStyledComboBox(GameMain.GameVariant.values());
        difficultySelector = createStyledComboBox(GameMain.Difficulty.values());
        difficultySelector.setSelectedItem(GameMain.Difficulty.HARD);

        difficultyLabel = new JLabel("AI Difficulty:");
        JLabel sizeLabel = new JLabel("Grid Size:");
        JLabel variantLabel = new JLabel("Game Rules:");

        for (JLabel label : new JLabel[]{sizeLabel, variantLabel, difficultyLabel}) {
            label.setForeground(Theme.TEXT_LIGHT);
            label.setFont(Theme.FONT_STATUS);
        }

        gbcOptions.gridx = 0; gbcOptions.gridy = 0; gbcOptions.weightx = 0.4; optionsPanel.add(sizeLabel, gbcOptions);
        gbcOptions.gridx = 1; gbcOptions.gridy = 0; gbcOptions.weightx = 0.6; optionsPanel.add(boardSizeSelector, gbcOptions);

        gbcOptions.gridx = 0; gbcOptions.gridy = 1; optionsPanel.add(variantLabel, gbcOptions);
        gbcOptions.gridx = 1; gbcOptions.gridy = 1; optionsPanel.add(variantSelector, gbcOptions);

        gbcOptions.gridx = 0; gbcOptions.gridy = 2; optionsPanel.add(difficultyLabel, gbcOptions);
        gbcOptions.gridx = 1; gbcOptions.gridy = 2; optionsPanel.add(difficultySelector, gbcOptions);

        // --- Susun Komponen di Panel Tengah ---
        centerContainer.add(onlineButton);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContainer.add(pvaButton);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        centerContainer.add(pvpButton);
        centerContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        centerContainer.add(optionsPanel);
        centerContainer.add(Box.createVerticalStrut(20));

        JPanel otherButtonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        otherButtonsPanel.setOpaque(false);
        otherButtonsPanel.setMaximumSize(new Dimension(350, 55));
        otherButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherButtonsPanel.add(leaderboardButton);
        otherButtonsPanel.add(settingsButton);
        centerContainer.add(otherButtonsPanel);
    }

    private void startGame(GameMain.GameMode mode) {
        try {
            if (gameMain == null) return;

            boolean isAiMode = (mode == GameMain.GameMode.PLAYER_VS_AI);
            difficultyLabel.setVisible(isAiMode);
            difficultySelector.setVisible(isAiMode);

            int size = parseSize((String) boardSizeSelector.getSelectedItem());
            GameMain.GameVariant variant = (GameMain.GameVariant) variantSelector.getSelectedItem();
            GameMain.Difficulty difficulty = (GameMain.Difficulty) difficultySelector.getSelectedItem();

            gameMain.setDifficulty(difficulty);
            gameMain.startNewGame(mode, size, variant);
            cardLayout.show(mainPanel, "GAME");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int parseSize(String sizeStr) {
        switch (sizeStr) {
            case "5x5": return 5;
            case "7x7": return 7;
            default: return 3;
        }
    }
}