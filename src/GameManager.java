package src;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.media.Media; 
import javafx.scene.media.MediaPlayer; 
import src.object.Ball;
import src.object.Paddle;
import src.object.brick.Brick;
import src.object.brick.BrickFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private GraphicsContext gc;
    private MediaPlayer backgroundMusic;
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
        this.random = new Random();
        this.meowSounds = new Media[3]; 

        ImageView backgroundView = new ImageView(new Image("file:assets/images/background_1.png"));
        backgroundView.setFitWidth(Main.WIDTH);
        backgroundView.setFitHeight(Main.HEIGHT);
        root.getChildren().add(backgroundView);
        backgroundView.toBack();
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

        paddle = new Paddle("file:assets/images/paddle1.png", 480, 240, 640, 120, 40, 6);
        ball = new Ball("file:assets/images/ball1.png", 280, 600, 18, 2, -2);
        
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                double x = i * BrickFactory.BRICK_WIDTH;
                double y = j * BrickFactory.BRICK_HEIGHT;
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
        if (!running) return;

        paddle.update();

        if (!ball.isLaunched()) {
            ball.setX(paddle.getX() + 40);
            ball.setY(paddle.getY() - 36);
        } else {
            showLaunchText = false; 
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