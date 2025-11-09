package src.object.brick;

import java.util.ArrayList;
import java.util.List;

import static src.enums.BrickType.*;

public class ElectricBrick extends Brick{
    private static int INITIAL_STRENGTH = 1;
    private static int SCORE = 50;
    private static String IMAGE_PATH = "file:assets/images/electricBrick.png";

    public ElectricBrick(double x, double y) {
        super(IMAGE_PATH, x, y, INITIAL_STRENGTH, SCORE);
        this.type = ELECTRIC;
    }

    @Override
    public void updateAppearance() {

    }

    public boolean isOnDiagonals(Brick other) {
        if (other == this || other.isDestroyed()) {
            return false;
        }

        int thisRow = this.getRow();
        int thisCol = this.getCol();

        int otherRow = other.getRow();
        int otherCol = other.getCol();

        int deltaRow = Math.abs(thisRow - otherRow);
        int deltaCol = Math.abs(thisCol - otherCol);

        return deltaRow == deltaCol && deltaRow != 0;
    }

    public List<Brick> getDiagonalBricks(List<Brick> allBricks) {
        List<Brick> diagonalBricks = new ArrayList<>();
        for (Brick brick : allBricks) {
            if (isOnDiagonals(brick)) {
                diagonalBricks.add(brick);
            }
        }

        return diagonalBricks;
    }
}
