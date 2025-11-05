package object.brick;

import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import object.GameObject;
import enums.BrickType;

import static utils.Constants.*;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    protected int score;
    private boolean isBeingHit = false;

    private Rectangle collisionShape;

    public Brick(String imagePath, double x, double y, double width, double height, int hitPoints, int score) {
        super(imagePath, x, y, width, height);

        this.hitPoints = hitPoints;
        this.score = score;

        this.collisionShape = new Rectangle(x, y, width, height);
        this.collisionShape.setVisible(false);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.RED);
        this.collisionShape.setArcWidth(20);
        this.collisionShape.setArcHeight(20);

        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(false);
        this.imageView.setStyle("-fx-border-color: gray;");
    }

    public void takeHit(Runnable onDestroyed) {
        if (hitPoints > 0 && !isBeingHit) {
            isBeingHit = true;

            shake(() -> {
                hitPoints--;
                updateAppearance();

                if (hitPoints <= 0) {
                    imageView.setVisible(false);
                    collisionShape.setVisible(false);
                    if (onDestroyed != null) {
                        onDestroyed.run();
                    }
                }
                isBeingHit = false;
            });
        }
    }

    public abstract void updateAppearance();

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public boolean isBeingHit() {
        return isBeingHit;
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

    public static Brick createBrick(BrickType type, double x, double y) {
        switch (type) {
            case STRONG:
                return new StrongBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
            case INDESTRUCTIBLE:
                return new IndestructibleBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
            case ELECTRIC:
                return new ElectricBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
            default:
                return new NormalBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        }
    }

    public int getRow() {
        double centerY = getY() + getHeight() / 2;

        int row = (int) Math.round((centerY - BRICK_START_Y) / (BRICK_HEIGHT + BRICK_PADDING));

        return row;
    }

    public int getCol() {
        double centerX = getX() + getWidth() / 2;

        int col = (int) Math.round((centerX - BRICK_START_X) / (BRICK_WIDTH + BRICK_PADDING));

        return col;
    }

    public Color getColor() {
        if (this instanceof ElectricBrick) {
            return Color.YELLOW;
        } else if (this instanceof StrongBrick) {
            return Color.BLUE;
        } else if (this instanceof IndestructibleBrick) {
            return Color.GRAY;
        } else {
            return Color.RED;
        }
    }

    @Override
    public void update() {
        // Bricks don't move so this method is empty, or we can make it move later :).
    }

    public void shake(Runnable onFinish) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(36), imageView);
        tt.setFromX(-2);
        tt.setToX(2);
        tt.setCycleCount(4); // đi qua lại 2 lần
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> {
            imageView.setTranslateX(0);
            if (onFinish != null) onFinish.run();
        });
        tt.play();
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

    public double getCenterX() {
        return getX() + getWidth() / 2;
    }

    public double getCenterY() {
        return getY() + getHeight() / 2;
    }
}
