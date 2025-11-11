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
import javafx.stage.Stage;

import static engine.AudioManager.*;
import static utils.Constants.*;

public class SceneManager {

    private Stage priStage;
    private Scene startMenuScene;
    private Scene gameScene;
    private VBox pauseMenu;
    private Scene levelSelectionScene;
    private AnimationTimer timer;
    private GameManager game;
    private AudioManager audioManager; // KHAI BÁO

    private HBox volumeBox;
    private ImageView volumeIcon;
    private Image volHigh;
    private Image volMedium;
    private Image volLow;
    private Image volMute;

    public SceneManager(Stage priStage) {
        this.priStage = priStage;
        this.audioManager = new AudioManager(); // KHỞI TẠO
        createPauseMenu();
        createVolumeButton();
    }

    public void createVolumeButton() {
        volHigh = new Image("file:assets/images/volume_high.png");
        volMedium = new Image("file:assets/images/volume_medium.png");
        volLow = new Image("file:assets/images/volume_low.png");
        volMute = new Image("file:assets/images/volume_mute.png");

        volumeIcon = new ImageView(volMedium);
        volumeIcon.setFitHeight(30);
        volumeIcon.setFitWidth(30);
        volumeIcon.setLayoutX(SCREEN_WIDTH - 30 - 20);
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
            System.out.println("running");
            if(e.getDeltaY() > 0) volume += VOLUME_STEP;
            else if (e.getDeltaY() < 0) volume -= VOLUME_STEP;
            volume = Math.max(0.0, Math.min(1.0, volume));
            setMasterVolume(volume);
            updateVolumeIcon();
        });
    }

    private void updateVolumeIcon() {
        if (volume == 0) volumeIcon.setImage(volMute);
        else if (volume <= 0.4) volumeIcon.setImage(volLow);
        else if (volume <= 0.8) volumeIcon.setImage(volMedium);
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
        if(game == null) return;

        if(game.isPaused()) {
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

        StackPane root = new StackPane();
        root.getChildren().addAll(startView, startButton, exitButton);

        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 140, 0));
        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(exitButton, new Insets(0, 0, 50, 0));
        startMenuScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void createLevelSelectionScene() {
        Image startMenuImg = new Image("file:assets/images/levelSelectionScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(SCREEN_WIDTH);
        startView.setFitHeight(SCREEN_HEIGHT);

    
        ImageView level0Button = createButtonImageView("file:assets/images/Level_0.png", 200, 80);
        level0Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(0);
        });

        //Lvl 2
        ImageView level1Button = createButtonImageView("file:assets/images/Level_1.png", 200, 80);
        level1Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(1);
        });

        //Lvl 3
        ImageView level2Button = createButtonImageView("file:assets/images/Level_2.png", 200, 80);
        level2Button.setOnMouseClicked(e -> {
            audioManager.playButtonClickSound();
            startGame(2);
        });

        //Back
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
        root.getChildren().addAll(volumeIcon, pauseMenu);

        gameScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        game = new GameManager(gc, root, levelNumber);

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                audioManager.playButtonClickSound(); // Âm thanh khi mở/đóng Pause Menu
                togglePauseMenu();
            } else {
                game.keyPressed(e);
            }
        });

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