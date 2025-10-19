package object;

import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Paddle extends GameObject {
    private double speed;
    private double boundary;

    private boolean movingLeft = false;
    private boolean movingRight = false;

    private Rectangle collisionShape;

    public Paddle(String imagePath, double boundary, double x, double y, double width, double height, double speed) {
        super(imagePath, x, y, width, height);
        this.speed = speed;
        this.boundary = boundary;

        this.collisionShape = new Rectangle(x, y, width, height);
        this.collisionShape.setVisible(true);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.RED);
        this.collisionShape.setArcWidth(20);
        this.collisionShape.setArcHeight(20);

        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(false);
    }

    public void moveLeft() {
        setX(Math.max(0, getX() - speed));
    }

    public void moveRight() {
        setX(Math.min(boundary, getX() + speed));
    }

    @Override
    public void update() {
        if (movingLeft) {
            moveLeft();
        }
        if (movingRight) {
            moveRight();
        }
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

    private void updateCollisionShape() {
        collisionShape.setX(getX());
        collisionShape.setY(getY());
    }

    public Bounds getCollisionBounds() {
        return collisionShape.getBoundsInParent();
    }

    public Rectangle getCollisionShape() {
        return collisionShape;
    }

    public double getVx() {
        if (movingLeft) {
            return -speed;
        }
        if (movingRight) {
            return speed;
        }
        return 0;
    }

    public double getSpeed() {
        return speed;
    }

    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            movingLeft = true;
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            movingRight = true;
        }
    }

    public void handleKeyReleased(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            movingLeft = false;
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            movingRight = false;
        }
    }
}
