// afiftammam/pf25d01/pf25d01-d5e914db64e716630e5da884f8aadbbd72a6a70b/TTTConsole/AudioManager.java
package TTTConsole;


import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class AudioManager {


    private static float masterVolume = 0.8f; // Nilai dari 0.0 hingga 1.0
    private static float musicVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    private static Clip currentMusic;


    // Memutar sound effect (satu kali)
    public static void playSound(String name) {
        Clip clip = AssetManager.getSound(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            setClipVolume(clip, sfxVolume);
            clip.start();
        } else {
            System.err.println("Suara tidak ditemukan di AssetManager: " + name);
        }
    }


    // Memutar musik (looping)
    public static void playMusic(String name) {
        // Hentikan musik sebelumnya jika ada
        if (currentMusic != null && currentMusic.isRunning()) {
            if (currentMusic.equals(AssetManager.getSound(name))) return; // Jangan putar ulang jika musiknya sama
            currentMusic.stop();
        }


        Clip clip = AssetManager.getSound(name);
        if (clip != null) {
            currentMusic = clip;
            setClipVolume(currentMusic, musicVolume);
            currentMusic.setFramePosition(0);
            currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.err.println("Musik tidak ditemukan di AssetManager: " + name);
        }
    }


    // Menghentikan musik yang sedang berjalan
    public static void stopMusic() {
        if (currentMusic != null && currentMusic.isRunning()) {
            currentMusic.stop();
        }
    }


    // Setter untuk volume dari panel settings
    public static void setMasterVolume(float volume) {
        masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateAllVolumes(); // Perbarui volume musik saat master diubah
    }


    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentMusic != null) {
            setClipVolume(currentMusic, musicVolume);
        }
    }


    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }


    // Getter untuk mendapatkan nilai volume saat ini (untuk JSlider)
    public static float getMasterVolume() { return masterVolume; }
    public static float getMusicVolume() { return musicVolume; }
    public static float getSfxVolume() { return sfxVolume; }


    // Memperbarui volume klip yang sedang berjalan
    private static void updateAllVolumes() {
        if (currentMusic != null) {
            setClipVolume(currentMusic, musicVolume);
        }
    }


    // Mengatur gain (volume) pada sebuah klip audio
    private static void setClipVolume(Clip clip, float specificVolume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float combinedVolume = masterVolume * specificVolume;
            if (combinedVolume <= 0.0001f) {
                gainControl.setValue(gainControl.getMinimum()); // Mute
            } else {
                // Konversi volume linear (0-1) ke desibel (dB)
                float gain = (float) (Math.log10(combinedVolume) * 20.0);
                // Pastikan nilai gain berada dalam rentang yang didukung
                gain = Math.max(gainControl.getMinimum(), Math.min(gain, gainControl.getMaximum()));
                gainControl.setValue(gain);
            }
        }
    }
}

