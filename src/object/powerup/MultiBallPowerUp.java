package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.*;

public class MultiBallPowerUp extends PowerUp {
    public MultiBallPowerUp(double x, double y) {
        super("file:assets/images/powerup_multiball.png", x, y, 0);
        this.type = MULTI_BALL;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        System.out.println("Multi Ball!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        // Instant power up, no deactivation
    }
}
