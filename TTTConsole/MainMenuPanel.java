package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
        initUI();
    }

    public void setGamePanel(GameMain gameMain) { this.gameMain = gameMain; }
    public void setLeaderboardPanel(LeaderboardPanel leaderboardPanel) { this.leaderboardPanel = leaderboardPanel; }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Judul
        JLabel titleLabel = new JLabel("Tic-Tac-Toe");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Panel Tengah untuk semua tombol dan opsi
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan Tombol
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(createStyledButton("Play Solo", e -> startGame(GameMain.GameMode.PLAYER_VS_AI), true));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createStyledButton("Play with a Friend", e -> startGame(GameMain.GameMode.PLAYER_VS_PLAYER), true));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createStyledButton("Leaderboard", e -> cardLayout.show(mainPanel, "LEADERBOARD"), false));
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createStyledButton("Settings", e -> cardLayout.show(mainPanel, "SETTINGS"), false));
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // Panel Bawah untuk Opsi
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        boardSizeSelector = new JComboBox<>(new String[]{"3x3", "5x5", "7x7"});
        variantSelector = new JComboBox<>(GameMain.GameVariant.values());
        difficultySelector = new JComboBox<>(GameMain.Difficulty.values());
        difficultySelector.setSelectedItem(GameMain.Difficulty.HARD);

        optionsPanel.add(new JLabel("Grid:"));
        optionsPanel.add(boardSizeSelector);
        optionsPanel.add(new JLabel("Rules:"));
        optionsPanel.add(variantSelector);
        optionsPanel.add(new JLabel("AI:"));
        optionsPanel.add(difficultySelector);

        // Style label di optionsPanel
        for(Component comp : optionsPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(Theme.textLight);
                comp.setFont(Theme.FONT_STATUS.deriveFont(14f));
            }
        }

        add(optionsPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, ActionListener listener, boolean isPrimary) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setFont(Theme.FONT_BUTTON);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(320, 65);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        if (isPrimary) {
            button.setBackground(Theme.accentColor);
            button.setForeground(Theme.textDark);
            button.setBorder(BorderFactory.createEmptyBorder());
        } else {
            button.setBackground(Theme.bgPanel);
            button.setForeground(Theme.textLight);
            button.setBorder(BorderFactory.createLineBorder(Theme.accentColor, 2));
        }
        return button;
    }

    private void startGame(GameMain.GameMode mode) {
        try {
            String selectedSizeStr = (String) boardSizeSelector.getSelectedItem();
            int size = "3x3".equals(selectedSizeStr) ? 3 : ("5x5".equals(selectedSizeStr) ? 5 : 7);

            GameMain.GameVariant selectedVariant = (GameMain.GameVariant) variantSelector.getSelectedItem();
            GameMain.Difficulty selectedDifficulty = (GameMain.Difficulty) difficultySelector.getSelectedItem();

            gameMain.setDifficulty(selectedDifficulty);
            gameMain.startNewGame(mode, size, selectedVariant);

            cardLayout.show(mainPanel, "GAME");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memulai game:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}