package src;

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
import src.object.Ball;
import src.object.Paddle;
import src.object.brick.Brick;
import src.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    public Text text = new Text();

    private boolean dynamicSpawning = false;
    private long lastSpawnTime = 0;
    private final double SPAWN_INTERVAL = 30.0;
    private boolean nextSpawnPattern = true;

    private final int brickWidth = Brick.BRICK_WIDTH;
    private final int brickHeight = Brick.BRICK_HEIGHT;
    private final int padding = 0;
    private final int startX = 0;
    private final int startY = 50;


    public GameManager(GraphicsContext gc, Pane root, int levelNumber) {
        this.gc = gc;
        this.root = root;
        this.levelNumber = levelNumber;
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

        backgroundImage = new Image("file:assets/images/background.png");

        this.random = new Random();
        this.meowSounds = new Media[3];

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

        paddle = new Paddle("file:assets/images/paddle_left.png", 480, 240, 760, 120, 36, 6);
        ball = new Ball("file:assets/images/ball.png", 280, 724, 18, 2, -2);

        loadLevel(this.levelNumber);

        root.getChildren().addAll(
                text,
                paddle.getImageView(), paddle.getCollisionShape(),
                ball.getImageView(), ball.getCollisionShape()
        );
    }

    private void loadLevel(int levelNumber) {

        int[][] selectedMap = null;
        this.dynamicSpawning = false;

        if (levelNumber == 0) {
            System.out.println("Đang tải Level 0 (Dynamic Spawning)");
            this.dynamicSpawning = true;
            this.lastSpawnTime = 0;
            this.nextSpawnPattern = true;

            addNewRowAtTop(this.nextSpawnPattern);
            this.nextSpawnPattern = !this.nextSpawnPattern;

            return;

        } else if (levelNumber == 1) {
            System.out.println("Đang tải Level 1 (Hard-code map xen kẽ)");
            selectedMap = new int[][] {
                    {2, 1, 2, 1, 2, 1, 2, 1},
                    {2, 1, 2, 1, 2, 1, 2, 1},
                    {2, 1, 2, 1, 2, 1, 2, 1},
                    {2, 1, 2, 1, 2, 1, 2, 1},
                    {2, 1, 2, 1, 2, 1, 2, 1},
                    {2, 1, 2, 1, 2, 1, 2, 1}
            };

        } else if (levelNumber == 2) {
            System.out.println("Đang tải Level 2 (Hard-code map chữ A)");
            selectedMap = new int[][] {
                    {0, 0, 2, 2, 2, 2, 0, 0},
                    {0, 0, 2, 2, 2, 2, 0, 0},
                    {0, 2, 2, 0, 0, 2, 2, 0},
                    {0, 2, 2, 0, 0, 2, 2, 0},
                    {0, 2, 2, 2, 2, 2, 2, 0},
                    {0, 2, 2, 2, 2, 2, 2, 0},
                    {0, 2, 2, 0, 0, 2, 2, 0},
                    {0, 2, 2, 0, 0, 2, 2, 0}
            };

        } else {
            String levelFile = "assets/levels/level_" + levelNumber + ".txt";
            System.out.println("Đang tải Level " + levelNumber + " từ file: " + levelFile);

            try (Scanner scanner = new Scanner(new File(levelFile))) {
                int currentY = startY;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] brickTypes = line.split(" ");
                    int currentX = startX;
                    for (String type : brickTypes) {
                        String brickTypeStr = "empty";
                        switch (type) {
                            case "1": brickTypeStr = "normal"; break;
                            case "2": brickTypeStr = "strong"; break;
                            case "3": brickTypeStr = "indestructible"; break;
                        }
                        if (!brickTypeStr.equals("empty")) {
                            Brick newBrick = Brick.createBrick(brickTypeStr, currentX, currentY);
                            bricks.add(newBrick);
                            root.getChildren().addAll(newBrick.getImageView(), newBrick.getCollisionShape());
                        }
                        currentX += brickWidth + padding;
                    }
                    currentY += brickHeight + padding;
                }
            } catch (FileNotFoundException e) {
                System.err.println("Không tìm thấy file màn chơi: " + levelFile);
                e.printStackTrace();
            }
            return;
        }

        if (selectedMap != null) {
            for (int j = 0; j < selectedMap.length; j++) {
                for (int i = 0; i < selectedMap[j].length; i++) {

                    int brickType = selectedMap[j][i];
                    String brickTypeStr = "empty";

                    switch (brickType) {
                        case 1: brickTypeStr = "normal"; break;
                        case 2: brickTypeStr = "strong"; break;
                        case 3: brickTypeStr = "indestructible"; break;
                    }

                    if (!brickTypeStr.equals("empty")) {
                        double x = startX + i * (brickWidth);
                        double y = startY + j * (brickHeight);

                        Brick newBrick = Brick.createBrick(brickTypeStr, x, y);
                        bricks.add(newBrick);
                        root.getChildren().addAll(newBrick.getImageView(), newBrick.getCollisionShape());
                    }
                }
            }
        }
    }

    private void moveAllBricksDown() {
        System.out.println("Đang đẩy gạch xuống...");
        double paddleTopY = paddle.getY();

        for (Brick brick : bricks) {
            double newY = brick.getY() + (brickHeight + padding);

            if (newY + brickHeight > paddleTopY) {
                System.out.println("Gạch đã chạm tới người chơi! Game Over.");
                gameOver();
                return;
            }

            brick.setY(newY);
        }
    }

    private void addNewRowAtTop(boolean strongFirst) {
        System.out.println("Đang sinh hàng gạch mới ở trên cùng");
        double y = startY;
        for (int i = 0; i < 7; i++) {
            double x = startX + i * (brickWidth);
            String brickType;
            if (strongFirst) {
                brickType = (i % 2 == 0) ? "strong" : "normal";
            } else {
                brickType = (i % 2 == 0) ? "normal" : "strong";
            }

            Brick newBrick = Brick.createBrick(brickType, x, y);
            bricks.add(newBrick);
            root.getChildren().addAll(newBrick.getImageView(), newBrick.getCollisionShape());
        }
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
        gc.drawImage(backgroundImage, 0, 0, Main.WIDTH, Main.HEIGHT);

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

    public void update(long now) {
        if (!running) return;

        if (dynamicSpawning) {
            if (lastSpawnTime == 0) {
                lastSpawnTime = now;
            }

            double elapsedTime = (now - lastSpawnTime) / 1_000_000_000.0;

            if (elapsedTime >= SPAWN_INTERVAL) {
                moveAllBricksDown();

                if (running) {
                    addNewRowAtTop(nextSpawnPattern);
                    nextSpawnPattern = !nextSpawnPattern;
                }

                lastSpawnTime = now;
            }
        }

        paddle.update();

        if (!ball.isLaunched()) {
            ball.setX(paddle.getX() + 40);
            ball.setY(paddle.getY() - 36);
        } else {
            showLaunchText = false; 
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

        if (dynamicSpawning) {
            dynamicSpawning = false;
        }

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