package src.engine;

import src.effect.ParticleEngine;
import src.enums.PowerUpType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import src.object.Ball;
import src.object.Paddle;
import src.object.brick.Brick;
import src.object.powerup.PowerUp;
import src.object.powerup.Shield;

import java.util.ArrayList;
import java.util.List;

import static src.enums.PowerUpType.*;
import static src.utils.Constants.*;

public class PowerUpManager {
    private List<PowerUp> powerUps;
    private List<PowerUp> activePowerUps;
    private Shield shield;
    private ParticleEngine effect;
    private AudioManager audioManager;

    private boolean redTrailEnabled = false;
    private boolean normalTrailEnabled = true;
    private boolean blueTrailEnabled = false;

    public PowerUpManager(ParticleEngine effect, AudioManager audioManager) {
        this.powerUps = new ArrayList<>();
        this.activePowerUps = new ArrayList<>();
        this.shield = new Shield();
        this.effect = effect;
        this.audioManager = audioManager;
    }

    public void dropPowerUp(Pane root, Brick brick, double dropChance) {
        if (Math.random() < dropChance) {
            System.out.println("Power up dropped!");
            PowerUp powerUp = PowerUp.createRandomPowerUp(
                    brick.getCenterX(),
                    brick.getY()
            );

            powerUps.add(powerUp);
            root.getChildren().addAll(powerUp.getImageView(), powerUp.getCollisionShape());
        }
    }

    public void update(Pane root, Paddle paddle, List<Ball> balls, boolean running) {
        updateFallingPowerUps(root, paddle, balls, running);
        updateActivePowerUps(paddle, balls);
        updateTrailEffect();
    }

    private void updateFallingPowerUps(Pane root, Paddle paddle, List<Ball> balls, boolean running) {
        List<PowerUp> powerUpsToRemove = new ArrayList<>();

        for (PowerUp powerUp : powerUps) {
            if (running) {
                Color powerUpColor = powerUp.getColor();
                effect.powerUpTrail(powerUp.getCenterX(), powerUp.getCenterY(), powerUpColor);
                powerUp.update();
            }

            if (powerUp.intersects(paddle)) {
                handlePowerUpCollection(root, powerUp, paddle, balls);
                powerUpsToRemove.add(powerUp);
            }

            if (powerUp.isOutOfScreen()) {
                powerUpsToRemove.add(powerUp);
            }
        }

        for (PowerUp powerUp : powerUpsToRemove) {
            root.getChildren().removeAll(powerUp.getImageView(), powerUp.getCollisionShape());
            powerUps.remove(powerUp);
        }
    }

        private void handlePowerUpCollection(Pane root, PowerUp powerUp, Paddle paddle, List<Ball> balls) {
        Color powerUpColor = powerUp.getColor();
        effect.powerUpCollect(
                powerUp.getCenterX(),
                powerUp.getCenterY(),
                powerUpColor
        );
        
        //Phát âm thanh power-up
        if (audioManager != null) {
            audioManager.playPowerUpSound();
        }

        if (powerUp.isTimedPowerUp()) {
            handleTimedPowerUp(powerUp, paddle, balls);
        } else {
            handleInstantPowerUp(root, powerUp, paddle, balls);
        }

        if (powerUp.isTimedPowerUp()) {
            handleTimedPowerUp(powerUp, paddle, balls);
        } else {
            handleInstantPowerUp(root, powerUp, paddle, balls);
        }
    }

    private void handleTimedPowerUp(PowerUp powerUp, Paddle paddle, List<Ball> balls) {
        removeConflictingPowerUp(powerUp, paddle, balls);

        PowerUp existingPowerUp = findActivePowerUp(powerUp.getType());

        if (existingPowerUp != null) {
            System.out.println("Reset activation Time!");
            existingPowerUp.setActivationTime(System.currentTimeMillis());
        } else {
            // Collect new power up
            if (powerUp.getType() == SHIELD) {
                shield.activate();
                effect.shieldActivate(SCREEN_WIDTH, shield.getY());
            }

            for (Ball ball : balls) {
                powerUp.collect(paddle, ball);
            }

            activePowerUps.add(powerUp);
        }
    }

    private void handleInstantPowerUp(Pane root, PowerUp powerUp, Paddle paddle, List<Ball> balls) {
        if (powerUp.getType() == MULTI_BALL) {
            Ball mainBall = balls.get(0);
            if (mainBall.isLaunched()) {
                createExtraBalls(root, paddle, balls, mainBall, EXTRA_BALLS);
            }
        } else {
            powerUp.collect(paddle, balls.get(0));
        }
    }

    private void createExtraBalls(Pane root, Paddle paddle, List<Ball> balls, Ball mainBall, int quantity) {
        double posX = mainBall.getX();
        double posY = mainBall.getY();
        double speed = Math.hypot(mainBall.getVx(), mainBall.getVy());

        for (int i = 0; i < quantity; i++) {
            double angle = Math.toRadians(-90 + (i + 1) * 30 - quantity * 15);
            double vx = speed * Math.cos(angle);
            double vy = speed * Math.sin(angle);

            Ball newBall = new Ball("file:assets/images/ball.png",
                    posX,
                    posY,
                    BALL_RADIUS,
                    vx,
                    vy);
            newBall.launch();

            applySpeedPowerUpsToNewBall(paddle, newBall);

            balls.add(newBall);

            root.getChildren().addAll(newBall.getImageView(), newBall.getCollisionShape());
        }
    }

    private void removeConflictingPowerUp(PowerUp newPowerUp, Paddle paddle, List<Ball> balls) {
        // Expand and Shrink cannot coexist
        if (newPowerUp.getType() == EXPAND_PADDLE) {
            activePowerUps.removeIf(p -> p.getType() == SHRINK_PADDLE);
        }
        if (newPowerUp.getType() == SHRINK_PADDLE) {
            activePowerUps.removeIf(p -> p.getType() == EXPAND_PADDLE);
        }

        // Fast and Slow cannot coexist
        if (newPowerUp.getType() == FAST_BALL) {
            PowerUp slowBall = findActivePowerUp(SLOW_BALL);
            if (slowBall != null) {
                for (Ball ball : balls) {
                    slowBall.deactivate(paddle, ball);
                }
                activePowerUps.remove(slowBall);
            }
        }
        if (newPowerUp.getType() == SLOW_BALL) {
            PowerUp fastBall = findActivePowerUp(FAST_BALL);
            if (fastBall != null) {
                for (Ball ball : balls) {
                    fastBall.deactivate(paddle, ball);
                }
                activePowerUps.remove(fastBall);
            }
        }
    }

    private PowerUp findActivePowerUp(PowerUpType type) {
        for (PowerUp active : activePowerUps) {
            if (active.getType() == type) {
                return active;
            }
        }

        return null;
    }

    private void updateActivePowerUps(Paddle paddle, List<Ball> balls) {
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
    }

    private void updateTrailEffect() {
        if (!activePowerUps.isEmpty()) {
            redTrailEnabled = false;
            normalTrailEnabled = true;
            blueTrailEnabled = false;

            boolean hasFastBall = false;
            boolean hasSlowBall = false;

            for (PowerUp powerUp : activePowerUps) {
                if (powerUp.getType() == FAST_BALL) {
                    hasFastBall = true;
                } else if (powerUp.getType() == SLOW_BALL) {
                    hasSlowBall = true;
                }
            }

            if (hasFastBall) {
                redTrailEnabled = true;
                normalTrailEnabled = false;
                blueTrailEnabled = false;
            } else if (hasSlowBall) {
                redTrailEnabled = false;
                normalTrailEnabled = false;
                blueTrailEnabled = true;
            }
        }
    }

    public void applySpeedPowerUpsToNewBall(Paddle paddle, Ball newBall) {
        for (PowerUp powerUp : activePowerUps) {
            if (powerUp.getType() == FAST_BALL || powerUp.getType() == SLOW_BALL) {
                powerUp.activate(paddle, newBall);
            }
        }
    }

    public void expireSpeedPowerUps() {
        for (PowerUp active : activePowerUps) {
            if (active.getType() == FAST_BALL || active.getType() == SLOW_BALL) {
                active.setExpired();
            }
        }
    }

    public void reset(Pane root) {
        for (PowerUp powerUp : powerUps) {
            root.getChildren().removeAll(powerUp.getImageView(), powerUp.getCollisionShape());
        }

        powerUps.clear();
        activePowerUps.clear();
        shield.deactivate();

        redTrailEnabled = false;
        normalTrailEnabled = true;
        blueTrailEnabled = false;
    }

    public Shield getShield() {
        return shield;
    }

    public List<PowerUp> getActivePowerUps() {
        return activePowerUps;
    }

    public boolean isRedTrailEnabled() {
        return redTrailEnabled;
    }

    public boolean isNormalTrailEnabled() {
        return normalTrailEnabled;
    }

    public boolean isBlueTrailEnabled() {
        return blueTrailEnabled;
    }

    public void setRedTrailEnabled(boolean enabled) {
        this.redTrailEnabled = enabled;
        if (enabled) {
            normalTrailEnabled = false;
            blueTrailEnabled = false;
        }
    }

    public void setNormalTrailEnabled(boolean enabled) {
        this.normalTrailEnabled = enabled;
        if (enabled) {
            redTrailEnabled = false;
            blueTrailEnabled = false;
        }
    }

    public void setBlueTrailEnabled(boolean enabled) {
        this.blueTrailEnabled = enabled;
        if (enabled) {
            redTrailEnabled = false;
            normalTrailEnabled = false;
        }
    }
}
