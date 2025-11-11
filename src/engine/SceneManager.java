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
    private Scene levelSelectionScene;
    private AnimationTimer timer;
    private GameManager game;

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

    public SceneManager(Stage priStage) {
        this.priStage = priStage;
        this.customFont = TextManager.loadFont();
        createPauseMenu();
        createVolumeButton();
        createWinScreenElements();
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

        volumeIcon.setOnMouseClicked(e -> {
            toggleMute();
            updateVolumeIcon();
        });

        volumeIcon.setOnScroll(e -> {
//            System.out.println("running");
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
        ImageView resumeButton = new ImageView(new Image("file:assets/images/resumeButton.png"));
        resumeButton.setFitWidth(BUTTON_WIDTH);
        resumeButton.setFitHeight(BUTTON_HEIGHT);
        resumeButton.setOnMouseEntered(e -> resumeButton.setOpacity(0.7));
        resumeButton.setOnMouseExited(e -> resumeButton.setOpacity(1.0));
        resumeButton.setOnMouseClicked(e -> togglePauseMenu());

        // Restart button
        ImageView restartButton = new ImageView(new Image("file:assets/images/restartButton.png"));
        restartButton.setFitWidth(BUTTON_WIDTH);
        restartButton.setFitHeight(BUTTON_HEIGHT);
        restartButton.setOnMouseEntered(e -> restartButton.setOpacity(0.7));
        restartButton.setOnMouseExited(e -> restartButton.setOpacity(1.0));
        restartButton.setOnMouseClicked(e -> {
            togglePauseMenu();
            game.restart();
        });

        // To main menu button
        ImageView menuButton = new ImageView(new Image("file:assets/images/mainMenuButton.png"));
        menuButton.setFitWidth(BUTTON_WIDTH);
        menuButton.setFitHeight(BUTTON_HEIGHT);
        menuButton.setOnMouseEntered(e -> menuButton.setOpacity(0.7));
        menuButton.setOnMouseExited(e -> menuButton.setOpacity(1.0));
        menuButton.setOnMouseClicked(e -> returnToMenu());

        // Pause menu layout
        pauseMenu = new VBox(20, resumeButton, restartButton, menuButton);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        pauseMenu.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        pauseMenu.setVisible(false);
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

        Image buttonImg = new Image("file:assets/images/startButton.png");
        ImageView startButton = new ImageView(buttonImg);
        startButton.setFitWidth(BUTTON_WIDTH);
        startButton.setFitHeight(BUTTON_HEIGHT);
        startButton.setOnMouseEntered(e -> startButton.setOpacity(0.9));
        startButton.setOnMouseExited(e -> startButton.setOpacity(1.0));
        startButton.setOnMouseClicked(e -> priStage.setScene(levelSelectionScene));

        Image buttonImgExit = new Image("file:assets/images/exitButton.png");
        ImageView exitButton = new ImageView(buttonImgExit);
        exitButton.setFitWidth(BUTTON_WIDTH);
        exitButton.setFitHeight(BUTTON_HEIGHT);
        exitButton.setOnMouseEntered(e -> exitButton.setOpacity(0.9));
        exitButton.setOnMouseExited(e -> exitButton.setOpacity(1.0));
        exitButton.setOnMouseClicked(e -> System.exit(0));

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

        //Lvl 1
        Image level0 = new Image("file:assets/images/Level_0.png");
        ImageView level0Button = new ImageView(level0);
        level0Button.setFitWidth(BUTTON_WIDTH);
        level0Button.setFitHeight(BUTTON_HEIGHT);
        level0Button.setOnMouseEntered(e -> level0Button.setOpacity(0.9));
        level0Button.setOnMouseExited(e -> level0Button.setOpacity(1.0));
        level0Button.setOnMouseClicked(e -> startGame(0));

        //Lvl 2
        Image level1 = new Image("file:assets/images/Level_1.png");
        ImageView level1Button = new ImageView(level1);
        level1Button.setFitWidth(BUTTON_WIDTH);
        level1Button.setFitHeight(BUTTON_HEIGHT);
        level1Button.setOnMouseEntered(e -> level1Button.setOpacity(0.9));
        level1Button.setOnMouseExited(e -> level1Button.setOpacity(1.0));
        level1Button.setOnMouseClicked(e -> startGame(1));

        //Lvl 3
        Image level2 = new Image("file:assets/images/Level_2.png");
        ImageView level2Button = new ImageView(level2);
        level2Button.setFitWidth(BUTTON_WIDTH);
        level2Button.setFitHeight(BUTTON_HEIGHT);
        level2Button.setOnMouseEntered(e -> level2Button.setOpacity(0.9));
        level2Button.setOnMouseExited(e -> level2Button.setOpacity(1.0));
        level2Button.setOnMouseClicked(e -> startGame(2));

        //Back
        Image back = new Image("file:assets/images/mainMenuButton.png");
        ImageView backButton = new ImageView(back);
        backButton.setFitWidth(BUTTON_WIDTH);
        backButton.setFitHeight(BUTTON_HEIGHT);
        backButton.setOnMouseEntered(e -> backButton.setOpacity(0.9));
        backButton.setOnMouseExited(e -> backButton.setOpacity(1.0));
        backButton.setOnMouseClicked(e -> priStage.setScene(startMenuScene));

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
        root.getChildren().addAll(volumeIcon, pauseMenu, winOverlay, playAgainButton, nextLevelButton, menuButton, winText);

        showWinScreen(false, levelNumber);

        gameScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        game = new GameManager(gc, root, levelNumber);

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && !game.isWon()) {
                togglePauseMenu();
            } else {
                game.keyPressed(e);
            }
        });

        playAgainButton.setOnMouseClicked(e -> {
            showWinScreen(false, levelNumber);
            game.restart();
        });

        nextLevelButton.setOnMouseClicked(e -> {
            showWinScreen(false, levelNumber);
            game.nextLevel();
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
