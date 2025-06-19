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

        // --- PERBAIKAN: Gunakan ukuran default yang tetap untuk panel menu ---
        // Ukuran ini didasarkan pada papan 3x3 (3 * 140 = 420) plus tinggi panel bawah (70)
        setPreferredSize(new Dimension(420, 490));

        setBackground(Theme.BG_MAIN);
        initUI();
    }

    public void setGamePanel(GameMain gameMain) { this.gameMain = gameMain; }
    public void setLeaderboardPanel(LeaderboardPanel leaderboardPanel) { this.leaderboardPanel = leaderboardPanel; }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Tic Tac Toe");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel boardSizePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        boardSizePanel.setOpaque(false);
        JLabel boardSizeLabel = new JLabel("Board Size:");
        boardSizeLabel.setFont(Theme.FONT_STATUS);
        boardSizeLabel.setForeground(Theme.TEXT_LIGHT);
        boardSizeSelector = new JComboBox<>(new String[]{"3x3", "4x4"});
        boardSizeSelector.setFont(Theme.FONT_STATUS);
        boardSizePanel.add(boardSizeLabel);
        boardSizePanel.add(boardSizeSelector);

        JButton pvpButton = new JButton("Player vs Player");
        styleButton(pvpButton);
        pvpButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_PLAYER));

        JPanel pvaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pvaPanel.setOpaque(false);

        JButton pvaButton = new JButton("Player vs AI");
        styleButton(pvaButton);

        String[] difficulties = { "Easy", "Medium", "Hard" };
        difficultySelector = new JComboBox<>(difficulties);
        difficultySelector.setSelectedIndex(2);
        difficultySelector.setFont(Theme.FONT_STATUS);

        pvaPanel.add(pvaButton);
        pvaPanel.add(difficultySelector);

        pvaButton.addActionListener(e -> startGame(GameMain.GameMode.PLAYER_VS_AI));

        JButton leaderboardButton = new JButton("Leaderboard");
        styleButton(leaderboardButton);
        leaderboardButton.addActionListener(e -> {
            if (leaderboardPanel != null) {
                leaderboardPanel.refreshLeaderboard();
                cardLayout.show(mainPanel, "LEADERBOARD");
            }
        });

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(boardSizePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        contentPanel.add(pvpButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(pvaPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(leaderboardButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(contentPanel, gbc);
    }

    private void startGame(GameMain.GameMode mode) {
        String selectedSize = (String) boardSizeSelector.getSelectedItem();
        int size = "3x3".equals(selectedSize) ? 3 : 4;

        String selectedDiff = (String) difficultySelector.getSelectedItem();
        GameMain.Difficulty diff = GameMain.Difficulty.HARD;
        if ("Easy".equals(selectedDiff)) diff = GameMain.Difficulty.EASY;
        if ("Medium".equals(selectedDiff)) diff = GameMain.Difficulty.MEDIUM;

        gameMain.setDifficulty(diff);
        gameMain.startNewGame(mode, size);
        cardLayout.show(mainPanel, "GAME");
    }

    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setBackground(Theme.BG_PANEL);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.GRID, 2));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(280, 65);
        if (button.getText().equals("Player vs AI")) {
            buttonSize = new Dimension(200, 65);
        }
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(Theme.NOUGHT); }
            public void mouseExited(MouseEvent evt) { button.setBackground(Theme.BG_PANEL); }
        });
    }
}