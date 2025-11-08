package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.*;
import static utils.Constants.*;

public class FastBallPowerUp extends PowerUp {
    private static final double SPEED_MULTIPLIER = 1.2;
    private double originalVx;
    private double originalVy;
    private double originalRotationSpeed;

    public FastBallPowerUp(double x, double y) {
        super("file:assets/images/powerup_fast.png", x, y, 8000);
        this.type = FAST_BALL;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        originalVx = ball.getVx();
        originalVy = ball.getVy();
        originalRotationSpeed = ball.getRotationSpeed();
        ball.setVx(originalVx * SPEED_MULTIPLIER);
        ball.setVy(originalVy * SPEED_MULTIPLIER);
        ball.setRotationSpeed(originalRotationSpeed * SPEED_MULTIPLIER);
        MAX_SPEED = 10.5;
        System.out.println("Ball is Moving Faster!");
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
        MAX_SPEED = 9.0;
        System.out.println("Ball returned to normal speed!");
    }
}
