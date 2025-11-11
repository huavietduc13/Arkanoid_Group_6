package engine;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import static engine.AudioManager.*;
import static utils.Constants.*;

public class SceneManager {

    private static Stage priStage;
    private static Scene startMenuScene;
    private Scene gameScene;
    private VBox pauseMenu;
    private VBox settingMenu;
    private Scene levelSelectionScene;
    private AnimationTimer timer;
    private GameManager game;
    private AudioManager audioManager; // KHAI BÁO

    private ImageView volumeIcon;
    private Image volHigh;
    private Image volMedium;
    private Image volLow;
    private Image volMute;

    private static ImageView playAgainButton;
    private static ImageView nextLevelButton;
    private static ImageView menuButton;
    private static Text winText;
    private Font customFont;
    private static Pane winOverlay;

    private ImageView musicSliderKnob;
    private ImageView sfxSliderKnob;
    private double musicSliderStartX = 0;
    private double sfxSliderStartX = 0;
    private final double SLIDER_WIDTH = 255;

    public SceneManager(Stage priStage) {
        this.priStage = priStage;
        this.customFont = TextManager.loadFont();
        this.audioManager = new AudioManager(); // KHỞI TẠO
        createPauseMenu();
        createVolumeButton();
        createWinScreenElements();
        createSettingMenu();
    }

    public void createWinScreenElements() {
        if (winOverlay == null) {
            winOverlay = new Pane();
            winOverlay.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            winOverlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
            winOverlay.setVisible(false);
        }

        if (playAgainButton == null) {
            Image playAgainImg = new Image("file:assets/images/playAgainButton.png");
            playAgainButton = new ImageView(playAgainImg);
            playAgainButton.setFitWidth(200);
            playAgainButton.setFitHeight(80);
            playAgainButton.setOnMouseEntered(e -> playAgainButton.setOpacity(0.8));
            playAgainButton.setOnMouseExited(e -> playAgainButton.setOpacity(1.0));
        }

        if (nextLevelButton == null) {
            Image nextLevelImg = new Image("file:assets/images/nextLevelButton.png");
            nextLevelButton = new ImageView(nextLevelImg);
            nextLevelButton.setFitWidth(200);
            nextLevelButton.setFitHeight(80);
            nextLevelButton.setOnMouseEntered(e -> nextLevelButton.setOpacity(0.8));
            nextLevelButton.setOnMouseExited(e -> nextLevelButton.setOpacity(1.0));
        }

        if (menuButton == null) {
            Image menuImg = new Image("file:assets/images/mainMenuButton.png");
            menuButton = new ImageView(menuImg);
            menuButton.setFitWidth(200);
            menuButton.setFitHeight(80);
            menuButton.setOnMouseEntered(e -> menuButton.setOpacity(0.8));
            menuButton.setOnMouseExited(e -> menuButton.setOpacity(1.0));
            menuButton.setOnMouseClicked(e -> priStage.setScene(startMenuScene));
        }

        if (winText == null) {
            winText = new Text("LEVEL COMPLETE!");
            winText.setFill(Color.YELLOW);
            winText.setFont(customFont);
            winText.setTextAlignment(TextAlignment.CENTER);
            winText.setVisible(false);
        }
    }

    public static void showWinScreen(boolean show, int currentLevel) {
        if (winOverlay != null) {
            winOverlay.setVisible(show);
            if (show) winOverlay.toFront();
        }

        if (winText != null) {
            winText.setVisible(show);
            if (show) winText.toFront();
        }
        if (playAgainButton != null) {
            playAgainButton.setVisible(show);
            if (show) playAgainButton.toFront();
        }
        if (nextLevelButton != null) {
            // Not show next level button at the last level
            boolean isLastLevel = currentLevel >= 2;
            nextLevelButton.setVisible(show && !isLastLevel);
            if (show && !isLastLevel) nextLevelButton.toFront();
        }
        if (menuButton != null) {
            menuButton.setVisible(show);
            if (show) menuButton.toFront();
        }

        if (show) {
            if (winText != null) {
                winText.setX((SCREEN_WIDTH - winText.getLayoutBounds().getWidth()) / 2);
                winText.setY(SCREEN_HEIGHT / 3);
            }

            double centerX = SCREEN_WIDTH / 2;
            double centerY = SCREEN_HEIGHT / 2 + 50;

            if (menuButton != null) {
                menuButton.setX(centerX - 100);
                menuButton.setY(centerY - 100);
            }

            if (playAgainButton != null) {
                if (currentLevel >= 2) {
                    playAgainButton.setX(centerX - playAgainButton.getFitWidth() / 2);
                    playAgainButton.setY(centerY);
                } else {
                    playAgainButton.setX(centerX - playAgainButton.getFitWidth() - 20);
                    playAgainButton.setY(centerY);
                }
            }

            if (nextLevelButton != null && currentLevel < 2) {
                nextLevelButton.setX(centerX + 20);
                nextLevelButton.setY(centerY);
            }
        }
    }

    public void createVolumeButton() {
        volHigh = new Image("file:assets/images/volume_high.png");
        volMedium = new Image("file:assets/images/volume_medium.png");
        volLow = new Image("file:assets/images/volume_low.png");
        volMute = new Image("file:assets/images/volume_mute.png");

        volumeIcon = new ImageView(volMedium);
        volumeIcon.setFitHeight(30);
        volumeIcon.setFitWidth(30);
        volumeIcon.setLayoutX(GAME_AREA_WIDTH - 50);
        volumeIcon.setLayoutY(5);
        volumeIcon.setCursor(Cursor.HAND);

        // Âm thanh khi di chuột
        volumeIcon.setOnMouseEntered(e -> volumeIcon.setOpacity(0.9));
        volumeIcon.setOnMouseExited(e -> volumeIcon.setOpacity(1.0));

        // Âm thanh khi nhấn
        volumeIcon.setOnMousePressed(e -> audioManager.playButtonClickSound());

        volumeIcon.setOnMouseClicked(e -> {
            toggleMute();
            updateVolumeIcon();
        });

        volumeIcon.setOnScroll(e -> {
//            System.out.println("running");
            if(e.getDeltaY() > 0) musicVolume += VOLUME_STEP;
            else if (e.getDeltaY() < 0) musicVolume -= VOLUME_STEP;
            musicVolume = Math.max(0.0, Math.min(1.0, musicVolume));
            audioManager.setMusicVolume(musicVolume);
            updateVolumeIcon();
        });
    }

    private void updateVolumeIcon() {
        if (musicVolume == 0) volumeIcon.setImage(volMute);
        else if (musicVolume <= 0.4) volumeIcon.setImage(volLow);
        else if (musicVolume <= 0.8) volumeIcon.setImage(volMedium);
        else volumeIcon.setImage(volHigh);
    }

    public void createPauseMenu() {
        // Resume button
        ImageView resumeButton = createButtonImageView("file:assets/images/resumeButton.png", 200, 80);
        resumeButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            togglePauseMenu();
        });

        // Restart button
        ImageView restartButton = createButtonImageView("file:assets/images/restartButton.png", 200, 80);
        restartButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            togglePauseMenu();
            game.restart();
        });

        // To main menu button
        ImageView menuButton = createButtonImageView("file:assets/images/mainMenuButton.png", 200, 80);
        menuButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            returnToMenu();
        });

        // Pause menu layout
        pauseMenu = new VBox(20, resumeButton, restartButton, menuButton);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        pauseMenu.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        pauseMenu.setVisible(false);
    }

    public void createSettingMenu() {
        // Setting panel
        Image settingPanelImg = new Image("file:assets/images/setting.png");
        ImageView settingPanel = new ImageView(settingPanelImg);
        settingPanel.setFitWidth(540);
        settingPanel.setFitHeight(180);

        // Back button
        ImageView backButton = createButtonImageView("file:assets/images/button_back.png", 200, 80);
        backButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            toggleSettingMenu();
        });

        // Save button
        ImageView saveButton = createButtonImageView("file:assets/images/button_save.png", 200, 80);
        saveButton.setOnMouseClicked(e -> {

        });

        HBox smallButtons = new HBox(50, backButton);
        smallButtons.setAlignment(Pos.CENTER);
        smallButtons.setTranslateY(-100);

        Pane musicSlider = createVolumeSlider(true);
        Pane sfxSlider = createVolumeSlider(false);

        settingMenu = new VBox(36, settingPanel, musicSlider, sfxSlider, smallButtons);
        settingMenu.setAlignment(Pos.CENTER);
        settingMenu.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        settingMenu.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));
        settingMenu.setVisible(false);
    }

    private Pane createVolumeSlider(boolean isMusicSlider) {
        double initialVolume = 0.5;

        ImageView sliderTrack = new ImageView(new Image("file:assets/images/slider_track.png"));
        sliderTrack.setFitWidth(SLIDER_WIDTH);
        sliderTrack.setFitHeight(20);
        sliderTrack.setX(320);
        sliderTrack.setY(-154);

        ImageView sliderKnob = new ImageView(new Image("file:assets/images/slider_knob.png"));
        sliderKnob.setFitWidth(28);
        sliderKnob.setFitHeight(28);
        sliderKnob.setX(320 + initialVolume * SLIDER_WIDTH - sliderKnob.getFitWidth() / 2);
        sliderKnob.setY(-158);

        Pane sliderPane = new Pane();
        sliderPane.setPrefSize(SLIDER_WIDTH, 20);
        sliderPane.getChildren().addAll(sliderTrack, sliderKnob);

        sliderKnob.setTranslateX(initialVolume * SLIDER_WIDTH - SLIDER_WIDTH / 2);

        if (isMusicSlider) {
            musicSliderKnob = sliderKnob;
        } else {
            sfxSliderKnob = sliderKnob;
        }

        sliderKnob.setOnMousePressed(e -> {
            if (isMusicSlider) {
                musicSliderStartX = e.getSceneX();
            } else {
                sfxSliderStartX = e.getSceneX();
            }
        });

        sliderKnob.setOnMouseDragged(e -> {
            double startX = isMusicSlider ? musicSliderStartX : sfxSliderStartX;
            double deltaX = e.getSceneX() - startX;
            double currentX = sliderKnob.getTranslateX();
            double newX = currentX + deltaX;

            // Clamp to slider bounds
            double minX = -SLIDER_WIDTH / 2 + 10;
            double maxX = SLIDER_WIDTH / 2 - 10;
            newX = Math.max(minX, Math.min(maxX, newX));

            sliderKnob.setTranslateX(newX);

            // Update volume
            double volume = (newX + SLIDER_WIDTH / 2 - 10) / SLIDER_WIDTH;
            if (isMusicSlider) {
                audioManager.setMusicVolume(volume);
                musicSliderStartX = e.getSceneX();
            } else {
                audioManager.setSfxVolume(volume);
                sfxSliderStartX = e.getSceneX();
            }
        });

        sliderTrack.setOnMouseClicked(e -> {
            double clickX = e.getX();
            double newX = clickX - SLIDER_WIDTH / 2;

            double minX = -SLIDER_WIDTH / 2;
            double maxX = SLIDER_WIDTH / 2;
            newX = Math.max(minX, Math.min(maxX, newX));

            sliderKnob.setTranslateX(newX);

            double volume = (newX + SLIDER_WIDTH / 2) / SLIDER_WIDTH;
            if (isMusicSlider) {
                audioManager.setMusicVolume(volume);
            } else {
                audioManager.setSfxVolume(volume);
            }
        });

        return sliderPane;
    }

    // Phương thức trợ giúp tạo nút với hiệu ứng âm thanh
    private ImageView createButtonImageView(String imagePath, double width, double height) {
        ImageView button = new ImageView(new Image(imagePath));
        button.setFitWidth(width);
        button.setFitHeight(height);
        button.setCursor(Cursor.HAND);

        // Hiệu ứng di chuột
        button.setOnMouseEntered(e -> {
            button.setOpacity(0.7);
            audioManager.playButtonTapSound();
        });

        button.setOnMouseExited(e -> button.setOpacity(1.0));
        button.setOnMousePressed(e -> button.setOpacity(0.5));
        button.setOnMouseReleased(e -> button.setOpacity(0.7));

        return button;
    }

    private void togglePauseMenu() {
        if(game == null) {
            return;
        }

        if (game.isPaused()) {
            game.resume();
            pauseMenu.setVisible(false);
            timer.start();
        } else {
            game.pause();
            pauseMenu.setVisible(true);
            pauseMenu.toFront();
            timer.stop();
        }
    }

    private void toggleSettingMenu() {
        if (game == null) {
            if (settingMenu.isVisible()) {
                settingMenu.setVisible(false);
            } else {
                settingMenu.setVisible(true);
                settingMenu.toFront();
            }
        } else {
            if (settingMenu.isVisible()) {
                settingMenu.setVisible(false);
                if (game.isPaused()) {
                    game.resume();
                    timer.start();
                }
            } else {
                settingMenu.setVisible(true);
                settingMenu.toFront();
                if (!game.isPaused()) {
                    game.pause();
                    timer.stop();
                }
            }
        }
    }

    public void createStartMenu() {
        Image startMenuImg = new Image("file:assets/images/startScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(SCREEN_WIDTH);
        startView.setFitHeight(SCREEN_HEIGHT);

        // Start button
        ImageView startButton = createButtonImageView("file:assets/images/startButton.png", 200, 80);
        startButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            priStage.setScene(levelSelectionScene);
        });

        // Exit button
        ImageView exitButton = createButtonImageView("file:assets/images/exitButton.png", 200, 80);
        exitButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            System.exit(0);
        });

        // Setting button
        ImageView settingButton = createButtonImageView("file:assets/images/button_setting.png", 60, 60);
        settingButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            toggleSettingMenu();
        });

        StackPane root = new StackPane();
        root.getChildren().addAll(startView, startButton, exitButton, settingButton, settingMenu);

        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 210, 0));
        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(exitButton, new Insets(0, 0, 120, 0));
        StackPane.setAlignment(settingButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(settingButton, new Insets(0, 0, 50, 0));
        startMenuScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void createLevelSelectionScene() {
        Image startMenuImg = new Image("file:assets/images/levelSelectionScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(SCREEN_WIDTH);
        startView.setFitHeight(SCREEN_HEIGHT);

        // Level 1
        ImageView level0Button = createButtonImageView("file:assets/images/Level_0.png", 200, 80);
        level0Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(0);
        });

        // Level 2
        ImageView level1Button = createButtonImageView("file:assets/images/Level_1.png", 200, 80);
        level1Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(1);
        });

        // Level 3
        ImageView level2Button = createButtonImageView("file:assets/images/Level_2.png", 200, 80);
        level2Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(2);
        });

        // Back
        ImageView backButton = createButtonImageView("file:assets/images/mainMenuButton.png", 200, 80);
        backButton.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            priStage.setScene(startMenuScene);
        });

        VBox buttonLayout = new VBox(20);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(level0Button, level1Button, level2Button, backButton);

        StackPane root = new StackPane();
        root.getChildren().addAll(startView, buttonLayout);

        levelSelectionScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void returnToMenu() {
        if (timer != null) {
            timer.stop();
        }

        pauseMenu.setVisible(false);

        if (game != null && game.isPaused()) {
            game.resume();
        }

        AudioManager.stopBackgroundMusic();
        priStage.setScene(startMenuScene);
    }

    public void startGame(int levelNumber) {
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);

        showWinScreen(false, levelNumber);

        gameScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        game = new GameManager(gc, root, levelNumber);

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && !game.isWon()) {
                audioManager.playButtonClickSound();
                togglePauseMenu();
            } else {
                game.keyPressed(e);
            }
        });

        playAgainButton.setOnMouseClicked(e -> {
            showWinScreen(false, levelNumber);
            audioManager.playButtonClickSound();
            game.restart();
        });

        nextLevelButton.setOnMouseClicked(e -> {
            showWinScreen(false, levelNumber);
            audioManager.playButtonClickSound();
            game.nextLevel();
        });

        ImageView settingIcon = createButtonImageView("file:assets/images/button_setting.png", 36, 36);
        settingIcon.setLayoutX(GAME_AREA_WIDTH - 45);
        settingIcon.setLayoutY(10);
        settingIcon.setCursor(Cursor.HAND);
        settingIcon.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            toggleSettingMenu();
        });

        root.getChildren().addAll(settingIcon, settingMenu, pauseMenu, winOverlay, playAgainButton, nextLevelButton, menuButton, winText);

        gameScene.setOnKeyReleased(e -> game.keyReleased(e));

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.update(now);
                game.render(root);
            }
        };
        timer.start();

        priStage.setScene(gameScene);
    }

    public Scene getStartMenuScene() {
        return startMenuScene;
    }
}
