package engine;

import effect.ParticleEngine;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.brick.ElectricBrick;
import object.brick.ExplodingBrick;
import object.powerup.Laser;
import object.powerup.PowerUp;
import object.powerup.Shield;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static enums.PowerUpType.*;
import static utils.Constants.*;

public class GameManager {
    private GraphicsContext gc;
    private Pane root;
    private Image backgroundImage;
    private AudioManager audioManager;
    private LevelManager levelManager;
    private Random random;
    private TextManager textManager;

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private List<PowerUp> activePowerUps = new ArrayList<>();
    private Shield shield;

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    private boolean isPaused = false;

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
        this.audioManager = new AudioManager();
        this.levelManager = new LevelManager();
        this.shield = new Shield();

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

        levelManager.clearBricks(root);

        for (PowerUp powerUp : powerUps) {
            root.getChildren().removeAll(powerUp.getImageView(), powerUp.getCollisionShape());
        }
        powerUps.clear();
        activePowerUps.clear();

        if (textManager != null) {
            textManager.removeText(root);
        }

        shield.deactivate();

        backgroundImage = new Image("file:assets/images/background.png");
        this.random = new Random();

        audioManager.playBackgroundMusic();

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
                mainBall.getImageView(), mainBall.getCollisionShape(),
                paddle.getGunLeftImageView(),
                paddle.getGunRightImageView()
        );

        redTrailEnabled = false;
        normalTrailEnabled = true;
        blueTrailEnabled = false;
    }

    private void loadLevel(int levelNumber) {
        levelManager.loadLevel(levelNumber);

        for (Brick brick : levelManager.getBricks()) {
            root.getChildren().addAll(brick.getImageView(), brick.getCollisionShape());
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

        List<Brick> bricksToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            for (Brick brick : levelManager.getBricks()) {
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

                        if (Math.random() < 0.3) { // 30% chance
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    brick.getCenterX(),
                                    brick.getY()
                            );
                            powerUps.add(powerUp);
                            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
                        }
                        bricksToRemove.add(brick);
                    });
                    audioManager.playRandomMeowSound();
                    break;
                }
            }
        }

        List<Laser> lasersToRemove = new ArrayList<>();
        for (Laser laser : paddle.getActiveLasers()) {
            if (!laser.isActive()) {
                lasersToRemove.add(laser);
                continue;
            }

            for (Brick brick : levelManager.getBricks()) {
                if (!brick.isDestroyed() && !brick.isBeingHit() && laser.intersects(brick)) {
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

                        if (Math.random() < 0.3) { // 30% chance
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    brick.getCenterX(),
                                    brick.getY()
                            );
                            powerUps.add(powerUp);
                            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
                        }
                        bricksToRemove.add(brick);
                    });

                    laser.deactivate();
                    lasersToRemove.add(laser);
                    effect.laserHit(laser.getX() + LASER_WIDTH / 2, brick.getY() + brick.getHeight());

                    audioManager.playRandomMeowSound();
                    break;
                }
            }
        }

        for (Laser laser : lasersToRemove) {
            root.getChildren().removeAll(laser.getImageView(), laser.getCollisionShape());
            paddle.getActiveLasers().remove(laser);
        }

        for (Brick brick : bricksToRemove) {
            root.getChildren().remove(brick.getImageView());
            root.getChildren().remove(brick.getCollisionShape());
            levelManager.removeBrick(brick);
        }

        List<PowerUp> powerUpsToRemove = new ArrayList<>();
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
                        if (powerUp.getType() == SHIELD) {
                            shield.activate();
                            effect.shieldActivate(SCREEN_WIDTH / 2, shield.getY());
                        }

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
                powerUpsToRemove.add(powerUp);
            }

            if (powerUp.getY() > SCREEN_HEIGHT) {
                powerUpsToRemove.add(powerUp);
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
                if (active.getType() == SHIELD) {
                    shield.deactivate();
                }

                for (Ball ball : balls) {
                    active.deactivate(paddle, ball);
                }
                expiredPowerUps.add(active);
            }
        }
        activePowerUps.removeAll(expiredPowerUps);

        // Xóa power up đã collect hoặc rơi ra ngoài màn hình
        for (PowerUp powerUp : powerUpsToRemove) {
            root.getChildren().remove(powerUp.getImageView());
            root.getChildren().remove(powerUp.getCollisionShape());
            powerUps.remove(powerUp);
        }

        shield.render(gc);

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

        levelManager.updateDynamicSpawning(now);

        shield.update(deltaTime);
        effect.update(deltaTime);
        paddle.update();
        effect.paddleTrail(paddle.getCenterX(), paddle.getCenterY());

        paddle.addLaserImage(root);

        Ball mainBall = balls.get(0);
        if (!mainBall.isLaunched()) {
            mainBall.setX(paddle.getCenterX() - mainBall.getRadius());
            mainBall.setY(paddle.getY() - mainBall.getRadius() * 2 + 4);
//            effect.fire(mainBall.getCenterX(), mainBall.getCenterY());
        } else {
            showLaunchText = false;
        }

        List<Ball> ballToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.update();

            if (shield.intersects(ball)) {
                shield.handleBallCollision(ball);
                effect.shieldDeflect(ball.getCenterX(), shield.getY());
            }

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
        List<Brick> diagonalBricks = electricBrick.getDiagonalBricks(levelManager.getBricks());
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
                        levelManager.removeBrick(targetBrick);

                        if (Math.random() < 0.1) { // 10% chance (lower chance for chain reaction)
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    targetBrick.getCenterX(),
                                    targetBrick.getY()
                            );
                            powerUps.add(powerUp);
                            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
                        }

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
        List<Brick> affectedBricks = explodingBrick.getBricksInExplosionRange(levelManager.getBricks());

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
                        effect.secondExplosion(targetBrick.getCenterX(), targetBrick.getCenterY());
                        root.getChildren().removeAll(targetBrick.getImageView(), targetBrick.getCollisionShape());
                        levelManager.removeBrick(targetBrick);

                        if (Math.random() < 0.1) { // 10% chance (lower chance for chain reaction)
                            System.out.println("Power up dropped!");
                            PowerUp powerUp = PowerUp.createRandomPowerUp(
                                    targetBrick.getCenterX(),
                                    targetBrick.getY()
                            );
                            powerUps.add(powerUp);
                            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
                        }

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

    public void restart() {
        score = 0;
        running = true;
        showLaunchText = true;
        textManager.showGameOver(false);
        paddle.reset();
        shield.deactivate();

        for (Ball ball : balls) {
            ball.notLaunch();
        }

        levelManager.setDynamicSpawning(false);

        for (PowerUp powerUp : powerUps) {
            root.getChildren().removeAll(powerUp.getImageView(), powerUp.getCollisionShape());
        }

        init();
    }

    private void gameOver() {
        running = false;
        audioManager.stopBackgroundMusic();
        textManager.showGameOver(true);
        shield.deactivate();

        for (Ball ball : balls) {
            ball.notLaunch();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pause() {
        isPaused = true;
        audioManager.pauseBackgroundMusic();
    }

    public void resume() {
        isPaused = false;
        audioManager.playBackgroundMusic();
    }
}
