package object.brick;

import javafx.animation.Timeline;

import java.util.ArrayList;
import java.util.List;

public class ExplodingBrick extends Brick {
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 75;
    private static String IMAGE_PATH = "file:assets/images/explodingBrick.png";

    private double explosionRadius = 80;

    private Timeline pulseAnimation;

    public ExplodingBrick(double x, double y) {
        super(IMAGE_PATH, x, y, INITIAL_STRENGTH, SCORE);

//        createPulseEffect();
    }

    @Override
    public void updateAppearance() {

    }

    public boolean isInExplosionRange(Brick other) {
        if (other == this || other.isDestroyed()) {
            return false;
        }

        double thisCenterX = this.getCenterX();
        double thisCenterY = this.getCenterY();

        double otherCenterX = other.getCenterX();
        double otherCenterY = other.getCenterY();

        double distance = Math.hypot(thisCenterX - otherCenterX, thisCenterY - otherCenterY);

        return distance <= explosionRadius;
    }

    // Get all bricks in range
    public List<Brick> getBricksInExplosionRange(List<Brick> allBricks) {
        List<Brick> affectedBricks = new ArrayList<>();
        for (Brick brick : allBricks) {
            if (isInExplosionRange(brick)) {
                affectedBricks.add(brick);
            }
        }

        return affectedBricks;
    }

    // Get 8 bricks surrounding
    public List<Brick> getAdjacentBricks(List<Brick> allBricks) {
        List<Brick> adjacentBricks = new ArrayList<>();

        double thisCenterX = this.getCenterX();
        double thisCenterY = this.getCenterY();

        int thisCol = this.getCol();
        int thisRow = this.getRow();

        int[][] direction = {
                {-1 , 0},
                {1, 0},
                {0, -1},
                {0, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };

        for (int[] dir : direction) {
            int targetRow = thisRow + dir[0];
            int targetCol = thisCol + dir[1];

            for (Brick brick : allBricks) {
                if (brick == this || brick.isDestroyed()) {
                    continue;
                }
                double brickCenterX = brick.getCenterX();
                double brickCenterY = brick.getCenterY();

                int brickRow = brick.getRow();
                int brickCol = brick.getCol();

                if (brickRow == targetRow && brickCol == targetCol) {
                    adjacentBricks.add(brick);
                    break;
                }
            }
        }

        return adjacentBricks;
    }

    public double getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    private void createPulseEffect() {
        pulseAnimation = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(imageView.opacityProperty(), 1.0)
                ),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(500),
                        new javafx.animation.KeyValue(imageView.opacityProperty(), 0.6)
                ),
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(1000),
                        new javafx.animation.KeyValue(imageView.opacityProperty(), 1.0)
                )
        );

        pulseAnimation.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        pulseAnimation.play();
    }

    @Override
    public void takeHit(Runnable onDestroyed) {
        // Stop pulse khi bị hit
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
        super.takeHit(onDestroyed);
    }
}
