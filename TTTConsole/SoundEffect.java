package TTTConsole;


import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;


public enum SoundEffect {
    EAT_FOOD("TTTConsole/audio/hidup-jokowi.wav"),
    EXPLODE("TTTConsole/audio/explode.wav"),
    DIE("TTTConsole/audio/die.wav");


    public enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }


    public static Volume volume = Volume.LOW;


    private Clip clip;


    SoundEffect(String soundFileName) {
        try {
            URL url = this.getClass().getClassLoader().getResource(soundFileName);
            if (url != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            } else {
                System.err.println("File suara tidak ditemukan: " + soundFileName);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void play() {
        if (volume != Volume.MUTE && clip != null) {
            if (clip.isRunning())
                clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }


    static void initGame() {
        values();
    }
}

