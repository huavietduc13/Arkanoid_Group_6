import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import object.Ball;
import object.Paddle;
import static utils.Constants.*;

public class SceneManager {

    private Stage priStage;
    private Scene startMenuScene;
    private Scene gameScene;
    private VBox pauseMenu;
    private Scene levelSelectionScene;
    private AnimationTimer timer;
    private GameManager game;

    private Paddle paddle;
    private Ball ball;



    SceneManager(Stage priStage) {
        this.priStage = priStage;
        createPauseMenu();
    }


    public void createPauseMenu() {
        // Resume button
        ImageView resumeButton = new ImageView(new Image("file:assets/images/resumeButton.png"));
        resumeButton.setFitWidth(200);
        resumeButton.setFitHeight(80);
        resumeButton.setOnMouseEntered(e -> resumeButton.setOpacity(0.7));
        resumeButton.setOnMouseExited(e -> resumeButton.setOpacity(1.0));
        resumeButton.setOnMouseClicked(e -> togglePauseMenu());

        // To main menu button
        ImageView menuButton = new ImageView(new Image("file:assets/images/mainMenuButton.png"));
        menuButton.setFitWidth(200);
        menuButton.setFitHeight(80);
        menuButton.setOnMouseEntered(e -> menuButton.setOpacity(0.7));
        menuButton.setOnMouseExited(e -> menuButton.setOpacity(1.0));
        menuButton.setOnMouseClicked(e -> returnToMenu());

        // Pause menu layout
        pauseMenu = new VBox(20, resumeButton, menuButton);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPrefSize(SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
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
        startButton.setFitWidth(200);
        startButton.setFitHeight(80);
        startButton.setOnMouseEntered(e -> startButton.setOpacity(0.9));
        startButton.setOnMouseExited(e -> startButton.setOpacity(1.0));
        startButton.setOnMouseClicked(e -> priStage.setScene(levelSelectionScene));

        Image buttonImgExit = new Image("file:assets/images/exitButton.png");
        ImageView exitButton = new ImageView(buttonImgExit);
        exitButton.setFitWidth(200);
        exitButton.setFitHeight(80);
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
        Image startMenuImg = new Image("file:assets/images/startScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(SCREEN_WIDTH);
        startView.setFitHeight(SCREEN_HEIGHT);

        //Lvl 1
        Image level0 = new Image("file:assets/images/Level_0.png");
        ImageView level0Button = new ImageView(level0);
        level0Button.setFitWidth(200);
        level0Button.setFitHeight(80);
        level0Button.setOnMouseEntered(e -> level0Button.setOpacity(0.9));
        level0Button.setOnMouseExited(e -> level0Button.setOpacity(1.0));
        level0Button.setOnMouseClicked(e -> startGame(0));

        //Lvl 2
        Image level1 = new Image("file:assets/images/Level_1.png");
        ImageView level1Button = new ImageView(level1);
        level1Button.setFitWidth(200);
        level1Button.setFitHeight(80);
        level1Button.setOnMouseEntered(e -> level1Button.setOpacity(0.9));
        level1Button.setOnMouseExited(e -> level1Button.setOpacity(1.0));
        level1Button.setOnMouseClicked(e -> startGame(1));

        //Lvl 3
        Image level2 = new Image("file:assets/images/Level_2.png");
        ImageView level2Button = new ImageView(level2);
        level2Button.setFitWidth(200);
        level2Button.setFitHeight(80);
        level2Button.setOnMouseEntered(e -> level2Button.setOpacity(0.9));
        level2Button.setOnMouseExited(e -> level2Button.setOpacity(1.0));
        level2Button.setOnMouseClicked(e -> startGame(2));

        //Back
        Image back = new Image("file:assets/images/mainMenuButton.png");
        ImageView backButton = new ImageView(back);
        backButton.setFitWidth(200);
        backButton.setFitHeight(80);
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

        GameManager.stopBackgroundMusic();
        priStage.setScene(startMenuScene);
    }



    public void startGame(int levelNumber) {
        Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        root.getChildren().add(pauseMenu);

        gameScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);

        game = new GameManager(gc, root, levelNumber);

        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
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
