import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.brick.BrickFactory;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static final double SCREEN_WIDTH = 600;
    private static final double SCREEN_HEIGHT = 800;

    private Paddle paddle;
    private Ball ball;

    @Override
    public void start(Stage stage) {
        // Layout
        Pane root = new Pane();

        // Scene
        Scene scene = new Scene(root, 600, 800, Color.BLACK);

        // Text
        Text text = new Text();
        text.setText("Press 'SPACE' to launch the ball!");
        text.setX(165);
        text.setY(360);
        text.setFont(Font.font("Times New Roman", 20));
        text.setFill(Color.WHITE);

        // Paddle
        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 764, 120, 40, 6);

        // Ball
        ball = new Ball("file:assets/images/ball1.png", 280, 724 , 18, 5, -5 );

        // Brick
        List<Brick> bricks = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                double x = 50 + i * (63 + 5);
                double y = 50 + j * (33 + 5);
                Brick newBrick;
                if (i % 2 == 0) {
                    newBrick = BrickFactory.createBrick("strong", x, y);
                } else {
                    newBrick = BrickFactory.createBrick("normal", x, y);
                }
                bricks.add(newBrick);
                root.getChildren().add(newBrick.getImageView());
            }
        }

//        for (int i = 0; i < 8; i++) {
//            double x = 50 + i * (63 + 5);
//            Brick indestructibleBrick = BrickFactory.createBrick("indestructible", x, 50 + 33 + 5);
//            bricks.add(indestructibleBrick);
//            root.getChildren().add(indestructibleBrick.getImageView());
//        }

        // Root
        root.getChildren().add(text);
        root.getChildren().add(paddle.getImageView());
        root.getChildren().add(paddle.getCollisionShape());
        root.getChildren().add(ball.getImageView());
        root.getChildren().add(ball.getCollisionShape());

        for (Brick brick : bricks) {
            root.getChildren().add(brick.getCollisionShape());
        }

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
                    ball.setX(paddle.getX() + 40);
                    ball.setY(paddle.getY() - 36);
                }

                ball.update();

                CollisionDetector.handlePaddleCollisionSimple(ball, paddle);

                List<Brick> bricksToRemove = new ArrayList<>();
                for (Brick brick : bricks) {
                    if (!brick.isDestroyed() && CollisionDetector.handleCollision(ball, brick)) {
                        if (brick.takeHit()) {
                            bricksToRemove.add(brick);
                            // Cộng điểm cho người chơi
                        }

                        // Va chạm đã xảy ra, không cần kiểm tra với viên gạch khác
                        break;
                    }
                }

                // Xoá gạch
                for (Brick brick : bricksToRemove) {
                    root.getChildren().remove(brick.getImageView());
                    root.getChildren().remove(brick.getCollisionShape());
                    bricks.remove(brick);
                }
            }
        };
        timer.start();



        // Stage
        stage.setTitle("Ligma Balls");
        stage.getIcons().add(new Image("file:assets/images/image.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}