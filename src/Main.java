package src;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private Stage primaryStage;
    private Scene startMenuScene;
    private Scene gameScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        createStartMenu();
        primaryStage.setScene(startMenuScene);
        primaryStage.setTitle("Arkanoid");
        primaryStage.show();
    }
    
    private void createStartMenu() {
        Image startMenuImg = new Image("assets/startScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(WIDTH);
        startView.setFitHeight(HEIGHT);

        Image buttonImg = new Image("assets/button.png");
        ImageView startButton = new ImageView(buttonImg);
        startButton.setFitWidth(200);
        startButton.setFitHeight(80);

        startButton.setOnMouseEntered(e -> startButton.setOpacity(0.9));
        startButton.setOnMouseExited(e -> startButton.setOpacity(1.0));
        startButton.setOnMouseClicked(e -> startGame());

        StackPane root = new StackPane();
        root.getChildren().addAll(startView, startButton);

        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 50, 0));

        startMenuScene = new Scene(root, WIDTH, HEIGHT);
    }

    private void startGame() {
        if (gameScene == null) {
            Canvas canvas = new Canvas(WIDTH, HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            GameManager game = new GameManager(gc);
            
            StackPane gameRoot = new StackPane(canvas);
            gameScene = new Scene(gameRoot);
            
            gameScene.setOnKeyPressed(event -> game.keyPressed(event));
            gameScene.setOnKeyReleased(event -> game.keyReleased(event));
            
            AnimationTimer loop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    game.update();
                    game.render();
                }
            };
            loop.start();
        }
        primaryStage.setScene(gameScene);
    }
}
