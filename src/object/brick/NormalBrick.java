package src.object.brick;

import java.util.Random;

public class NormalBrick extends Brick {
    private static final int INITIAL_STRENGTH = 1;
    private static final int SCORE = 10;

    private static final String[] IMAGE_PATHS =  {
        "file:assets/images/normalBrick1.png",
        "file:assets/images/normalBrick2.png",
        "file:assets/images/normalBrick3.png"
    };

    private static String getRandomImagePath() {
        Random random = new Random();
        int randomIndex = random.nextInt(IMAGE_PATHS.length);
        return IMAGE_PATHS[randomIndex];
    }

    public NormalBrick(double x, double y, double width, double height) {
        super(getRandomImagePath(), x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    @Override
    public void updateAppearance() {

    }
}
