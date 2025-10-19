package object.brick;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import object.GameObject;

public abstract class Brick extends GameObject {
    private static int BRICK_WIDTH = 63;
    private static int BRICK_HEIGHT = 33;

    protected int hitPoints;
    protected int score;

    private Rectangle collisionShape;

    public Brick(String imagePath, double x, double y, double width, double height, int hitPoints, int score) {
        super(imagePath, x, y, width, height);

        this.hitPoints = hitPoints;
        this.score = score;

        this.imageView.setPreserveRatio(false);
        this.imageView.setStyle("-fx-border-color: gray;");

        this.collisionShape = new Rectangle(x, y, width, height);
        this.collisionShape.setVisible(false);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.RED);

        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(false);
    }

    public boolean takeHit() {
        if (hitPoints > 0) {
            hitPoints--;
            updateAppearance();
        }
        return hitPoints <= 0;
    }

    public abstract void updateAppearance();

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getScore() {
        return score;
    }

    public Bounds getCollisionBounds() {
        return collisionShape.getBoundsInParent();
    }

    public Rectangle getCollisionShape() {
        return collisionShape;
    }

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

    @Override
    public void update() {
        // Bricks don't move so this method is empty, or we can make it move later :).
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        collisionShape.setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        collisionShape.setY(y);
    }
}
