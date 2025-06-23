package TTTConsole;


import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class AudioManager {


    public enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }


    // PERUBAHAN: Volume default diubah menjadi MEDIUM
    private static Volume currentVolume = Volume.MEDIUM;


    public static void playSound(String name) {
        if (currentVolume == Volume.MUTE) {
            return;
        }


        Clip clip = AssetManager.getSound(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0); // Putar dari awal
            setClipVolume(clip);
            clip.start();
        } else {
            System.err.println("Suara tidak ditemukan di AssetManager: " + name);
        }
    }


    public static void setVolume(Volume level) {
        currentVolume = level;
    }


    public static Volume getVolume() {
        return currentVolume;
    }


    private static void setClipVolume(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float gain = -80.0f; // Default ke MUTE
            switch (currentVolume) {
                case LOW:    gain = -20.0f; break;
                case MEDIUM: gain = -10.0f; break;
                case HIGH:   gain = 6.0f;  break; // Nilai maksimum untuk gain
            }
            gain = Math.max(gainControl.getMinimum(), Math.min(gain, gainControl.getMaximum()));
            gainControl.setValue(gain);
        }
    }
}

