package object.powerup;

import object.Ball;
import object.Paddle;

public class ExtraLifePowerUp extends PowerUp {
    public ExtraLifePowerUp(double x, double y) {
        super("file:assets/images/powerup_life_1.png", x, y, 12, 2.0, 0);
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        paddle.gainLife();
        System.out.println("Extra Life!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        // Instant power up, no deactivation
    }
}
