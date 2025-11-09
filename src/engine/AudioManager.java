package src.engine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;

import static src.utils.Constants.NUMBER_OF_RANDOM_SOUND;

public class AudioManager {
    private static MediaPlayer backgroundMusic;
    private static Media[] meowSounds;
    private static Random random;
    private static boolean soundEnabled;
    protected static double volume = 0.5;
    private static double volumeBeforeMute;
    private static Media paddleCollidingSound;
    private static Media wallCollidingSound;
    private static Media indestructibleBrickCollidingSound;
    private static Media powerUpSound;
    private static Media gameOverSound;
    private static Media buttonClick;
    private static Media buttonTap;

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
        // Paddle Colliding Sound
        File paddleFile = new File("assets/sounds/paddle_colliding_sound.mp3");
        String paddlePath = paddleFile.toURI().toString();
        paddleCollidingSound = new Media(paddlePath);

        // Wall Colliding Sound
        File wallFile = new File("assets/sounds/wall_colliding_sound.mp3");
        String wallPath = wallFile.toURI().toString();
        wallCollidingSound = new Media(wallPath);

        // Indestructible Brick Colliding Sound
        File indestructibleFile = new File("assets/sounds/indestructable_brick_colliding_sound.mp3");
        String indestructiblePath = indestructibleFile.toURI().toString();
        indestructibleBrickCollidingSound = new Media(indestructiblePath);

        File powerUpFile = new File("assets/sounds/powerup_sound.mp3");
        String powerUpPath = powerUpFile.toURI().toString();
        powerUpSound = new Media(powerUpPath);

        File gameOverFile = new File("assets/sounds/game_over.mp3");
        String gameOverPath = gameOverFile.toURI().toString();
        gameOverSound = new Media(gameOverPath);

        File buttonTapFile = new File("assets/sounds/button_tap.mp3");
        String buttonTapPath = buttonTapFile.toURI().toString();
        buttonTap = new Media(buttonTapPath);

        File buttonClickFile = new File("assets/sounds/button_click.mp3");
        String buttonClickPath = buttonClickFile.toURI().toString();
        buttonClick = new Media(buttonClickPath);
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

    public void playGameOverSound() {
        playSound(gameOverSound);
    }

    public void playButtonTapSound() {
        playSound(buttonTap);
    }

    public void playButtonClickSound() {
        playSound(buttonClick);
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

    public void playPaddleCollisionSound() {
        playSound(paddleCollidingSound);
    }

    public void playWallCollisionSound() {
        playSound(wallCollidingSound);
    }

    public void playIndestructibleBrickCollisionSound() {
        playSound(indestructibleBrickCollidingSound);
    }

    public void playPowerUpSound() {
    playSound(powerUpSound);
    }

    private void playSound(Media media) {
        if (soundEnabled && media != null) {
            try {
                MediaPlayer player = new MediaPlayer(media);
                player.setVolume(volume);
                player.play();
                // Đảm bảo MediaPlayer được giải phóng sau khi chơi xong
                player.setOnEndOfMedia(() -> player.dispose());
            } catch (Exception e) {
                System.out.println("Lỗi khi phát âm thanh: " + e.getMessage());
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