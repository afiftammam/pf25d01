package TTTConsole;


import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class AssetManager {


    private static final Map<String, BufferedImage> images = new HashMap<>();
    private static final Map<String, Clip> sounds = new HashMap<>();


    public static void loadAssets() {
        System.out.println("--- Memulai Pemuatan Aset ---");


        // Memuat semua gambar yang dibutuhkan
        loadImage("BACKGROUND", "TTTConsole/images/background.png");
        loadImage("CROSS", "TTTConsole/images/ex.png");
        loadImage("NOUGHT", "TTTConsole/images/bulat.png");
        System.out.println("--- Pemuatan Gambar Selesai ---");




        // Memuat semua suara yang dibutuhkan
        System.out.println("\n--- Memulai Pemuatan Suara ---");
        loadSound("GAME_START", "TTTConsole/audio/GameStart.wav");
        loadSound("CROSS_MOVE", "TTTConsole/audio/Ex.wav");
        loadSound("NOUGHT_MOVE", "TTTConsole/audio/Bulat.wav");
        loadSound("WIN", "TTTConsole/audio/Winning.wav");
        loadSound("LOSE", "TTTConsole/audio/lose.wav");
        loadSound("DRAW", "TTTConsole/audio/Draw.wav");
        System.out.println("--- Pemuatan Suara Selesai ---");
    }


    private static void loadImage(String name, String path) {
        System.out.println("Mencari gambar '" + name + "' di -> " + path);
        try {
            URL url = AssetManager.class.getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("-> GAGAL: File gambar tidak ditemukan.");
                return;
            }
            System.out.println("-> BERHASIL: File ditemukan.");
            images.put(name.toUpperCase(), ImageIO.read(url));
        } catch (IOException e) {
            System.err.println("-> ERROR: Gagal membaca file gambar.");
            e.printStackTrace();
        }
    }


    private static void loadSound(String name, String path) {
        System.out.println("Mencari suara '" + name + "' di -> " + path);
        try {
            URL url = AssetManager.class.getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("-> GAGAL: File suara tidak ditemukan.");
                return;
            }
            System.out.println("-> BERHASIL: File ditemukan.");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            sounds.put(name.toUpperCase(), clip);
        } catch (Exception e) {
            System.err.println("-> ERROR: Gagal memuat file suara.");
            e.printStackTrace();
        }
    }


    public static BufferedImage getImage(String name) {
        return images.get(name.toUpperCase());
    }


    public static Clip getSound(String name) {
        return sounds.get(name.toUpperCase());
    }
}

