package TTTConsole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final DatabaseManager dbManager;
    private JTable leaderboardTable;
    private final String[] columnNames = {"Rank", "Username", "Wins", "Losses", "Draws", "Win Rate"};

    public LeaderboardPanel(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.bgMain); // Perbaikan
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.textLight); // Perbaikan
        add(titleLabel, BorderLayout.NORTH);

        leaderboardTable = new JTable();
        leaderboardTable.setBackground(Theme.bgPanel); // Perbaikan
        leaderboardTable.setForeground(Theme.textLight); // Perbaikan
        leaderboardTable.setFont(Theme.FONT_STATUS);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(Theme.FONT_BUTTON);
        leaderboardTable.getTableHeader().setBackground(Theme.bgPanel); // Perbaikan
        leaderboardTable.getTableHeader().setForeground(Theme.textLight); // Perbaikan

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.accentColor, 2)); // Perbaikan
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Theme.bgMain); // Perbaikan
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ... (sisa kode tidak berubah)
    public void refreshLeaderboard() {
        List<Player> players = dbManager.getLeaderboard();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(1);
        int rank = 1;
        for (Player p : players) {
            model.addRow(new Object[]{ rank++, p.getUsername(), p.getWins(), p.getLosses(), p.getDraws(), percentFormat.format(p.getWinRate()) });
        }
        leaderboardTable.setModel(model);
    }
    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.bgPanel);
        button.setForeground(Theme.textLight);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.accentColor, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 65));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { button.setBackground(Theme.bgPanel.brighter()); }
            public void mouseExited(MouseEvent evt) { button.setBackground(Theme.bgPanel); }
        });
    }
}