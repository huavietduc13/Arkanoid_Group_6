package src.object.powerup;

import src.object.Ball;
import src.object.Paddle;

import static src.enums.PowerUpType.*;
import static src.utils.Constants.*;

public class SlowBallPowerUp extends PowerUp {
    private static final double SPEED_MULTIPLIER = 0.6;
    private double originalVx;
    private double originalVy;
    private double originalRotationSpeed;

    public SlowBallPowerUp(double x, double y) {
        super("file:assets/images/powerup_slow.png", x, y, 15000);
        this.type = SLOW_BALL;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        originalVx = ball.getVx();
        originalVy = ball.getVy();
        originalRotationSpeed = ball.getRotationSpeed();
        ball.setVx(originalVx * SPEED_MULTIPLIER);
        ball.setVy(originalVy * SPEED_MULTIPLIER);
        ball.setRotationSpeed(originalRotationSpeed * SPEED_MULTIPLIER);
        MIN_SPEED = 4.0;
        System.out.println("Ball slowed down!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        double currentSpeed  = Math.hypot(ball.getVx(), ball.getVy());
        double originalSpeed = Math.hypot(originalVx, originalVy);
        if (currentSpeed > 0) {
            double ratio = originalSpeed / currentSpeed;
            ball.setVx(ball.getVx() * ratio);
            ball.setVy(ball.getVy() * ratio);
        }
        ball.resetRotationSpeed();
        MIN_SPEED = 6.0;
        System.out.println("Ball returned to normal speed!");
    }

}
