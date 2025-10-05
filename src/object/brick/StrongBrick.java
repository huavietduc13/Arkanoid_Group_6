package object.brick;

import javafx.scene.image.Image;

public class StrongBrick extends Brick{
    private static int INITIAL_STRENGTH = 3;
    private static int SCORE = 30;
    private static String IMAGE_PATH_1 = "file:assets/images/brick_strong_1.png";
    private static String IMAGE_PATH_2 = "file:assets/images/brick_strong_2.png";
    private static String IMAGE_PATH_3 = "file:assets/images/brick_strong_3.png";

    public StrongBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH_3, x, y, width, height, INITIAL_STRENGTH, SCORE);
    }

    @Override
    public void updateAppearance() {
        if (hitPoints == 2) {
            this.imageView.setImage(new Image(IMAGE_PATH_2));
        } else if (hitPoints == 1) {
            this.imageView.setImage(new Image(IMAGE_PATH_1));
        }
    }
}
