package engine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;

import static utils.Constants.NUMBER_OF_RANDOM_SOUND;

public class AudioManager {
    private static MediaPlayer backgroundMusic;
    private static Media[] meowSounds;
    private static Random random;
    private static boolean soundEnabled;

    public AudioManager() {
        this.random = new Random();
        this.meowSounds = new Media[NUMBER_OF_RANDOM_SOUND];
        this.soundEnabled = true;
        loadSounds();
    }

    private void loadSounds() {
        // Background music
        File musicFile = new File("assets/sounds/gamePlay.mp3");
        String musicPath = musicFile.toURI().toString();
        Media gameMusic = new Media(musicPath);
        backgroundMusic = new MediaPlayer(gameMusic);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.setVolume(0.5);

        // Meow sounds
        for (int i = 0; i < NUMBER_OF_RANDOM_SOUND; i++) {
            File meowFile = new File("assets/sounds/meow_" + (i + 1) + ".mp3");
            String meowPath = meowFile.toURI().toString();
            meowSounds[i] = new Media(meowPath);
        }
    }

    public void playBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null) {
            backgroundMusic.play();
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public void pauseBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
        }
    }

    public static void playRandomMeowSound() {
        if (soundEnabled && meowSounds != null && meowSounds[0] != null) {
            try {
                int randomIndex = random.nextInt(NUMBER_OF_RANDOM_SOUND);
                MediaPlayer meowPlayer = new MediaPlayer(meowSounds[randomIndex]);
                meowPlayer.setVolume(0.4);
                meowPlayer.play();
            } catch (Exception e) {
                System.out.println("Lỗi khi phát meow: " + e.getMessage());
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setBackgroundMusicVolume(double volume) {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
        }
    }

    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
}