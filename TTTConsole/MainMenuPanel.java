package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private GameMain gameMain; // Diubah dari GamePanel

    public MainMenuPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 70));
        setBackground(Theme.BG_MAIN);
        setLayout(null); // Gunakan layout absolut untuk penempatan presisi
        initUI();
    }

    public void setGamePanel(GameMain gameMain) { // Diubah dari GamePanel
        this.gameMain = gameMain;
    }

    private void initUI() {
        // Tombol Player vs Player
        JButton pvpButton = new JButton("Player vs Player");
        pvpButton.setFont(Theme.FONT_BUTTON);
        pvpButton.setBounds(100, 250, 260, 60);
        styleButton(pvpButton);
        pvpButton.addActionListener(e -> {
            gameMain.startNewGame(GameMain.GameMode.PLAYER_VS_PLAYER); // Diubah dari GamePanel
            cardLayout.show(mainPanel, "GAME");
        });

        // Tombol Player vs AI
        JButton pvaButton = new JButton("Player vs AI");
        pvaButton.setFont(Theme.FONT_BUTTON);
        pvaButton.setBounds(100, 330, 260, 60);
        styleButton(pvaButton);
        pvaButton.addActionListener(e -> {
            gameMain.startNewGame(GameMain.GameMode.PLAYER_VS_AI); // Diubah dari GamePanel
            cardLayout.show(mainPanel, "GAME");
        });

        add(pvpButton);
        add(pvaButton);
    }

    private void styleButton(JButton button) {
        button.setForeground(Theme.TEXT_LIGHT);
        button.setBackground(Theme.BG_PANEL);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.GRID, 2));

        // Efek hover untuk tombol
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Theme.NOUGHT);
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Theme.BG_PANEL);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Gambar Judul
        g2d.setFont(Theme.FONT_TITLE);
        g2d.setColor(Theme.TEXT_LIGHT);
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth("Tic Tac Toe");
        g2d.drawString("Tic Tac Toe", (getWidth() - titleWidth) / 2, 150);
    }
}