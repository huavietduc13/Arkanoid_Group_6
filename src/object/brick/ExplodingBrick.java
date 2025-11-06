package object.brick;

import java.util.ArrayList;
import java.util.List;

public class ExplodingBrick extends Brick {
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 75;
    private static String IMAGE_PATH = "file:assets/images/explodingBrick.png";

    private double explosionRadius = 80;

    public ExplodingBrick(double x, double y, double width, double height) {
        super(IMAGE_PATH, x, y, width, height, INITIAL_STRENGTH, SCORE);
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
}
