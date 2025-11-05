import enums.BrickType;
import enums.PowerUpType;
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

import static utils.Constants.*;

public class GameManager {
    private GraphicsContext gc;
    private Pane root;
    private Image backgroundImage;
    private static MediaPlayer backgroundMusic = null;
    private Media[] meowSounds;
    private Random random;
    private TextManager textManager;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    List<PowerUp> activePowerUps = new ArrayList<>();

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    private boolean isPaused = false;

    private boolean dynamicSpawning = false;
    private long lastSpawnTime = 0;
    private final double SPAWN_INTERVAL = 15;
    private boolean nextSpawnPattern = true;

    private final int brickWidth = 63;
    private final int brickHeight = 33;
    private final int padding = 5;
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
        if (ball != null) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
        }
        for (Brick brick : bricks) {
            root.getChildren().removeAll(brick.getImageView(), brick.getCollisionShape());
        }
        bricks.clear();

        for (PowerUp powerUp: powerUps) {
            root.getChildren().removeAll(powerUp.getCollisionShape(), powerUp.getImageView());
        }
        powerUps.clear();
        root.getChildren().remove(powerUps);
        if (textManager != null) {
            textManager.removeText(root);
        }

        backgroundImage = new Image("file:assets/images/background.png");

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

        textManager = new TextManager(root);

        loadLevel(this.levelNumber);

        root.getChildren().addAll(
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
                        BrickType brickType = null;
                        switch (type) {
                            case "1": brickType = BrickType.NORMAL; break;
                            case "2": brickType = BrickType.STRONG; break;
                            case "3": brickType = BrickType.INDESTRUCTIBLE; break;
                        }

                        if (brickType != null) {
                            Brick newBrick = Brick.createBrick(brickType, currentX, currentY);
                            bricks.add(newBrick);
                            root.getChildren().addAll(
                                    newBrick.getImageView(),
                                    newBrick.getCollisionShape()
                            );
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
                    int brickCode = selectedMap[j][i];
                    BrickType brickType = null;

                    switch (brickCode) {
                        case 1: brickType = BrickType.NORMAL; break;
                        case 2: brickType = BrickType.STRONG; break;
                        case 3: brickType = BrickType.INDESTRUCTIBLE; break;
                    }

                    if (brickType != null) {
                        double x = startX + i * (brickWidth + padding);
                        double y = startY + j * (brickHeight + padding);

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
        for (int i = 0; i < 8; i++) {
            double x = startX + i * (brickWidth + padding);
            BrickType brickType;
            if (strongFirst) {
                brickType = (i % 2 == 0) ? BrickType.STRONG : BrickType.NORMAL;
            } else {
                brickType = (i % 2 == 0) ? BrickType.NORMAL : BrickType.STRONG;
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
        gc.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        List<Brick> brickToRemove = new ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && !brick.isBeingHit() && CollisionDetector.handleCollision(ball, brick)) {
                score += SCORE * SCORE_MULTIPLIER;
                brick.takeHit(() -> {
                    if (Math.random() < 0.5) {
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
                break;
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
                    if (powerUp.getType() == PowerUpType.EXPAND_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == PowerUpType.SHRINK_PADDLE);
                    }
                    if (powerUp.getType() == PowerUpType.SHRINK_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == PowerUpType.EXPAND_PADDLE);
                    }

                    // Kiểm tra xem đã có power up cùng loại đang active chưa
                    PowerUp existingPowerUp = null;
                    for (PowerUp active : activePowerUps) {
                        if (active.getType() == powerUp.getType()) {
                            existingPowerUp = active;
                            break;
                        }
                    }

                    if (existingPowerUp != null) {
                        // Đã có power up cùng loại -> reset thời gian
                        System.out.println("Reset activation time!");
                        existingPowerUp.setActivationTime(System.currentTimeMillis());
                    } else {
                        // Chưa có -> thêm mới
                        powerUp.collect(paddle, ball);
                        activePowerUps.add(powerUp);
                    }
                } else {
                    // Instant power up (duration = 0)
                    powerUp.collect(paddle, ball);
                }
                powerUpToRemove.add(powerUp);
            }

            if (powerUp.getY() > SCREEN_HEIGHT) {
                powerUpToRemove.add(powerUp);
            }
        }

        // Kiểm tra power up hết hạn
        List<PowerUp> expiredPowerUps = new ArrayList<>();
        for (PowerUp active : activePowerUps) {
            if (active.isExpired()) {
                active.deactivate(paddle, ball);
                expiredPowerUps.add(active);
            }
        }
        activePowerUps.removeAll(expiredPowerUps);

        // Xóa power up đã collect hoặc rơi ra ngoài màn hình
        for (PowerUp powerUp : powerUpToRemove) {
            root.getChildren().remove(powerUp.getImageView());
            root.getChildren().remove(powerUp.getCollisionShape());
            powerUps.remove(powerUp);
        }
    }



    public void update(long now) {
        if (!running || isPaused) return;

        System.out.println(ball.getVx() + " " + ball.getVy());

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
            ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getRadius());
            ball.setY(paddle.getY() - ball.getRadius() * 2); // Dock ball to paddle
        } else {
            showLaunchText = false; // ẩn đi
        }

        ball.update();

        // Ball rơi xuống đáy -> mất một mạng
        if (ball.isOutOfBounds()) {
            System.out.println("Ball fell out!");
            paddle.loseLife();
            System.out.println("Lives left: " + paddle.getLives());
            if (paddle.getLives() > 0) {
                ball.reset(paddle.getX() + paddle.getWidth() / 2 - ball.getRadius(),
                        paddle.getY() - ball.getRadius() * 2);
            }
        }

        if (paddle.isOutOfLives()) {
            System.out.println("Game over!");
            gameOver();
        }

        textManager.updateScoreAndLives(score, paddle);
        textManager.showLaunchHint(showLaunchText);

        // Kiểm tra va chạm
        CollisionDetector.handlePaddleCollision(ball, paddle);
    }



    void keyPressed(KeyEvent e) {
        if (isPaused) return;

        if (!running && e.getCode() == KeyCode.R) {
            restart();
            textManager.showGameOver(false);
        }

        if (e.getCode() == KeyCode.ESCAPE) {
            return;
        }

        if (e.getCode() == KeyCode.SPACE && !ball.isLaunched()) {
            ball.launch();
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
        textManager.showGameOver(false);
        paddle.reset();

        if (dynamicSpawning) {
            dynamicSpawning = false;
        }

        init();
    }



    private void gameOver() {
        running = false;
        stopBackgroundMusic();
        ball.notLaunch();
        textManager.showGameOver(true);
    }



    public boolean isPaused() {
        return isPaused;
    }



    public void pause() {
        isPaused = true;
        if(backgroundMusic != null) backgroundMusic.pause();
    }



    public void resume() {
        isPaused = false;
        if(backgroundMusic != null) backgroundMusic.play();
    }
}