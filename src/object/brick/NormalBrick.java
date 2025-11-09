package object.brick;

import java.util.Random;

import static enums.BrickType.*;

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

    public NormalBrick(double x, double y) {
        super(getRandomImagePath(), x, y, INITIAL_STRENGTH, SCORE);
        this.type = NORMAL;
    }

    @Override
    public void updateAppearance() {

    }
}
