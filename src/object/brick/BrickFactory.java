package src.object.brick;

public class BrickFactory {
    public static int BRICK_WIDTH = 80;
    public static int BRICK_HEIGHT = 40;

    public static Brick createBrick(String type, double x, double y) {
        switch (type.toLowerCase()) {
            case "strong":
                return new StrongBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
            case "indestructible":
                return new IndestructibleBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
            default:
                return new NormalBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }
    }
}
