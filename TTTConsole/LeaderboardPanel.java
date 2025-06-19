package TTTConsole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final DatabaseManager dbManager;
    private JTable leaderboardTable;
    private final String[] columnNames = {"Rank", "Username", "Wins", "Losses", "Draws"};

    public LeaderboardPanel(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);

        leaderboardTable = new JTable();
        leaderboardTable.setBackground(Theme.BG_PANEL);
        leaderboardTable.setForeground(Theme.TEXT_LIGHT);
        leaderboardTable.setFont(Theme.FONT_STATUS);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(Theme.FONT_BUTTON);
        leaderboardTable.getTableHeader().setBackground(Theme.GRID);
        leaderboardTable.getTableHeader().setForeground(Theme.TEXT_LIGHT);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.GRID, 2));
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Theme.BG_MAIN);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshLeaderboard() {
        List<Player> players = dbManager.getLeaderboard();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int rank = 1;
        for (Player p : players) {
            model.addRow(new Object[]{
                    rank++,
                    p.getUsername(),
                    p.getWins(),
                    p.getLosses(),
                    p.getDraws()
            });
        }
        leaderboardTable.setModel(model);
    }

    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setBackground(Theme.BG_PANEL);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.GRID, 2));
        button.setPreferredSize(new Dimension(200, 50));
    }
}