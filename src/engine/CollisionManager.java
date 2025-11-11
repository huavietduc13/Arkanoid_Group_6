package engine;

import effect.ParticleEngine;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import object.brick.ElectricBrick;
import object.brick.ExplodingBrick;
import object.powerup.Laser;
import object.Boss;

import java.util.ArrayList;
import java.util.List;

import static enums.BrickType.*;
import static enums.PowerUpType.*;
import static utils.Constants.*;

public class CollisionManager {
    private ParticleEngine effect;
    private AudioManager audioManager;
    private LevelManager levelManager;
    private PowerUpManager powerUpManager;

    private int score = 0;

    public CollisionManager(ParticleEngine effect, AudioManager audioManager,
                            LevelManager levelManager, PowerUpManager powerUpManager) {

        this.effect = effect;
        this.audioManager = audioManager;
        this.levelManager = levelManager;
        this.powerUpManager = powerUpManager;
    }

    public void handleBallBricksCollision(Pane root, List<Ball> balls) {
        List<Brick> bricksToRemove = new ArrayList<>();

        boolean isExplodingBallActive = powerUpManager.isActive(EXPLODING_BALL);
        boolean isElectricBallActive = powerUpManager.isActive(ELECTRIC_BAll);

        for (Ball ball : balls) {
            for (Brick brick : levelManager.getBricks()) {
                if (!brick.isDestroyed() && !brick.isBeingHit() && CollisionDetector.handleCollision(ball, brick)) {
                    score += SCORE * SCORE_MULTIPLIER;

                    Color brickColor = brick.getColor();

                    final boolean isElectricBrick = brick instanceof ElectricBrick;
                    final boolean isExplodingBrick = brick instanceof ExplodingBrick;
                    final boolean shouldExplode = isExplodingBrick || isExplodingBallActive;
                    final boolean shouldElectric = isElectricBrick || isElectricBallActive;

                    brick.takeHit(() -> {
                        effect.brickExplosion(
                                brick.getCenterX(),
                                brick.getCenterY(),
                                brickColor
                        );

                        // Xử lý hiệu ứng điện
                        if (shouldElectric) {
                            if (isElectricBrick) {
                                handleElectricBrickDestruction(root, (ElectricBrick) brick);
                            } else {
                                // Gạch thường bị bóng điện chạm
                                handleElectricBallEffect(root, brick);
                            }
                        }

                        // Xử lý hiệu ứng nổ
                        if (shouldExplode) {
                            if (isExplodingBrick) {
                                handleExplodingBrickDestruction(root, (ExplodingBrick) brick);
                            } else {
                                handleExplodingBallEffect(root, brick);
                            }
                        }

                        powerUpManager.dropPowerUp(root, brick, 0.3);
                        bricksToRemove.add(brick);
                    });

                    if (brick.getType() == INDESTRUCTIBLE) {
                        audioManager.playIndestructibleBrickCollisionSound();
                    } else {
                        audioManager.playRandomMeowSound();
                    }
                    break;
                }
            }
        }

        removeBricks(root, bricksToRemove);

    }

    public void handleLaserBricksCollision(Pane root, Paddle paddle) {
        List<Laser> lasersToRemove = new ArrayList<>();
        List<Brick> bricksToRemove = new ArrayList<>();

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
                            handleElectricBrickDestruction(root, (ElectricBrick) brick);
                        }

                        if (isExploding) {
                            handleExplodingBrickDestruction(root, (ExplodingBrick) brick);
                        }

                        powerUpManager.dropPowerUp(root, brick, 0.3);
                        bricksToRemove.add(brick);
                    });

                    laser.deactivate();
                    lasersToRemove.add(laser);
                    effect.laserHit(laser.getX() + LASER_WIDTH / 2, brick.getY() + brick.getHeight());

                    if (brick.getType() != INDESTRUCTIBLE) {
                        audioManager.playRandomMeowSound();
                    }
                    break;
                }
            }
        }

        removeLasers(root, paddle, lasersToRemove);
        removeBricks(root, bricksToRemove);

    }

    public void handleBallPaddleCollision(Paddle paddle, List<Ball> balls) {
        for (Ball ball : balls) {
            if (CollisionDetector.handlePaddleCollision(ball, paddle)) {
                audioManager.playPaddleCollisionSound();
                effect.paddleHit(ball.getCenterX(), paddle.getY());
            }
        }
    }

    private void handleElectricBrickDestruction(Pane root, ElectricBrick electricBrick) {
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

                        powerUpManager.dropPowerUp(root, targetBrick, 0.1);

                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction(root, (ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction(root, (ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }

    }

    private void handleElectricBallEffect(Pane root, Brick centerBrick) {
        List<Brick> diagonalBricks = getDiagonalBricks(centerBrick, levelManager.getBricks());

        double centerX = centerBrick.getCenterX();
        double centerY = centerBrick.getCenterY();

        effect.electricExplosion(centerX, centerY);
        effect.screenShake(root, 200, 5);

        for (int i = 0; i < diagonalBricks.size(); i++) {
            Brick targetBrick = diagonalBricks.get(i);
            if (targetBrick.isDestroyed() || targetBrick.isBeingHit()) {
                continue;
            }

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

                        powerUpManager.dropPowerUp(root, targetBrick, 0.1);

                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction(root, (ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction(root, (ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }
    }

    private List<Brick> getDiagonalBricks(Brick centerBrick, List<Brick> allBricks) {
        List<Brick> diagonalBricks = new ArrayList<>();

        int centerRow = centerBrick.getRow();
        int centerCol = centerBrick.getCol();

        for (Brick brick : allBricks) {
            if (brick == centerBrick || brick.isDestroyed()) {
                continue;
            }

            int brickRow = brick.getRow();
            int brickCol = brick.getCol();

            int deltaRow = Math.abs(centerRow - brickRow);
            int deltaCol = Math.abs(centerCol - brickCol);

            if (deltaRow == deltaCol && deltaRow != 0) {
                diagonalBricks.add(brick);
            }
        }

        return diagonalBricks;
    }

    private void handleExplodingBrickDestruction(Pane root, ExplodingBrick explodingBrick) {
        List<Brick> affectedBricks = explodingBrick.getBricksInExplosionRange(levelManager.getBricks());

        double centerX = explodingBrick.getCenterX();
        double centerY = explodingBrick.getCenterY();

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

                        powerUpManager.dropPowerUp(root, targetBrick, 0.1);

                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction(root, (ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction(root, (ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }

    }

    private void handleExplodingBallEffect(Pane root, Brick centerBrick) {
        List<Brick> affectedBricks = getBricksInRange(centerBrick, 150.0);

        double centerX = centerBrick.getCenterX();
        double centerY = centerBrick.getCenterY();

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

            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(event -> {
                if (!targetBrick.isDestroyed()) {
                    score += targetBrick.getScore() * SCORE_MULTIPLIER;

                    targetBrick.takeHit(() -> {
                        effect.secondExplosion(targetBrick.getCenterX(), targetBrick.getCenterY());

                        root.getChildren().removeAll(targetBrick.getImageView(), targetBrick.getCollisionShape());

                        levelManager.removeBrick(targetBrick);

                        powerUpManager.dropPowerUp(root, targetBrick, 0.1);

                        // Chain reaction với ElectricBrick và ExplodingBrick
                        if (targetBrick instanceof ElectricBrick) {
                            handleElectricBrickDestruction(root, (ElectricBrick) targetBrick);
                        }
                        if (targetBrick instanceof ExplodingBrick) {
                            handleExplodingBrickDestruction(root, (ExplodingBrick) targetBrick);
                        }
                    });
                }
            });
            pause.play();
        }
    }

    private List<Brick> getBricksInRange(Brick centerBrick, double radius) {
        List<Brick> bricksInRange = new ArrayList<>();
        double centerX = centerBrick.getCenterX();
        double centerY = centerBrick.getCenterY();

        for (Brick brick : levelManager.getBricks()) {
            if (brick == centerBrick || brick.isDestroyed()) {
                continue;
            }

            double brickCenterX = brick.getCenterX();
            double brickCenterY = brick.getCenterY();
            double distance = Math.hypot(centerX - brickCenterX, centerY - brickCenterY);

            if (distance <= radius) {
                bricksInRange.add(brick);
            }
        }

        return bricksInRange;
    }

    private void removeBricks(Pane root, List<Brick> bricksToRemove) {
        for (Brick brick : bricksToRemove) {
            root.getChildren().removeAll(brick.getImageView(), brick.getCollisionShape());
            levelManager.removeBrick(brick);
        }
    }

    private void removeLasers(Pane root, Paddle paddle, List<Laser> lasersToRemove) {
        for (Laser laser : lasersToRemove) {
            root.getChildren().removeAll(laser.getImageView(), laser.getCollisionShape());
            paddle.getActiveLasers().remove(laser);
        }
    }

    public void handleBallBossCollision(Ball ball, Boss boss) {
        if (boss != null && CollisionDetector.handleBallBossCollision(ball, boss)) {
            score += SCORE * SCORE_MULTIPLIER;
        }
    }

    public void handleLaserBossCollision(Paddle paddle, Boss boss) {
        if (boss == null) {
            return;
        }

        for (Laser laser : paddle.getActiveLasers()) {
            if (laser.isActive() && laser.intersects(boss)) {
                laser.deactivate();
                boss.takeHit();
                score += SCORE * SCORE_MULTIPLIER;
            }
        }
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }
}
