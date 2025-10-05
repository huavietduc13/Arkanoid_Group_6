package object.brick;

public class BrickFactory {
    private static int BRICK_WIDTH = 63;
    private static int BRICK_HEIGHT = 33;

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
