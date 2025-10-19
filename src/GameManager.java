import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import object.Ball;
import object.Paddle;
import object.brick.Brick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private GraphicsContext gc;
    Pane root;
    private Image backgroundImage;
    private static MediaPlayer backgroundMusic = null;
    private Media[] meowSounds;
    private Random random;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();

    private int score = 0;
    private boolean running = true;
    private boolean showLaunchText = true;
    public Text text = new Text();

    public GameManager(GraphicsContext gc, Pane root) {
        this.gc = gc;
        this.root = root;
        init();
    }

    private void init() {
        // Xóa các đối tượng cũ nếu có
        if (paddle != null) {
            root.getChildren().removeAll(paddle.getImageView(), paddle.getCollisionShape());
        }
        if (ball != null) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
        }
        for (Brick brick : bricks) {
            root.getChildren().removeAll(brick.getImageView(), brick.getCollisionShape());
        }
        bricks.clear();
        root.getChildren().remove(text);

        backgroundImage = new Image("file:assets/images/background_1.png");

        this.random = new Random();
        this.meowSounds = new Media[3];

        // Nhạc nền
        java.io.File musicFile = new java.io.File("assets/sounds/gamePlay.mp3");
        String musicPath = musicFile.toURI().toString();

        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 760, 120, 36, 6);
        ball = new Ball("file:assets/images/ball1.png", 280, 724, 18, 5, -5);
        Media gameMusic = new Media(musicPath);
        backgroundMusic = new MediaPlayer(gameMusic);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.setVolume(0.5);
        backgroundMusic.play();

        for (int i = 0; i < 3; i++) {
            java.io.File meowFile = new java.io.File("assets/sounds/meow_" + (i + 1) + ".mp3");
            String meowPath = meowFile.toURI().toString();
            meowSounds[i] = new Media(meowPath);
        }

        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 755, 120, 40, 6);
        ball = new Ball("file:assets/images/ball1.png", 280, 724, 18, 2, -2);
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
        root.getChildren().addAll(
                text,
                paddle.getImageView(), paddle.getCollisionShape(),
                ball.getImageView(), ball.getCollisionShape()
        );
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    private void playRandomMeowSound() {
        if (meowSounds != null && meowSounds[0] != null) {
            try {
                int randomIndex = random.nextInt(3);
                MediaPlayer meowPlayer = new MediaPlayer(meowSounds[randomIndex]);
                meowPlayer.setVolume(0.4);
                meowPlayer.play();
            } catch (Exception e) {
                System.out.println("Lỗi khi phát meow: " + e.getMessage());
            }
        }
    }

    public void render(Pane root) {
        gc.drawImage(backgroundImage, 0, 0, 600, 800);

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
                playRandomMeowSound();
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

        // ball rơi xuống đáy -> gameover
        if (ball.getY() > 760) {
            gameOver();
        }

        // Kiểm tra va chạm
        CollisionDetector.handlePaddleCollision(ball, paddle);
    }

    public void keyPressed(KeyEvent e) {
        if (!running && e.getCode() == KeyCode.R) {
            restart();
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            return;
        }

        if (e.getCode() == KeyCode.SPACE && !ball.isLaunched()) {
            ball.launch();
            text.setVisible(false);
        } else {
            paddle.handleKeyPressed(e.getCode());
        }
    }

    public void keyReleased(KeyEvent e) {
        paddle.handleKeyReleased(e.getCode());
    }

    private void restart() {
        score = 0;
        running = true;
        showLaunchText = true;
        ball.notLaunch();
        init();
    }

    private void gameOver() {
        running = false;
        ball.notLaunch();
    }

    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
    public List<Brick> getBricks() { return bricks; }
}
