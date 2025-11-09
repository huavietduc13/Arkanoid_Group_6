package src.object.brick;

import static src.enums.BrickType.*;

public class IndestructibleBrick extends Brick{
    private static int INITIAL_STRENGTH = 999;
    private static int SCORE = 0;
    private static String IMAGE_PATH = "file:assets/images/brick_indestructible.png";

    public IndestructibleBrick(double x, double y) {
        super(IMAGE_PATH, x, y, INITIAL_STRENGTH, SCORE);
        this.type = INDESTRUCTIBLE;
    }

    public boolean takeHit() {
        return false;
    }

    @Override
    public void updateAppearance() {

    }
}