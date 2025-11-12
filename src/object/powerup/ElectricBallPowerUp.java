package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.ELECTRIC_BAll;; 

public class ElectricBallPowerUp extends PowerUp {

    private static final String IMAGE_PATH = "file:assets/images/powerup_electric.png"; 
    
    private static final double DURATION_MS = 10000; 

    public ElectricBallPowerUp(double x, double y) {
        super(IMAGE_PATH, x, y, DURATION_MS);
        this.type = ELECTRIC_BAll;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        System.out.println("Electric Ball PowerUp Activated! Diagonal hit chain enabled.");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        System.out.println("Electric Ball PowerUp Deactivated.");
    }
}