import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.brick.NormalBrick;
import object.brick.StrongBrick;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private GraphicsContext gc;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();

    private int score = 0;
    private boolean running = true;
    private boolean showLaunchText = true;
    public Text text = new Text();

    public GameManager(GraphicsContext gc, Pane root) {
        this.gc = gc;

        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 760, 120, 36, 6);
        ball = new Ball("file:assets/images/ball1.png", 280, 724, 18, 5, -5);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                double x = 36 + i * (63 + 5);
                double y = 36 + j * (33 + 5);
                Brick newBrick;
                if (i % 2 == 0) {
                    newBrick = Brick.createBrick("strong", x, y);
                } else {
                    newBrick = Brick.createBrick("normal", x, y);
                }
                bricks.add(newBrick);
                root.getChildren().addAll(newBrick.getImageView(), newBrick.getCollisionShape());
            }
        }
        root.getChildren().addAll(text, paddle.getImageView(), paddle.getCollisionShape(), ball.getImageView(), ball.getCollisionShape());
    }

    public void render(Pane root) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 600, 800);

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);

        text.setText("Press 'SPACE' to launch the ball!");
        text.setX(165);
        text.setY(360);
        text.setFont(Font.font("Times New Roman", 20));
        text.setFill(Color.WHITE);
        if (showLaunchText) {
            text.setVisible(true);
        } else {
            text.setVisible(false);
        }

        List<Brick> toRemove = new ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && CollisionDetector.handleCollision(ball, brick)) {
                score += 10;
                if (brick.takeHit()) {
                    toRemove.add(brick);
                }
                break;
            }
        }
        for (Brick brick : toRemove) {
            root.getChildren().remove(brick.getImageView());
            root.getChildren().remove(brick.getCollisionShape());
            bricks.remove(brick);
        }

        if (!running) {
            gc.fillText("GAME OVER - Press R to Restart", 250, 300);
        }
    }

    public void update() {
        if (ball.outOfBounds) {
            running = false;
        }

        if (!running) return;

        paddle.update();

        if (!ball.isLaunched()) {
            ball.setX(paddle.getX() + 40);
            ball.setY(paddle.getY() - 36);
        } else {
            showLaunchText = false; // ẩn đi
        }

        ball.update();

        // Kiểm tra va chạm
        CollisionDetector.handlePaddleCollision(ball, paddle);
    }


    public void keyPressed(KeyEvent e) {
        if (!running && e.getCode().toString().equals("R")) {
            restart();
        }
    }

    public void keyReleased(KeyEvent e) {
        // TODO: paddle.keyReleased(e);
    }

    private void restart() {
        running = true;
        score = 0;
        /*
        ball.reset();
        paddle.reset();
        */
    }

    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
    public List<Brick> getBricks() { return bricks; }
}
