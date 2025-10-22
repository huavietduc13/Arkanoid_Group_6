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
import static utils.Constants.*;

public class GameManager {
    private GraphicsContext gc;
    private Pane root;
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
    public Text launchText = new Text();

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
        root.getChildren().remove(launchText);

        backgroundImage = new Image("file:assets/images/background_1.png");

        this.random = new Random();
        this.meowSounds = new Media[NUMBER_OF_RANDOM_SOUND];

        // Nhạc nền
        java.io.File musicFile = new java.io.File("assets/sounds/gamePlay.mp3");
        String musicPath = musicFile.toURI().toString();

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

        paddle = new Paddle("file:assets/images/paddle1.png",
                PADDLE_BOUNDARY,
                PADDLE_POS_X,
                PADDLE_POS_Y,
                PADDLE_WIDTH,
                PADDLE_HEIGHT,
                PADDLE_SPEED);
        ball = new Ball("file:assets/images/ball1.png",
                BALL_POS_X,
                BALL_POS_Y,
                BALL_RADIUS,
                BALL_VX,
                BALL_VY);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                double x = 36 + i * (BRICK_WIDTH + BRICK_GAP);
                double y = 36 + j * (BRICK_HEIGHT + BRICK_GAP);
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
                launchText,
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
        gc.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        gc.setFont(Font.font("Times New Roman", TEXT_SIZE));
        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score, SCORE_POS_X, SCORE_POS_Y);
        gc.fillText("Lives: " + paddle.getLives(), LIVES_POS_X, LIVES_POS_Y);

        launchText.setText("Press 'SPACE' to launch the ball!");
        launchText.setX(LAUNCH_TEXT_POS_X);
        launchText.setY(LAUNCH_TEXT_POS_Y);
        launchText.setFont(Font.font("Times New Roman", TEXT_SIZE));
        launchText.setFill(Color.BLACK);
        if (showLaunchText) {
            launchText.setVisible(true);
        } else {
            launchText.setVisible(false);
        }

        List<Brick> toRemove = new ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && CollisionDetector.handleCollision(ball, brick)) {
                score += SCORE;
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
            gc.fillText("GAME OVER - Press R to Restart",
                    GAME_OVER_POS_X,
                    GAME_OVER_POS_Y);
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

        // ball rơi xuống đáy -> gameover
        if (ball.isOutOfBounds()) {
            System.out.println("Ball fell out!");
            paddle.loseLife();
            System.out.println("Lives left: " + paddle.getLives());
            if (paddle.getLives() > 0) {
                ball.reset(paddle.getX() + 40, paddle.getY() - 36);
            }
        }

        if (paddle.isOutOfLives()) {
            System.out.println("Game over!");
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
            launchText.setVisible(false);
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
        stopBackgroundMusic();
        ball.notLaunch();
    }

    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
    public List<Brick> getBricks() { return bricks; }
}
