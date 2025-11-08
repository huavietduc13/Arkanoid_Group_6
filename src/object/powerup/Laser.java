package object.powerup;

import javafx.geometry.Bounds;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import object.GameObject;

import static utils.Constants.*;

public class Laser extends GameObject {
    private double vy;
    private boolean active;
    private Rectangle collisionShape;

    public Laser(double x, double y) {
        super("file:assets/images/laser.png", x, y, LASER_WIDTH, LASER_HEIGHT);
        this.vy = LASER_SPEED;
        this.active = true;

        this.collisionShape = new Rectangle(x, y, LASER_WIDTH, LASER_HEIGHT);
        this.collisionShape.setVisible(true);
        this.collisionShape.setFill(Color.CYAN);
        this.collisionShape.setStroke(Color.WHITE);
        this.collisionShape.setStrokeWidth(1);
        this.collisionShape.setEffect(new Glow(0.8));

        this.imageView.setVisible(false);
        this.imageView.setFitWidth(LASER_WIDTH);
        this.imageView.setFitHeight(LASER_HEIGHT);
        this.imageView.setPreserveRatio(false);
    }

    @Override
    public void update() {
        if (!active) {
            return;
        }

        double newY = getY() + vy * 0.016;
        setY(newY);

        collisionShape.setY(newY);

        if (newY + LASER_HEIGHT < 0) {
            active = false;
        }
    }

    public boolean intersects(GameObject other) {
        if (!active) {
            return false;
        }

        Bounds laserBounds = collisionShape.getBoundsInParent();
        Bounds otherBounds = other.getImageView().getBoundsInParent();

        return laserBounds.intersects(otherBounds);
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getCollisionShape() {
        return collisionShape;
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        collisionShape.setY(y);
    }
}
