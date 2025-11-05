package object.powerup;

import object.Ball;
import object.Paddle;
import utils.Constants;
import static enums.PowerUpType.*;


public class ExpandPaddlePowerUp extends PowerUp {
    private static final double SIZE_MULTIPLIER = 1.5;
    private double originalWidth;

    public ExpandPaddlePowerUp(double x, double y) {
        super("file:assets/images/powerup_expand_1.png", x, y, 12, 2, 12000);
        this.type = EXPAND_PADDLE;
    }

    @Override
    public void activate(Paddle paddle, Ball ball) {
        originalWidth = Constants.PADDLE_WIDTH;
        double newWidth = originalWidth * SIZE_MULTIPLIER;
        paddle.getImageView().setFitWidth(newWidth);
        paddle.getCollisionShape().setWidth(newWidth);
        System.out.println("Paddle Expanded!");
    }

    @Override
    public void deactivate(Paddle paddle, Ball ball) {
        paddle.getImageView().setFitWidth(originalWidth);
        paddle.getCollisionShape().setWidth(originalWidth);
        System.out.println("Paddle returned to normal size");
    }
}
