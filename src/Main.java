import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import object.Ball;
import object.Paddle;

public class Main extends Application {
    private static final double SCREEN_WIDTH = 400;
    private static final double SCREEN_HEIGHT = 600;

    private Paddle paddle;
    private Ball ball;

    @Override
    public void start(Stage stage) {
        // Label
//        Label label = new Label("Hello, JavaFX!");

        // Layout
        Pane root = new Pane();
//        root.getChildren().add(label);

        // Scene
        Scene scene = new Scene(root, 400, 600, Color.BLACK);

        // Text
        Text text = new Text();
        text.setText("Press 'SPACE' to launch the ball!");
        text.setX(70);
        text.setY(280);
        text.setFont(Font.font("Times New Roman", 20));
        text.setFill(Color.WHITE);

        // Paddle
        paddle = new Paddle("file:assets/paddle.png", 320, 160, 576, 79, 20, 6);

        // Ball
        ball = new Ball("file:assets/ball.png", 190, 556, 10, 4, -4);

        // Root
        root.getChildren().add(text);
        root.getChildren().add(paddle.getImageView());
        root.getChildren().add(ball.getImageView());

        // Key events
        scene.setOnKeyPressed((KeyEvent e) -> paddle.handleKeyPressed(e.getCode()));
        scene.setOnKeyReleased((KeyEvent e) -> paddle.handleKeyReleased(e.getCode()));
        scene.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.SPACE && !ball.isLaunched()) {
                ball.launch();
                text.setVisible(false);
            }
            paddle.handleKeyPressed(e.getCode());
            paddle.handleKeyReleased(e.getCode());
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                paddle.update();

                if (!ball.isLaunched()) {
                    ball.setX(paddle.getX() + 30);
                    ball.setY(paddle.getY() - 20);
                }

                ball.update();

                if (ball.intersects(paddle)) {

                }
            }
        };
        timer.start();

        // Stage
        stage.setTitle("Ligma Balls");
        stage.getIcons().add(new Image("file:assets/image.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}