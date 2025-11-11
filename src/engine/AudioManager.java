package engine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Random;

import static utils.Constants.NUMBER_OF_RANDOM_SOUND;

public class AudioManager {
    private static MediaPlayer musicPlayer;
    private static Media[] meowSounds;
    private static Random random;
    private static boolean soundEnabled;
    protected static double musicVolume = 0.5;
    protected static double sfxVolume = 0.5;
    private static double volumeBeforeMute;
    private static Media paddleCollidingSound;
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
        musicPlayer = new MediaPlayer(gameMusic);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.setVolume(musicVolume);

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
        if(musicVolume != 0) {
            volumeBeforeMute = musicVolume;
            musicVolume = 0;
            setMusicVolume(musicVolume);
        } else {
            musicVolume = volumeBeforeMute;
            setMusicVolume(musicVolume);
        }
    }

    public void playBackgroundMusic() {
        if (soundEnabled && musicPlayer != null) {
            musicPlayer.play();
        }
    }

    public static void stopBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public void pauseBackgroundMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
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
                meowPlayer.setVolume(sfxVolume);
                meowPlayer.play();
            } catch (Exception e) {
                System.out.println("Lỗi khi phát meow: " + e.getMessage());
            }
        }
    }

    public void playPaddleCollisionSound() {
        playSound(paddleCollidingSound);
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
                player.setVolume(sfxVolume);
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

    public double getMusicVolume() {
        return musicVolume;
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        if (musicPlayer != null) {
            musicPlayer.setVolume(musicVolume);
        }
    }

    public double getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(double volume) {
        sfxVolume = volume;
    }

    public MediaPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void dispose() {
        if (musicPlayer != null) {
            musicPlayer.dispose();
            musicPlayer = null;
        }
    }
}