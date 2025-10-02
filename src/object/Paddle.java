package object;

import javafx.scene.input.KeyCode;

public class Paddle extends GameObject {
    private double speed;
    private double boundary;

    private boolean movingLeft = false;
    private boolean movingRight = false;

    public Paddle(String imagePath, double boundary, double x, double y, double width, double height, double speed) {
        super(imagePath, x, y, width, height);
        this.speed = speed;
        this.boundary = boundary;
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
