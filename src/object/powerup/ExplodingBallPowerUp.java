package object.powerup;

import object.Ball;
import object.Paddle;

import static enums.PowerUpType.EXPLODING_BALL;

public class ExplodingBallPowerUp extends PowerUp {

    private static final String IMAGE_PATH = "file:assets/images/powerup_exploding.png"; 
    
    private static final double DURATION_MS = 10000; 

    public ExplodingBallPowerUp(double x, double y) {
        super(IMAGE_PATH, x, y, DURATION_MS);
        this.type = EXPLODING_BALL;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        System.out.println("Exploding Ball PowerUp Activated!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        System.out.println("Exploding Ball PowerUp Deactivated!");
    }
}