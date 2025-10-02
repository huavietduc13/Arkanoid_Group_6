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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        GameManager game = new GameManager(gc);


        Scene scene = new Scene(new StackPane(canvas));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Arkanoid");
        primaryStage.show();

        scene.setOnKeyPressed(event->game.keyPressed(event));
        scene.setOnKeyReleased(event->game.keyReleased(event));

        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.update();
                game.render();
            }
        };
        loop.start();
    }
}
