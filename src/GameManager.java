package src;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import src.object.Ball;
import src.object.Paddle;
import src.object.brick.Brick;
import src.object.brick.BrickFactory;

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

        createCheckerboardBackground(root);

        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 640, 120, 40, 6);
        ball = new Ball("file:assets/images/ball1.png", 280, 600, 18, 2, -2);
        
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                double x = i * (80);
                double y = j * (40);
                Brick newBrick;
                if (i % 2 == 0) {
                    newBrick = BrickFactory.createBrick("strong", x, y);
                } else {
                    newBrick = BrickFactory.createBrick("normal", x, y);
                }
                bricks.add(newBrick);
                root.getChildren().addAll(newBrick.getImageView());
            }
        }
        
        root.getChildren().addAll(text, paddle.getImageView(), ball.getImageView());
    }

    // Làm background hình ô cờ
    private void createCheckerboardBackground(Pane root) {

        Image rect1Img = new Image("file:assets/images/rectangle_1.png");
        Image rect2Img = new Image("file:assets/images/rectangle_2.png");
        
        double tileWidth = 40;
        double tileHeight = 40;
        int cols = (int) (Main.WIDTH/ tileWidth); 
        int rows = (int) (Main.HEIGHT / tileHeight); 
        
        for (int i = 0; i < cols; i++) { 
            for (int j = 0; j < rows; j++) { 
                double x = i * tileWidth;
                double y = j * tileHeight;
                
                ImageView tileView;
                
                // Logic bàn cờ
                if ((i + j) % 2 == 0) {
                    tileView = new ImageView(rect1Img);
                } else {
                    tileView = new ImageView(rect2Img);
                }
                
                tileView.setX(x);
                tileView.setY(y);
                tileView.setFitWidth(tileWidth);
                tileView.setFitHeight(tileHeight);
                
                root.getChildren().add(tileView);
                
                tileView.toBack(); 
            }
        }
    }

    public void render(Pane root) {
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
                if (brick.takeHit()) {
                    toRemove.add(brick);
                    score += 10;
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
        CollisionDetector.handlePaddleCollisionSimple(ball, paddle);
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