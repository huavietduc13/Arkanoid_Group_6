package object.brick;

public class IndestructibleBrick extends Brick{
    private static int INITIAL_STRENGTH = 999;
    private static int SCORE = 0;
    private static String IMAGE_PATH = "file:assets/images/brick_silver.png";

    public IndestructibleBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH, x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    public boolean takeHit() {
        return false;
    }

    @Override
    public void updateAppearance() {

    }
}