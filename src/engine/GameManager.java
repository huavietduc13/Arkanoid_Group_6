package engine;

import effect.ParticleEngine;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.brick.ElectricBrick;
import object.brick.ExplodingBrick;
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
    private TextManager textManager;

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<PowerUp> activePowerUps = new ArrayList<>();

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    private boolean isPaused = false;

    private boolean dynamicSpawning = false;
    private long lastSpawnTime = 0;
    private final double SPAWN_INTERVAL = 15;
    private boolean nextSpawnPattern = true;

    private final int startX = 50;
    private final int startY = 50;

    private ParticleEngine effect;
    private long lastFrameTime = 0;

    private boolean redTrailEnabled = false;
    private boolean normalTrailEnabled = true;
    private boolean blueTrailEnabled = false;

    public GameManager(GraphicsContext gc, Pane root, int levelNumber) {
        this.gc = gc;
        this.root = root;
        this.levelNumber = levelNumber;
        this.effect = new ParticleEngine(gc);
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
        activePowerUps.clear();

        if (textManager != null) {
            textManager.removeText(root);
        }

        backgroundImage = new Image("file:assets/images/background.png");

        this.random = new Random();
        this.meowSounds = new Media[NUMBER_OF_RANDOM_SOUND];

        // Nhạc nền
        File musicFile = new File("assets/sounds/gamePlay.mp3");
        String musicPath = musicFile.toURI().toString();

        Media gameMusic = new Media(musicPath);
        backgroundMusic = new MediaPlayer(gameMusic);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.setVolume(0.5);
        backgroundMusic.play();

        for (int i = 0; i < 3; i++) {
            File meowFile = new File("assets/sounds/meow_" + (i + 1) + ".mp3");
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

        redTrailEnabled = false;
        normalTrailEnabled = true;
        blueTrailEnabled = false;
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
                    {1, 1, 1, 1, 1, 1, 1, 1},
                    {2, 1, 1, 1, 1, 1, 1, 1},
                    {1, 5, 1, 1, 1, 1, 1, 2},
                    {1, 1, 1, 1, 1, 1, 5, 1},
                    {1, 1, 1, 2, 1, 1, 1, 1},
                    {1, 1, 1, 5, 4, 1, 1, 1}
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
                            case "4": brickType = ELECTRIC; break;
                            case "5": brickType = EXPLODING; break;
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
                        case 4: brickType = ELECTRIC; break;
                        case 5: brickType = EXPLODING; break;
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

            for (PowerUp powerUp : activePowerUps) {
                if (powerUp.getType() == SLOW_BALL || powerUp.getType() == FAST_BALL) {
                    powerUp.activate(paddle, newBall);
                }
            }

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
                    Color brickColor = brick.getColor();

                    final boolean isElectric = brick instanceof ElectricBrick;
                    final boolean isExploding = brick instanceof ExplodingBrick;

                    brick.takeHit(() -> {
                        effect.brickExplosion(
                                brick.getCenterX(),
                                brick.getCenterY(),
                                brickColor
                        );

                        if (isElectric) {
                            handleElectricBrickDestruction((ElectricBrick) brick);
                        }

                        if (isExploding) {
                            handleExplodingBrickDestruction((ExplodingBrick) brick);
                        }

                        if (Math.random() < 0.5) { // 50% chance
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    brick.getCenterX(),
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
        }

        for (Brick brick : brickToRemove) {
            root.getChildren().remove(brick.getImageView());
            root.getChildren().remove(brick.getCollisionShape());
            bricks.remove(brick);
        }

        List<PowerUp> powerUpToRemove = new ArrayList<>();
        for (PowerUp powerUp : powerUps) {
            if (running) {
                Color powerUpColor = powerUp.getColor();
                effect.powerUpTrail(powerUp.getCenterX(), powerUp.getCenterY(), powerUpColor);
                powerUp.update();
            }

            if (powerUp.intersects(paddle)) {
                Color powerUpColor = powerUp.getColor();
                effect.powerUpCollect(
                        powerUp.getCenterX(),
                        powerUp.getCenterY(),
                        powerUpColor
                );

                if (powerUp.getDuration() > 0) {
                    // Expand and Shrink at the same time is not allowed
                    if (powerUp.getType() == EXPAND_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == SHRINK_PADDLE);
                    }
                    if (powerUp.getType() == SHRINK_PADDLE) {
                        activePowerUps.removeIf(p -> p.getType() == EXPAND_PADDLE);
                    }

                    // Same with Fast and Slow
                    if (powerUp.getType() == FAST_BALL) {
                        PowerUp slowBall = null;
                        for (PowerUp active : activePowerUps) {
                            if (active.getType() == SLOW_BALL) {
                                slowBall = active;
                                break;
                            }
                        }
                        if (slowBall != null) {
                            for (Ball ball : balls) {
                                slowBall.deactivate(paddle, ball);
                            }
                            activePowerUps.remove(slowBall);
                        }
                    }
                    if (powerUp.getType() == SLOW_BALL) {
                        PowerUp fastBall = null;
                        for (PowerUp active : activePowerUps) {
                            if (active.getType() == FAST_BALL) {
                                fastBall = active;
                                break;
                            }
                        }
                        if (fastBall != null) {
                            for (Ball ball : balls) {
                                fastBall.deactivate(paddle, ball);
                            }
                            activePowerUps.remove(fastBall);
                        }
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
                        for (Ball ball : balls) {
                            powerUp.collect(paddle, ball);
                        }
                        activePowerUps.add(powerUp);
                    }
                } else {
                    // Instant power up (duration = 0)
                    if (powerUp.getType() == MULTI_BALL) {
                        Ball mainBall = balls.get(0);
                        if (mainBall.isLaunched()) {
                            createExtraBalls(mainBall, EXTRA_BALLS);
                        }
                    } else {
                        powerUp.collect(paddle, balls.get(0));
                    }
                }
                powerUpToRemove.add(powerUp);
            }

            if (powerUp.getY() > SCREEN_HEIGHT) {
                powerUpToRemove.add(powerUp);
            }
        }

        if (!activePowerUps.isEmpty()) {
            normalTrailEnabled = true;
            redTrailEnabled = false;
            blueTrailEnabled = false;

            boolean hasFastBall = false;
            boolean hasSlowBall = false;
            for (PowerUp powerUp : activePowerUps) {
                if (powerUp.getType() == FAST_BALL) {
                    hasFastBall = true;
                }
                if (powerUp.getType() == SLOW_BALL) {
                    hasSlowBall = true;
                }
            }
            if (hasFastBall) {
                redTrailEnabled = true;
                normalTrailEnabled = false;
                blueTrailEnabled = false;
            } else if (hasSlowBall) {
                blueTrailEnabled = true;
                redTrailEnabled = false;
                normalTrailEnabled = false;
            }
        }

        // Check expired power up
        List<PowerUp> expiredPowerUps = new ArrayList<>();
        for (PowerUp active : activePowerUps) {
            if (active.isExpired()) {
                if (active.getType() == FAST_BALL || active.getType() == SLOW_BALL) {
                    redTrailEnabled = false;
                    normalTrailEnabled = true;
                    blueTrailEnabled = false;
                }
                for (Ball ball : balls) {
                    active.deactivate(paddle, ball);
                }
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
        effect.render();
    }

    public void update(long now) {
        if (!running || isPaused) {
            return;
        }

        double deltaTime = 0.016; // ~60 FPS
        if (lastFrameTime != 0) {
            deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
        }
        lastFrameTime = now;

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

        effect.update(deltaTime);
        paddle.update();
        effect.paddleTrail(paddle.getCenterX(), paddle.getCenterY());

        Ball mainBall = balls.get(0);
        if (!mainBall.isLaunched()) {
            mainBall.setX(paddle.getCenterX() - mainBall.getRadius());
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

            if (ball.isLaunched()) {
                if (normalTrailEnabled) {
                    effect.ballTrail(ball.getCenterX(), ball.getCenterY());
                } else if (redTrailEnabled) {
                    effect.redBallTrail(ball.getCenterX(), ball.getCenterY());
                } else if (blueTrailEnabled) {
                    effect.blueBallTrail(ball.getCenterX(), ball.getCenterY());
                }
            }

            if (ball.hitLeftBound()) {
                effect.hitLeftBound(ball.getX(), ball.getCenterY());
            }
            if (ball.hitRightBound()) {
                effect.hitRightBound(ball.getX() + ball.getWidth(), ball.getCenterY());
            }
            if (ball.hitUpperBound()) {
                effect.hitUpperBound(ball.getCenterX(), ball.getY());
            }

            if (CollisionDetector.handlePaddleCollision(ball, paddle)) {
                effect.paddleHit(ball.getCenterX(), paddle.getY());
            }
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
                        paddle.getCenterX() - BALL_RADIUS,
                        paddle.getY() - BALL_RADIUS * 2,
                        BALL_RADIUS,
                        BALL_VX,
                        BALL_VY);

                for (PowerUp active : activePowerUps) {
                    if (active.getType() == FAST_BALL || active.getType() == SLOW_BALL) {
                        active.setExpired();
                    }
                }
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

    public void handleElectricBrickDestruction(ElectricBrick electricBrick) {
        List<Brick> diagonalBricks = electricBrick.getDiagonalBricks(bricks);

        double centerX = electricBrick.getCenterX();
        double centerY = electricBrick.getCenterY();

        effect.electricExplosion(centerX, centerY);

        effect.screenShake(root, 200, 5);

        for (int i = 0; i < diagonalBricks.size(); i++) {
            Brick targetBrick = diagonalBricks.get(i);
            if (targetBrick.isDestroyed() || targetBrick.isBeingHit()) {
                continue;
            }
            final int index = i;

            // Delay để tạo hiệu ứng lan truyền
            PauseTransition pause = new PauseTransition(Duration.millis(30 * i));
            pause.setOnFinished(event -> {
                if (!targetBrick.isDestroyed()) {
                    effect.lightningEffect(root, centerX, centerY, targetBrick.getCenterX(), targetBrick.getCenterY());

                    score += targetBrick.getScore() * SCORE_MULTIPLIER;

                    targetBrick.takeHit(() -> {
                        effect.electricExplosion(targetBrick.getCenterX(), targetBrick.getCenterY());

                        root.getChildren().removeAll(targetBrick.getImageView(), targetBrick.getCollisionShape());
                        bricks.remove(targetBrick);

                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction((ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction((ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }
    }

    public void handleExplodingBrickDestruction(ExplodingBrick explodingBrick) {
        double centerX = explodingBrick.getCenterX();
        double centerY = explodingBrick.getCenterY();

        List<Brick> affectedBricks = explodingBrick.getBricksInExplosionRange(bricks);

        effect.firstExplosion(centerX, centerY);

        effect.shockwave(root, centerX, centerY, 300);

        effect.screenShake(root, 200, 5);

        for (int i = 0; i < affectedBricks.size(); i++) {
            Brick targetBrick = affectedBricks.get(i);
            if (targetBrick.isDestroyed() || targetBrick.isBeingHit()) {
                continue;
            }

            double targetCenterX = targetBrick.getCenterX();
            double targetCenterY = targetBrick.getCenterY();
            double distance = Math.hypot(centerX - targetCenterX, centerY - targetCenterY);
            long delay = (long) (distance * 0.5);

            // Delay để tạo hiệu ứng lan truyền
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(event -> {
                if (!targetBrick.isDestroyed()) {
                    score += targetBrick.getScore() * SCORE_MULTIPLIER;
                    targetBrick.takeHit(() -> {
                        effect.secondaryExplosion(targetBrick.getCenterX(), targetBrick.getCenterY());
                        root.getChildren().removeAll(targetBrick.getImageView(), targetBrick.getCollisionShape());
                        bricks.remove(targetBrick);

                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction((ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction((ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }
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

        if (e.getCode() == KeyCode.F) {
            for (int i = 0; i < 5; i++) {
                double x = 100 + Math.random() * 400;
                double y = 100 + Math.random() * 200;
                effect.firework(x, y);
            }
        }

        if (e.getCode() == KeyCode.SHIFT) {
            redTrailEnabled = true;
            normalTrailEnabled = false;
            blueTrailEnabled = false;
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
        if (e.getCode() == KeyCode.SHIFT) {
            redTrailEnabled = false;
            normalTrailEnabled = true;
            blueTrailEnabled = false;
        }
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
        if (effect != null) {
            effect.clear();
        }

        for (Ball ball : balls) {
            ball.notLaunch();
        }
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
