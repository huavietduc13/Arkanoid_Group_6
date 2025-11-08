package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.*;
import static utils.Constants.*;

public class PointMultiplierPowerUp extends PowerUp {
    public PointMultiplierPowerUp(double x, double y) {
        super("file:assets/images/powerup_x2.png", x, y, 15000);
        this.type = POINTS_MULTIPLIER;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        SCORE_MULTIPLIER = 2;
        System.out.println("2x Score Multiplier!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        SCORE_MULTIPLIER = 1;
        System.out.println("Multiplier Ended!");
    }
}
