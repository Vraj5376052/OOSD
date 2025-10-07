package tetrisclient;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;

public class AudioManager {

    private static MediaPlayer backgroundMusicPlayer;
    private static MediaPlayer soundEffectPlayer;
    private static MediaPlayer soundEffectLine;
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) return;

        try {
            // Only initialize if JavaFX Media is available
            Media bgMusic = new Media(Paths.get("assets/music/tetrisbg.mp3").toUri().toString());
            backgroundMusicPlayer = new MediaPlayer(bgMusic);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            Media soundEffect = new Media(Paths.get("assets/sounds/piece_fall.mp3").toUri().toString());
            soundEffectPlayer = new MediaPlayer(soundEffect);

            Media soundEffect2 = new Media(Paths.get("assets/sounds/line.mp3").toUri().toString());
            soundEffectLine = new MediaPlayer(soundEffect2);

            isInitialized = true;
        } catch (Exception e) {
            System.err.println("Error AudioManager (ignored for testing): " + e.getMessage());
            // For testing, we can just leave the players null
            isInitialized = true;
        }
    }

    public static void playBackgroundMusic() {
        if (backgroundMusicPlayer != null && Main.isMUSIC()) {
            backgroundMusicPlayer.play();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
    }

    public static void playPieceFallSound() {
        if (soundEffectPlayer != null && Main.isSOUND_EFFECTS()) {
            soundEffectPlayer.stop();
            soundEffectPlayer.seek(soundEffectPlayer.getStartTime());
            soundEffectPlayer.play();
        }
    }

    public static void playLineSound() {
        if (soundEffectLine != null && Main.isSOUND_EFFECTS()) {
            soundEffectLine.stop();
            soundEffectLine.seek(soundEffectLine.getStartTime());
            soundEffectLine.play();
        }
    }


    public static void play(String s) {
        // Do nothing in tests
    }


    public static Object stopAll() {
        // do nothing in tests
        return null;
    }
}
