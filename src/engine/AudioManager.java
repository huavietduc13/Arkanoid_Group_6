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
    protected static double volume = 0.5;
    private static double volumeBeforeMute;

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

    public static void toggleMute() {
        if(volume != 0) {
            volumeBeforeMute = volume;
            volume = 0;
            setMasterVolume(volume);
        } else {
            volume = volumeBeforeMute;
            setMasterVolume(volume);
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

    public void playRandomMeowSound() {
        if (soundEnabled && meowSounds != null && meowSounds[0] != null) {
            try {
                int randomIndex = random.nextInt(NUMBER_OF_RANDOM_SOUND);
                MediaPlayer meowPlayer = new MediaPlayer(meowSounds[randomIndex]);
                meowPlayer.setVolume(volume);
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

    public static void setMasterVolume(double volume) {
        AudioManager.volume = volume;
        backgroundMusic.setVolume(volume);
    }

    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
}