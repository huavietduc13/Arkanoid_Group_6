package object;

import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;

public class Ball extends GameObject {
    private double dx;
    private double dy;
    private double radius;

    private boolean ballLaunched = false;

    public Ball(String imagePath, double x, double y, double radius, double dx, double dy) {
        super(imagePath, x, y, radius * 2, radius * 2);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        if (!ballLaunched) {
            return;
        }

        double newX = getX() + dx;
        double newY = getY() + dy;

        // Va chạm biên trái/phải
        if (newX < 0 || newX + getWidth() > 400) {
            dx *= -1;
            if (newX < 0) {
                newX = 0;
            } else {
                newX = 400 - getWidth();
            }
        }

        // Va chạm biên trên
        if (newY < 0) {
            dy *= -1;
            newY = 0;
        }

        // Va chạm biên dưới
        if (newY + getHeight() > 600) {
            dy *= -1;
            newY = 600 - getHeight();
        }

        setX(newX);
        setY(newY);
    }

    public boolean intersects(GameObject other) {
        Bounds ballBounds  = this.getImageView().getBoundsInParent();
        Bounds otherBounds = this.getImageView().getBoundsInParent();

        return ballBounds.intersects(otherBounds);
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getXCenter() {
        return getX() + radius;
    }

    public double getYCenter() {
        return getY() + radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public void launch() {
        this.ballLaunched = true;
    }

    public boolean isLaunched() {
        return ballLaunched;
    }
}
