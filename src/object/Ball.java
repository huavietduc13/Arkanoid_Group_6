package object;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball extends GameObject {
    private double vx;
    private double vy;
    private double radius;

    private double rotationSpeed = 5;
    private double rotationAngle = 0;

    private boolean ballLaunched = false;

    private Circle collisionShape;

    public Ball(String imagePath, double x, double y, double radius, double vx, double vy) {
        super(imagePath, x, y, radius * 2, radius * 2);
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;

        this.collisionShape = new Circle(x + radius, y + radius, radius);
        this.collisionShape.setVisible(true);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.RED);

        this.imageView.setFitWidth(radius * 2);
        this.imageView.setFitHeight(radius * 2);
        this.imageView.setPreserveRatio(false);
    }

    @Override
    public void update() {
        if (ballLaunched) {
            rotate();
        }

        if (!ballLaunched) {
            updateCollisionShape();
            return;
        }

        double newX = getX() + vx;
        double newY = getY() + vy;

        // Collide with left/right boundary
        if (newX < 0 || newX + getWidth() > 600) {
            vx *= -1;
            if (newX < 0) {
                newX = 0;
            } else {
                newX = 600 - getWidth();
            }
        }

        // Collide with upper boundary
        if (newY < 0) {
            vy *= -1;
            newY = 0;
        }

        // Collide with bottom boundary
        if (newY + getHeight() > 800) {
            vy *= -1;
            newY = 800 - getHeight();
        }

        setX(newX);
        setY(newY);

        updateCollisionShape();
    }

    private void updateCollisionShape() {
        collisionShape.setCenterX(getX() + radius);
        collisionShape.setCenterY(getY() + radius);
    }

    public boolean intersects(GameObject other) {
        Bounds ballBounds  = this.getImageView().getBoundsInParent();
        Bounds otherBounds = this.getImageView().getBoundsInParent();

        return ballBounds.intersects(otherBounds);
    }

    public void rotate() {
        double dx = vx;
        double dy = vy;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double deltaAngle = Math.toDegrees(distance / radius);

        if (dx >= 0) {
            rotationAngle += rotationSpeed;
        } else {
            rotationAngle -= rotationSpeed;
        }

        if (rotationAngle >= 360) rotationAngle -= 360;
        if (rotationAngle < 0) rotationAngle += 360;

        imageView.setRotate(rotationAngle);
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getCenterX() {
        return getX() + radius;
    }

    public double getCenterY() {
        return getY() + radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setCenterX(double x) {
        collisionShape.setCenterX(x);
        imageView.setX(x - radius);
    }

    public void setCenterY(double y) {
        collisionShape.setCenterY(y);
        imageView.setY(y - radius);
    }

    public void reverseX() {
        vx *= -1;
    }

    public void reverseY() {
        vy *= -1;
    }

    public void launch() {
        this.ballLaunched = true;
    }

    public boolean isLaunched() {
        return ballLaunched;
    }

    public Bounds getCollisionBounds() {
        return collisionShape.getBoundsInParent();
    }

    public Circle getCollisionShape() {
        return collisionShape;
    }
}
