package object.powerup;

import object.Ball;
import object.Paddle;
import static utils.Constants.*;
import static enums.PowerUpType.*;

public class ShrinkPaddlePowerUp extends PowerUp {
    private static final double SIZE_MULTIPLIER = 0.6;
    private double originalWidth;

    public ShrinkPaddlePowerUp(double x, double y) {
        super("file:assets/images/powerup_warp_1.png", x, y, 12, 2, 12000);
        this.type = SHRINK_PADDLE;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        originalWidth = PADDLE_WIDTH;
        double newWidth = originalWidth * SIZE_MULTIPLIER;
        paddle.getImageView().setFitWidth(newWidth);
        paddle.getCollisionShape().setWidth(newWidth);
        System.out.println("Paddle Shrunk!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        paddle.getImageView().setFitWidth(originalWidth);
        paddle.getCollisionShape().setWidth(originalWidth);
        System.out.println("Paddle returned to normal size");
    }
}
