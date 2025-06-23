// Simpan sebagai TTTConsole/AudioSettingsPanel.java
package TTTConsole;


import javax.swing.*;
import java.awt.*;


public class AudioSettingsPanel extends JPanel {


    private final JPanel mainPanel;
    private final CardLayout cardLayout;


    public AudioSettingsPanel(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        initUI();
    }


    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));


        // Judul Panel
        JLabel titleLabel = new JLabel("Audio Settings", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_TITLE.deriveFont(48f));
        titleLabel.setForeground(Theme.TEXT_LIGHT);
        add(titleLabel, BorderLayout.NORTH);


        // Panel untuk slider
        JPanel settingsGrid = new JPanel();
        settingsGrid.setOpaque(false);
        settingsGrid.setLayout(new BoxLayout(settingsGrid, BoxLayout.Y_AXIS));
        settingsGrid.setMaximumSize(new Dimension(500, 200));
        settingsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);


        settingsGrid.add(createVolumeSlider("Master Volume", AudioManager::setMasterVolume, AudioManager.getMasterVolume()));
        settingsGrid.add(Box.createRigidArea(new Dimension(0, 25)));
        settingsGrid.add(createVolumeSlider("Music", AudioManager::setMusicVolume, AudioManager.getMusicVolume()));
        settingsGrid.add(Box.createRigidArea(new Dimension(0, 25)));
        settingsGrid.add(createVolumeSlider("Sound Effects", AudioManager::setSfxVolume, AudioManager.getSfxVolume()));


        // Panel tengah untuk memastikan grid tidak meregang
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(settingsGrid);


        add(centerPanel, BorderLayout.CENTER);


        // Tombol kembali
        JButton backButton = new JButton("Back to Settings");
        styleButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SETTINGS"));


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }


    // Helper untuk membuat slider volume
    private JPanel createVolumeSlider(String labelText, VolumeSetter setter, float initialValue) {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(500, 40));


        JLabel label = new JLabel(labelText);
        label.setFont(Theme.FONT_STATUS);
        label.setForeground(Theme.TEXT_LIGHT);
        label.setPreferredSize(new Dimension(150, 30));
        panel.add(label, BorderLayout.WEST);


        JSlider slider = new JSlider(0, 100, (int) (initialValue * 100));
        slider.setOpaque(false);
        slider.addChangeListener(e -> setter.set((float) slider.getValue() / 100.0f));
        panel.add(slider, BorderLayout.CENTER);


        return panel;
    }


    // Helper untuk gaya tombol
    private void styleButton(JButton button) {
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BG_PANEL);
        button.setForeground(Theme.TEXT_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_COLOR, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 65));
    }


    // Functional interface untuk mempermudah passing metode setter
    @FunctionalInterface
    interface VolumeSetter {
        void set(float volume);
    }
}

