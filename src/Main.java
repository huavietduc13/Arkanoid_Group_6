package wgame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

public class Main extends Application {
    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private GameManager game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        //tạo menu scene
        MenuManager menu = new MenuManager(this);
        menuScene = menu.getScene();

        //Tạo game scene
        Canvas canvas = new Canvas(600, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        game = new GameManager(gc);
        StackPane gameRoot = new StackPane(canvas);
        gameScene = new Scene(gameRoot);

        stage.setTitle("Arkanoid");
        stage.setScene(menuScene);
        stage.show();

        gameScene.setOnKeyPressed(event -> game.keyPressed(event));
        gameScene.setOnKeyReleased(event -> game.keyReleased(event));
    }

    public void startGame() {
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.update();
                game.render();
            }
        };
        loop.start();
        stage.setScene(gameScene);
    }

    public void openMenu() {
        stage.setScene(menuScene);
    }

    public void exitGame() {
        stage.close();
    }
}
