package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.*;

public class ShieldPowerUp extends PowerUp {
    public ShieldPowerUp(double x, double y) {
        super("file:assets/images/powerup_shield1.png", x, y, 15000);
        this.type = SHIELD;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        System.out.println("Shield Activated!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        System.out.println("Shield Deactivated!");
    }
}
