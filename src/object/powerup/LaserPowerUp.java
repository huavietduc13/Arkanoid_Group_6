package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.*;

public class LaserPowerUp extends PowerUp {
    public LaserPowerUp(double x, double y) {
        super("file:assets/images/powerup_laser.png", x, y, 5000);
        this.type = LASER;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        paddle.enableLaser();
        System.out.println("Laser Activated!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        paddle.disableLaser();
        System.out.println("Laser Deactivated!");
    }
}
