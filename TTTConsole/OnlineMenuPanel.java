package TTTConsole;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;


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
        setPreferredSize(new Dimension(450, 650));
        setBackground(Theme.BG_MAIN);
        initUI();
    }


    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JLabel titleLabel = new JLabel("Online Play", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE.deriveFont(60f));
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);


        JPanel contentContainer = new JPanel();
        contentContainer.setOpaque(false);
        contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
        add(contentContainer, BorderLayout.CENTER);


        // --- Panel untuk HOST GAME ---
        JPanel hostPanel = createTitledPanel("Host a New Game");
        GridBagConstraints gbcHost = new GridBagConstraints();
        gbcHost.fill = GridBagConstraints.HORIZONTAL;
        gbcHost.insets = new Insets(5, 5, 5, 5);


        gbcHost.gridx = 0; gbcHost.gridy = 0; gbcHost.weightx = 0.3;
        hostPanel.add(createStyledLabel("Your Name:"), gbcHost);
        gbcHost.gridx = 1; gbcHost.gridy = 0; gbcHost.weightx = 0.7;
        JTextField hostNameField = new JTextField();
        styleTextField(hostNameField);
        hostPanel.add(hostNameField, gbcHost);


        gbcHost.gridx = 0; gbcHost.gridy = 1;
        hostPanel.add(createStyledLabel("Grid Size:"), gbcHost);
        gbcHost.gridx = 1; gbcHost.gridy = 1;
        JComboBox<String> boardSizeSelector = createStyledComboBox(new String[]{"3x3", "5x5", "7x7"});
        hostPanel.add(boardSizeSelector, gbcHost);


        gbcHost.gridx = 0; gbcHost.gridy = 2;
        hostPanel.add(createStyledLabel("Game Rules:"), gbcHost);
        gbcHost.gridx = 1; gbcHost.gridy = 2;
        JComboBox<GameMain.GameVariant> variantSelector = createStyledComboBox(GameMain.GameVariant.values());
        hostPanel.add(variantSelector, gbcHost);


        gbcHost.gridx = 0; gbcHost.gridy = 3; gbcHost.gridwidth = 2; gbcHost.insets = new Insets(15, 5, 5, 5);
        JButton hostButton = new JButton("Create Game");
        styleButton(hostButton);
        hostButton.addActionListener(e -> hostGame(hostNameField.getText(), (String)boardSizeSelector.getSelectedItem(), (GameMain.GameVariant)variantSelector.getSelectedItem()));
        hostPanel.add(hostButton, gbcHost);


        // --- Panel untuk JOIN GAME ---
        JPanel joinPanel = createTitledPanel("Join an Existing Game");
        GridBagConstraints gbcJoin = new GridBagConstraints();
        gbcJoin.fill = GridBagConstraints.HORIZONTAL;
        gbcJoin.insets = new Insets(5, 5, 5, 5);


        gbcJoin.gridx = 0; gbcJoin.gridy = 0; gbcJoin.weightx = 0.3;
        joinPanel.add(createStyledLabel("Your Name:"), gbcJoin);
        gbcJoin.gridx = 1; gbcJoin.gridy = 0; gbcJoin.weightx = 0.7;
        JTextField joinNameField = new JTextField();
        styleTextField(joinNameField);
        joinPanel.add(joinNameField, gbcJoin);


        gbcJoin.gridx = 0; gbcJoin.gridy = 1;
        joinPanel.add(createStyledLabel("Game ID:"), gbcJoin);
        gbcJoin.gridx = 1; gbcJoin.gridy = 1;
        JTextField gameIdField = new JTextField();
        styleTextField(gameIdField);
        joinPanel.add(gameIdField, gbcJoin);


        gbcJoin.gridx = 0; gbcJoin.gridy = 2; gbcJoin.gridwidth = 2; gbcJoin.insets = new Insets(15, 5, 5, 5);
        JButton joinButton = new JButton("Join Game");
        styleButton(joinButton);
        joinButton.addActionListener(e -> joinGame(joinNameField.getText(), gameIdField.getText()));
        joinPanel.add(joinButton, gbcJoin);


        // --- Tombol Kembali ---
        JButton backButton = new JButton("Back to Main Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));


        contentContainer.add(hostPanel);
        contentContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        contentContainer.add(joinPanel);


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void hostGame(String username, String sizeStr, GameMain.GameVariant variant) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int size = parseSize(sizeStr);
        String gameId = dbManager.createNewGame(username, size, variant);


        if (gameId != null) {
            JOptionPane.showMessageDialog(this, "Game created! Your Game ID is: " + gameId + "\nShare this ID with your friend.", "Game Hosted", JOptionPane.INFORMATION_MESSAGE);
            gameMain.startNewGame(GameMain.GameMode.ONLINE_MULTIPLAYER, size, variant, gameId, Seed.CROSS, username, "Waiting...");
            cardLayout.show(mainPanel, "GAME");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create game. Please check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void joinGame(String username, String gameId) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (gameId == null || gameId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Game ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        Map<String, Object> gameDetails = dbManager.joinGame(gameId.toUpperCase(), username);


        if (gameDetails != null && !gameDetails.isEmpty()) {
            int size = (int) gameDetails.get("board_size");
            GameMain.GameVariant variant = (GameMain.GameVariant) gameDetails.get("game_variant");
            String opponentUsername = (String) gameDetails.get("player_x");


            gameMain.startNewGame(GameMain.GameMode.ONLINE_MULTIPLAYER, size, variant, gameId.toUpperCase(), Seed.NOUGHT, username, opponentUsername);
            cardLayout.show(mainPanel, "GAME");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to join game.\n- Check Game ID\n- The game might be full or already started.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private int parseSize(String sizeStr) {
        switch (sizeStr) {
            case "5x5": return 5;
            case "7x7": return 7;
            default: return 3;
        }
    }


    // --- Helper untuk styling ---
    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(350, 220));


        Border lineBorder = BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 1, true);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, " " + title + " ", TitledBorder.LEFT, TitledBorder.TOP, Theme.FONT_STATUS.deriveFont(16f), Theme.TEXT_LIGHT);


        Border innerPadding = new EmptyBorder(15, 10, 10, 10);


        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, innerPadding));


        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(Theme.TEXT_LIGHT);
            }
        }
        return panel;
    }


    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_STATUS);
        label.setForeground(Theme.TEXT_LIGHT);
        return label;
    }


    private <E> JComboBox<E> createStyledComboBox(E[] items) {
        JComboBox<E> comboBox = new JComboBox<>(items);
        comboBox.setFont(Theme.FONT_STATUS);
        comboBox.setBackground(Theme.BG_PANEL);
        comboBox.setForeground(Theme.TEXT_LIGHT);
        return comboBox;
    }


    private void styleTextField(JTextField textField) {
        textField.setBackground(Theme.BG_PANEL);
        textField.setForeground(Theme.TEXT_LIGHT);
        textField.setCaretColor(Theme.TEXT_LIGHT);
        textField.setFont(Theme.FONT_STATUS);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }


    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON.deriveFont(16f));
        button.setBackground(Theme.ACCENT_COLOR);
        button.setForeground(Theme.TEXT_DARK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.ACCENT_COLOR.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.ACCENT_COLOR);
            }
        });
    }
}

