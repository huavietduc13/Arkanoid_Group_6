import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.powerup.PowerUp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import enums.BrickType;

import static utils.Constants.*;
import static enums.BrickType.*;
import static enums.PowerUpType.*;

public class GameManager {
    private GraphicsContext gc;
    private Pane root;
    private Image backgroundImage;
    private static MediaPlayer backgroundMusic = null;
    private Media[] meowSounds;
    private Random random;

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<PowerUp> activePowerUps = new ArrayList<>();

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private TextManager textManager;
    private boolean showLaunchText = true;

    private boolean dynamicSpawning = false;
    private long lastSpawnTime = 0;
    private final double SPAWN_INTERVAL = 15;
    private boolean nextSpawnPattern = true;

    private final int startX = 50;
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

        for (Ball ball : balls) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
        }
        balls.clear();

        for (Brick brick : bricks) {
            root.getChildren().removeAll(brick.getImageView(), brick.getCollisionShape());
        }
        bricks.clear();

        for (PowerUp powerUp : powerUps) {
            root.getChildren().removeAll(powerUp.getImageView(), powerUp.getCollisionShape());
        }
        powerUps.clear();
        
        if (textManager != null) {
            textManager.removeText(root);
        }

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
        Ball mainBall = new Ball("file:assets/images/ball1.png",
                BALL_POS_X,
                BALL_POS_Y,
                BALL_RADIUS,
                BALL_VX,
                BALL_VY);
        balls.add(mainBall);

        textManager = new TextManager(root);

        loadLevel(this.levelNumber);

        root.getChildren().addAll(
                paddle.getImageView(), paddle.getCollisionShape(),
                mainBall.getImageView(), mainBall.getCollisionShape()
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
                        BrickType brickType = null;
                        switch (type) {
                            case "1": brickType = NORMAL; break;
                            case "2": brickType = STRONG; break;
                            case "3": brickType = INDESTRUCTIBLE; break;
                        }

                        if (brickType != null) {
                            Brick newBrick = Brick.createBrick(brickType, currentX, currentY);
                            bricks.add(newBrick);
                            root.getChildren().addAll(
                                    newBrick.getImageView(),
                                    newBrick.getCollisionShape()
                            );
                        }
                        currentX += BRICK_WIDTH + BRICK_PADDING;
                    }
                    currentY += BRICK_HEIGHT + BRICK_PADDING;
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
                    int brickCode = selectedMap[j][i];
                    BrickType brickType = null;

                    switch (brickCode) {
                        case 1: brickType = NORMAL; break;
                        case 2: brickType = STRONG; break;
                        case 3: brickType = INDESTRUCTIBLE; break;
                    }

                    if (brickType != null) {
                        double x = startX + i * (BRICK_WIDTH + BRICK_PADDING);
                        double y = startY + j * (BRICK_HEIGHT + BRICK_PADDING);

                        Brick newBrick = Brick.createBrick(brickType, x, y);
                        bricks.add(newBrick);
                        root.getChildren().addAll(
                                newBrick.getImageView(),
                                newBrick.getCollisionShape()
                        );
                    }
                }
            }
        }
    }

    private void moveAllBricksDown() {
        System.out.println("Đang đẩy gạch xuống...");
        double paddleTopY = paddle.getY();

        for (Brick brick : bricks) {
            double newY = brick.getY() + (BRICK_HEIGHT + BRICK_PADDING);

            if (newY + BRICK_HEIGHT > paddleTopY) {
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
        for (int i = 0; i < 8; i++) {
            double x = startX + i * (BRICK_WIDTH + BRICK_PADDING);
            BrickType brickType;
            if (strongFirst) {
                brickType = (i % 2 == 0) ? STRONG : NORMAL;
            } else {
                brickType = (i % 2 == 0) ? NORMAL : STRONG;
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

    private void createExtraBalls(Ball mainBall, int quantity) {
        double posX = mainBall.getX();
        double posY = mainBall.getY();
        double speed = Math.hypot(mainBall.getVx(), mainBall.getVy());

        for (int i = 0; i < quantity; i++) {
            double angle = Math.toRadians(-90 + (i + 1) * 30 - quantity * 15);
            double vx = speed * Math.cos(angle);
            double vy = speed * Math.sin(angle);

            Ball newBall = new Ball("file:assets/images/ball1.png",
                    posX,
                    posY,
                    BALL_RADIUS,
                    vx,
                    vy);
            newBall.launch();

            balls.add(newBall);
            root.getChildren().addAll(newBall.getImageView(), newBall.getCollisionShape());
        }
    }

    public void render(Pane root) {
        gc.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        List<Brick> brickToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && !brick.isBeingHit() && CollisionDetector.handleCollision(ball, brick)) {
                    score += SCORE * SCORE_MULTIPLIER;
                    brick.takeHit(() -> {
                        if (Math.random() < 0.5) { // 50% chance
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    brick.getX() + brick.getWidth() / 2,
                                    brick.getY()
                            );
                            powerUps.add(powerUp);
                            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
                        }
                        brickToRemove.add(brick);
                    });
                    playRandomMeowSound();
//                    break;
                }
            }
        }

        for (Brick brick : brickToRemove) {
            root.getChildren().remove(brick.getImageView());
            root.getChildren().remove(brick.getCollisionShape());
            bricks.remove(brick);
        }

        List<PowerUp> powerUpToRemove = new ArrayList<>();
        for (PowerUp powerUp : powerUps) {
            if (running) {
                powerUp.update();
            }

            if (powerUp.intersects(paddle)) {
                if (powerUp.getDuration() > 0) {
                    // Expand and shrink at the same time is not allowed
                    if (powerUp.getType() == EXPAND_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == SHRINK_PADDLE);
                    }
                    if (powerUp.getType() == SHRINK_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == EXPAND_PADDLE);
                    }

                    // Check if there is already an active power up of the same type
                    PowerUp existingPowerUp = null;
                    for (PowerUp active : activePowerUps) {
                        if (active.getType() == powerUp.getType()) {
                            existingPowerUp = active;
                            break;
                        }
                    }

                    if (existingPowerUp != null) {
                        // Reset activation time
                        System.out.println("Reset activation time!");
                        existingPowerUp.setActivationTime(System.currentTimeMillis());
                    } else {
                        // Collect new power up
                        powerUp.collect(paddle, balls.get(0));
                        activePowerUps.add(powerUp);
                    }
                } else {
                    // Instant power up (duration = 0)
                    if (powerUp.getType() == MULTI_BALL) {
                        Ball mainBall = balls.get(0);
                        if (mainBall.isLaunched()) {
                            createExtraBalls(mainBall, EXTRA_BALLS);
                        }
                    }
                    powerUp.collect(paddle, balls.get(0));
                }
                powerUpToRemove.add(powerUp);
            }

            if (powerUp.getY() > SCREEN_HEIGHT) {
                powerUpToRemove.add(powerUp);
            }
        }

        // Check expired power up
        List<PowerUp> expiredPowerUps = new ArrayList<>();
        for (PowerUp active : activePowerUps) {
            if (active.isExpired()) {
                active.deactivate(paddle, balls.get(0));
                expiredPowerUps.add(active);
            }
        }
        activePowerUps.removeAll(expiredPowerUps);

        // Remove collected or fell out of the screen
        for (PowerUp powerUp : powerUpToRemove) {
            root.getChildren().remove(powerUp.getImageView());
            root.getChildren().remove(powerUp.getCollisionShape());
            powerUps.remove(powerUp);
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

        Ball mainBall = balls.get(0);
        if (!mainBall.isLaunched()) {
            mainBall.setX(paddle.getX() + paddle.getWidth() / 2 - mainBall.getRadius());
            mainBall.setY(paddle.getY() - mainBall.getRadius() * 2); // Dock ball to paddle
        } else {
            showLaunchText = false; // Hide text
        }

        List<Ball> ballToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.update();
            if (ball.isOutOfBounds()) {
                ballToRemove.add(ball);
            }
            CollisionDetector.handlePaddleCollision(ball, paddle);
        }

        for (Ball ball : ballToRemove) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
            balls.remove(ball);
        }

        // All balls fell out -> lose one life
        if (balls.isEmpty()) {
            System.out.println("All balls fell out!");
            paddle.loseLife();
            System.out.println("Lives left: " + paddle.getLives());
            if (paddle.getLives() > 0) {
                Ball newMainBall = new Ball("file:assets/images/ball1.png",
                        paddle.getX() + paddle.getWidth() / 2 - BALL_RADIUS,
                        paddle.getY() - BALL_RADIUS * 2,
                        BALL_RADIUS,
                        BALL_VX,
                        BALL_VY);
                balls.add(newMainBall);
                root.getChildren().addAll(newMainBall.getImageView(), newMainBall.getCollisionShape());
                showLaunchText = true;
            }
        }

        if (paddle.isOutOfLives()) {
            System.out.println("Game over!");
            gameOver();
        }

        textManager.updateScoreAndLives(score, paddle);
        textManager.showLaunchHint(showLaunchText);
    }

    void keyPressed(KeyEvent e) {
        if (!running && e.getCode() == KeyCode.R) {
            restart();
            textManager.showGameOver(false);
        }
        if (e.getCode() == KeyCode.ESCAPE) {
            return;
        }

        if (e.getCode() == KeyCode.SPACE) {
            for (Ball ball : balls) {
                if (!ball.isLaunched()) {
                    ball.launch();
                }
            }
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
        textManager.showGameOver(false);
        paddle.reset();

        for (Ball ball : balls) {
            ball.notLaunch();
        }

        if (dynamicSpawning) {
            dynamicSpawning = false;
        }

        init();
    }

    private void gameOver() {
        running = false;
        stopBackgroundMusic();
        textManager.showGameOver(true);

        for (Ball ball : balls) {
            ball.notLaunch();
        }
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<Brick> getBricks() {
        return bricks;
    }
}
