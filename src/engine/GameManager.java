package engine;

import effect.ParticleEngine;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import object.Ball;
import object.Bomb;
import object.Boss;
import object.Paddle;
import object.brick.Brick;
import object.brick.IndestructibleBrick;
import object.powerup.ExtraLifePowerUp;
import object.powerup.Laser;
import object.powerup.PowerUp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static engine.SceneManager.showWinScreen;
import static utils.Constants.*;

public class GameManager {
    private GraphicsContext gc;
    private Pane root;
    private Image backgroundImage;

    private ParticleEngine effect;
    private AudioManager audioManager;
    private LevelManager levelManager;
    private TextManager textManager;
    private PowerUpManager powerUpManager;
    private CollisionManager collisionManager;
    private InfoPanel infoPanel;

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();
    private Boss boss = null;
    private Rectangle healthBarBackground;
    private Rectangle healthBarForeground;

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    private boolean isPaused = false;
    private boolean gameWon = false;

    private List<Bomb> bombs = new ArrayList<>();

    private long lastFrameTime = 0;

    private List<PowerUp> bossDroppedPowerUps = new ArrayList<>();

    public GameManager(GraphicsContext gc, Pane root, int levelNumber) {
        this.gc = gc;
        this.root = root;
        this.levelNumber = levelNumber;
        this.effect = new ParticleEngine(gc);
        this.audioManager = new AudioManager();
        this.levelManager = new LevelManager();
        this.powerUpManager = new PowerUpManager(effect, audioManager);
        this.collisionManager = new CollisionManager(effect, audioManager, levelManager, powerUpManager);
        this.infoPanel = new InfoPanel(gc);

        paddle = new Paddle("file:assets/images/paddle_right.png",
                PADDLE_BOUNDARY,
                PADDLE_POS_X,
                PADDLE_POS_Y,
                PADDLE_WIDTH,
                PADDLE_HEIGHT,
                PADDLE_SPEED);

        init();
    }

    public void init() {
        // Xóa các đối tượng cũ nếu có
        if (paddle != null) {
            root.getChildren().removeAll(paddle.getImageView(), paddle.getCollisionShape());

            for (Laser laser : paddle.getActiveLasers()) {
                root.getChildren().removeAll(laser.getImageView(), laser.getCollisionShape());
            }
            paddle.disableLaser();
        }

        for (Ball ball : balls) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
        }
        balls.clear();

        levelManager.clearBricks(root);
        powerUpManager.reset(root);

        if (boss != null) {
            root.getChildren().remove(boss.getImageView());
            boss = null;
            if (healthBarBackground != null) {
                root.getChildren().removeAll(healthBarBackground, healthBarForeground);
                healthBarBackground = null;
                healthBarForeground = null;
            }
        }

        if (textManager != null) {
            textManager.removeText(root);
        }


        backgroundImage = new Image("file:assets/images/background.png");

        audioManager.playBackgroundMusic();

        paddle = new Paddle("file:assets/images/paddle_right.png",
                PADDLE_BOUNDARY,
                PADDLE_POS_X,
                PADDLE_POS_Y,
                PADDLE_WIDTH,
                PADDLE_HEIGHT,
                PADDLE_SPEED);

        Ball mainBall = new Ball("file:assets/images/ball.png",
                BALL_POS_X,
                BALL_POS_Y,
                BALL_RADIUS,
                BALL_VX,
                BALL_VY);
        balls.add(mainBall);
        root.getChildren().addAll(mainBall.getImageView(), mainBall.getCollisionShape());

        textManager = new TextManager(root);

        loadLevel(this.levelNumber);

        if (this.levelNumber == 5) {
            boss = new Boss();

            healthBarBackground = new Rectangle(
                    BOSS_HEALTH_BAR_X, BOSS_HEALTH_BAR_Y,
                    BOSS_HEALTH_BAR_WIDTH, BOSS_HEALTH_BAR_HEIGHT
            );
            healthBarBackground.setFill(Color.BLACK);

            healthBarForeground = new Rectangle(
                    BOSS_HEALTH_BAR_X, BOSS_HEALTH_BAR_Y,
                    BOSS_HEALTH_BAR_WIDTH, BOSS_HEALTH_BAR_HEIGHT
            );
            healthBarForeground.setFill(Color.GREEN);

            root.getChildren().addAll(
                    boss.getImageView(),
                    healthBarBackground,
                    healthBarForeground
            );
        }

        if (!root.getChildren().contains(paddle.getImageView())) {
            root.getChildren().addAll(
                    paddle.getImageView(), paddle.getCollisionShape(),
                    paddle.getGunLeftImageView(),
                    paddle.getGunRightImageView()
            );
        }
    }

    private void loadLevel(int levelNumber) {
        levelManager.loadLevel(levelNumber);

        for (Brick brick : levelManager.getBricks()) {
            root.getChildren().addAll(brick.getImageView(), brick.getCollisionShape());
        }
    }

    public void render(Pane root) {
        if(gameWon) return;

        gc.drawImage(backgroundImage, 0, 0, GAME_AREA_WIDTH, SCREEN_HEIGHT);

        if (boss != null) {
            for (Laser laser : paddle.getActiveLasers()) {
                if (laser.isActive() && laser.intersects(boss)) {
                    laser.deactivate();
                    boss.takeHit();
                    score += SCORE;
                }
            }
        }

        collisionManager.handleBallBricksCollision(root, balls);
        collisionManager.handleLaserBricksCollision(root, paddle);

        score = collisionManager.getScore();

        powerUpManager.update(root, paddle, balls, running);
        powerUpManager.getShield().render(gc);
        effect.render();

        infoPanel.render(score, paddle, levelNumber,
                powerUpManager.getActivePowerUps(),
                0.016);
    }

    public void update(long now) {
        if (!running || isPaused || gameWon) {
            return;
        }

        double deltaTime = 0.016;
        if (lastFrameTime != 0) {
            deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
        }
        lastFrameTime = now;

        if (levelManager.updateDynamicSpawning(now)) {
            gameOver();
            return;
        }

        List<Brick> newBricks = levelManager.getJustSpawnedBricks();
        if (!newBricks.isEmpty()) {
            for (Brick brick : newBricks) {
                root.getChildren().addAll(brick.getImageView(), brick.getCollisionShape());
            }
        }

        powerUpManager.getShield().update(deltaTime);
        effect.update(deltaTime);
        paddle.update();
        effect.paddleTrail(paddle.getCenterX(), paddle.getCenterY());
        paddle.addLaserImage(root);

        if (boss != null) {
            updateBossHealthBar();

            if (boss.isDestroyed() && running) {
                for (int i = 0; i < 5; i++) {
                    double x = 100 + Math.random() * 400;
                    double y = 100 + Math.random() * 200;
                    effect.firework(x, y);
                }

                PauseTransition delay = new PauseTransition(Duration.seconds(2));

                delay.setOnFinished(event -> {
                    running = false;

                    AudioManager.stopBackgroundMusic();
                    winGame();

                    root.getChildren().remove(boss.getImageView());
                    if (healthBarBackground != null) {
                        root.getChildren().removeAll(healthBarBackground, healthBarForeground);
                    }
                });

                delay.play();

                return;
            }

            if (boss.canUseSkill()) {
                List<PowerUp> droppedItems = boss.useSkill();

                for (PowerUp pu : droppedItems) {
                    bossDroppedPowerUps.add(pu);
                    root.getChildren().addAll(pu.getImageView(), pu.getCollisionShape());
                }
            }
            double paddleCenterX = paddle.getX() + (paddle.getWidth() / 2);
            double paddleCenterY = paddle.getY() + (paddle.getHeight() / 2);

            Bomb newBomb = boss.updateAndAttack(paddleCenterX, paddleCenterY);

            if (newBomb != null) {
                bombs.add(newBomb);
                root.getChildren().add(newBomb.getImageView());
            }
        }

        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update();

            if (bomb.getCollisionShape().getBoundsInParent().intersects(paddle.getCollisionShape().getBoundsInParent())) {
                boss.playExplosionAnimation(root, bomb.getX() + (bomb.getWidth() / 2), bomb.getY() + (bomb.getHeight() / 2), 0.5);
                paddle.takeHit();

                root.getChildren().remove(bomb.getImageView());
                iterator.remove();
            }
            else if (bomb.getY() > SCREEN_HEIGHT) {

                root.getChildren().remove(bomb.getImageView());
                iterator.remove();
            }
        }

        Iterator<PowerUp> puIterator = bossDroppedPowerUps.iterator();
        while (puIterator.hasNext()) {
            PowerUp pu = puIterator.next();
            pu.update();

            if (pu.getCollisionShape().getBoundsInParent().intersects(paddle.getCollisionShape().getBoundsInParent())) {

                if (pu instanceof ExtraLifePowerUp) {
                    paddle.gainLife();
                }

                root.getChildren().removeAll(pu.getImageView(), pu.getCollisionShape());
                puIterator.remove();

            }
            else if (pu.getY() > SCREEN_HEIGHT) {
                root.getChildren().removeAll(pu.getImageView(), pu.getCollisionShape());
                puIterator.remove();
            }
        }

        dockBallToPaddle();
        updateBalls();

        if (balls.isEmpty()) {
            respawnMainBall();
        }
        if (paddle.isOutOfLives()) {
            System.out.println("Game over!");
            gameOver();
        }

//        textManager.updateScoreAndLives(score, paddle);
        textManager.showLaunchHint(showLaunchText);
        checkWinCondition();
    }

    private void checkWinCondition() {
        if (gameWon) return;

        if (levelNumber == 5) return;

        boolean allBricksDestroyed = true;
        for (Brick brick : levelManager.getBricks()) {
            if (!brick.isDestroyed() && !(brick instanceof IndestructibleBrick)) { // Unless indestrucibleBrick
                allBricksDestroyed = false;
                break;
            }
        }

        if (allBricksDestroyed && !levelManager.getBricks().isEmpty()) {
            winGame();
        }
    }

    private void winGame() {
        gameWon = true;
        running = false;
        audioManager.stopBackgroundMusic();
        showWinScreen(true, levelManager.getCurrentLevel());

        for (Ball ball : balls) {
            ball.notLaunch();
        }
    }

    public void nextLevel() {
        gameWon = false;
        running = true;
        score = 0;
        levelNumber++;
        collisionManager.resetScore();

        init();
    }

    private void updateBossHealthBar() {
        if (boss == null || healthBarForeground == null) return;

        double maxHealth = boss.getMaxHitPoints();
        double currentHealth = boss.getHitPoints();
        double healthPercentage = currentHealth / maxHealth;

        double newWidth = BOSS_HEALTH_BAR_WIDTH * healthPercentage;
        if (newWidth < 0) newWidth = 0;

        healthBarForeground.setWidth(newWidth);
    }

    private void dockBallToPaddle() {
        Ball mainBall = balls.get(0);
        if (!mainBall.isLaunched()) {
            mainBall.setX(paddle.getCenterX() - mainBall.getRadius());
            mainBall.setY(paddle.getY() - mainBall.getRadius() * 2 + 4);
        } else {
            showLaunchText = false;
        }
    }

    private void updateBalls() {
        List<Ball> ballsToRemove = new ArrayList<>();
        for (Ball ball : balls) {
            ball.update();

            if (boss != null) {
                if (CollisionDetector.handleBallBossCollision(ball, boss)) {
                    boss.playExplosionAnimation(root, ball.getCenterX(), ball.getCenterY(), 0.5);
                    boss.takeHit();
                    score += SCORE;
                }
            }

            if (powerUpManager.getShield().intersects(ball)) {
                powerUpManager.getShield().handleBallCollision(ball);
                effect.shieldDeflect(ball.getCenterX(), powerUpManager.getShield().getY());
            }

            if (ball.isOutOfBounds()) {
                ballsToRemove.add(ball);
            }

            if (ball.isLaunched()) {
                if (powerUpManager.isNormalTrailEnabled()) {
                    effect.ballTrail(ball.getCenterX(), ball.getCenterY());
                } else if (powerUpManager.isRedTrailEnabled()) {
                    effect.redBallTrail(ball.getCenterX(), ball.getCenterY());
                } else if (powerUpManager.isBlueTrailEnabled()) {
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
        }

        for (Ball ball : ballsToRemove) {
            root.getChildren().removeAll(ball.getImageView(), ball.getCollisionShape());
            balls.remove(ball);
        }

        collisionManager.handleBallPaddleCollision(paddle, balls);
    }

    private void respawnMainBall() {
        System.out.println("All balls fell out!");
        paddle.loseLife();
        System.out.println("Lives left: " + paddle.getLives());

        if (paddle.getLives() > 0) {
            Ball newMainBall = new Ball("file:assets/images/ball.png",
                    paddle.getCenterX() - BALL_RADIUS,
                    paddle.getY() - BALL_RADIUS * 2,
                    BALL_RADIUS,
                    BALL_VX,
                    BALL_VY);

            powerUpManager.expireSpeedPowerUps();

            balls.add(newMainBall);
            root.getChildren().addAll(newMainBall.getImageView(), newMainBall.getCollisionShape());
            showLaunchText = true;

            paddle.setPreGameInvincible(true);
        }
    }

    public void keyPressed(KeyEvent e) {
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
            powerUpManager.setRedTrailEnabled(true);
        }

        if (e.getCode() == KeyCode.SPACE) {
            if (boss != null && !boss.isActive()) {
                boss.activate();
            }

            boolean ballWasLaunched = false;

            for (Ball ball : balls) {
                if (!ball.isLaunched()) {
                    ball.launch();
                    ballWasLaunched = true;
                }
            }

            if (ballWasLaunched) {
                paddle.setPreGameInvincible(false);
                levelManager.activateScrolling();
            }
        } else {
            paddle.handleKeyPressed(e.getCode());
        }
    }

    public void keyReleased(KeyEvent e) {
        paddle.handleKeyReleased(e.getCode());
        if (e.getCode() == KeyCode.SHIFT) {
            powerUpManager.setNormalTrailEnabled(true);
        }
    }

    public void restart() {
        score = 0;
        running = true;
        showLaunchText = true;
        gameWon = false;
        collisionManager.resetScore();
        textManager.showGameOver(false);
        paddle.removeLaserImage(root);
        paddle.reset();

        for (Ball ball : balls) {
            ball.notLaunch();
        }

        levelManager.setDynamicSpawning(false);

        init();
    }

    private void gameOver() {
        audioManager.playGameOverSound();
        running = false;
        AudioManager.stopBackgroundMusic();
        textManager.showGameOver(true);

        for (Ball ball : balls) {
            ball.notLaunch();
        }
    }

    public boolean isWon() {
        return gameWon;
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


