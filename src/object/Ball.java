package src.object;

import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import static src.utils.Constants.*;

public class Ball extends GameObject {
    private double vx;
    private double vy;
    private double radius;

    private double rotationSpeed = 5;
    private double rotationAngle = 0;

    private boolean ballLaunched = false;
    private boolean outOfBounds = false;

    private Circle collisionShape;

    public Ball(String imagePath, double x, double y, double radius, double vx, double vy) {
        super(imagePath, x, y, radius * 2, radius * 2);
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.ballLaunched = ballLaunched;

        this.collisionShape = new Circle(x + radius, y + radius, radius);
        this.collisionShape.setVisible(false);
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
        if (newX < 0 || newX + getWidth() > SCREEN_WIDTH) {
            vx *= -1;
            if (newX < 0) {
                newX = 0;
            } else {
                newX = SCREEN_WIDTH - getWidth();
            }
        }

        // Collide with upper boundary
        if (newY < 0) {
            vy *= -1;
            newY = 0;
        }

        // Collide with bottom boundary
        if (newY + getHeight() > SCREEN_HEIGHT) {
            outOfBounds = true;
        }

        setX(newX);
        setY(newY);

        updateCollisionShape();
    }

    public boolean hitLeftBound() {
        return getX() + vx < 0;
    }

    public boolean hitRightBound() {
        return getX() + vx + getWidth() > SCREEN_WIDTH;
    }

    public boolean hitUpperBound() {
        return getY() + vy < 0;
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

    public double getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(double rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void resetRotationSpeed() {
        this.rotationSpeed = 5;
    }

    public void reset(double x, double y) {
        setCenterX(x + radius);
        setCenterY(y + radius);
        setVx(BALL_VX);
        setVy(BALL_VY);
        imageView.setRotate(0);
        rotationAngle = 0;
        ballLaunched = false;
        outOfBounds = false;
    }

    public void reverseX() {
        vx *= -1;
    }

    public void reverseY() {
        vy *= -1;
    }

    public void launch() {
        ballLaunched = true;
    }

    public void notLaunch() {
        ballLaunched = false;
    }

    public boolean isLaunched() {
        return ballLaunched;
    }

    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    public Bounds getCollisionBounds() {
        return collisionShape.getBoundsInParent();
    }

    public Circle getCollisionShape() {
        return collisionShape;
    }
}