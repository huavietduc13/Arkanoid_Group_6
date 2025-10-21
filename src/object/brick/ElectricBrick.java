package src.object.brick;

public class ElectricBrick extends Brick{
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 10;
    private static String IMAGE_PATH = "file:assets/images/electricBrick.png";

    public ElectricBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH, x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    @Override
    public void updateAppearance() {

    }
}
