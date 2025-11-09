package src.object.powerup;

import src.object.Ball;
import src.object.Paddle;
import static src.utils.Constants.*;
import static src.enums.PowerUpType.*;

public class ShrinkPaddlePowerUp extends PowerUp {
    private static final double SIZE_MULTIPLIER = 0.6;
    private double originalWidth;

    public ShrinkPaddlePowerUp(double x, double y) {
        super("file:assets/images/powerup_shrink.png", x, y, 8000);
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
