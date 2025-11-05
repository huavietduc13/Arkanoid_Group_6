package object.brick;

public class ExplodingBrick extends Brick {
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 10;
    private static String IMAGE_PATH = "file:assets/images/explodingBrick.png";

    public ExplodingBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH, x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    @Override
    public void updateAppearance() {

    }
}
