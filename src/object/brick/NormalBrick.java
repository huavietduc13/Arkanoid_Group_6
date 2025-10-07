package src.object.brick;

public class NormalBrick extends Brick{
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 10;
    private static String IMAGE_PATH = "file:assets/images/brick_strong_1.png";

    public NormalBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH, x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    @Override
    public void updateAppearance() {

    }
}
