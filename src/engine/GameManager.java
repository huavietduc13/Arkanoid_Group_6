package engine;

import effect.ParticleEngine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import object.Ball;
import object.Paddle;
import object.brick.Brick;

import java.util.ArrayList;
import java.util.List;

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

    private Paddle paddle;
    private List<Ball> balls = new ArrayList<>();

    private int score = 0;
    private int levelNumber;
    private boolean running = true;
    private boolean showLaunchText = true;
    private boolean isPaused = false;

    private long lastFrameTime = 0;

    public GameManager(GraphicsContext gc, Pane root, int levelNumber) {
        this.gc = gc;
        this.root = root;
        this.levelNumber = levelNumber;
        this.effect = new ParticleEngine(gc);
        this.audioManager = new AudioManager();
        this.levelManager = new LevelManager();
        this.powerUpManager = new PowerUpManager(effect, audioManager);
        this.collisionManager = new CollisionManager(effect, audioManager, levelManager, powerUpManager);

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
        powerUpManager.reset(root);

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

        textManager = new TextManager(root);

        loadLevel(this.levelNumber);

        root.getChildren().addAll(
                paddle.getImageView(), paddle.getCollisionShape(),
                mainBall.getImageView(), mainBall.getCollisionShape(),
                paddle.getGunLeftImageView(),
                paddle.getGunRightImageView()
        );
    }

    private void loadLevel(int levelNumber) {
        levelManager.loadLevel(levelNumber);

        for (Brick brick : levelManager.getBricks()) {
            root.getChildren().addAll(brick.getImageView(), brick.getCollisionShape());
        }
    }

    public void render(Pane root) {
        gc.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        collisionManager.handleBallBricksCollision(root, balls);

        collisionManager.handleLaserBricksCollision(root, paddle);

        score = collisionManager.getScore();

        powerUpManager.update(root, paddle, balls, running);

        powerUpManager.getShield().render(gc);

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

        powerUpManager.getShield().update(deltaTime);

        effect.update(deltaTime);

        paddle.update();

        effect.paddleTrail(paddle.getCenterX(), paddle.getCenterY());

        paddle.addLaserImage(root);

        dockBallToPaddle();

        updateBalls();

        if (balls.isEmpty()) {
            respawnMainBall();
        }

        if (paddle.isOutOfLives()) {
            System.out.println("Game over!");
            gameOver();
        }

        textManager.updateScoreAndLives(score, paddle);
        textManager.showLaunchHint(showLaunchText);
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
            powerUpManager.setNormalTrailEnabled(true);
        }
    }

    public void restart() {
        score = 0;
        running = true;
        showLaunchText = true;
        textManager.showGameOver(false);
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
