package object;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import object.powerup.Laser;

import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

public class Paddle extends GameObject {
    private double speed;
    private double boundary;
    private int lives = DEFAULT_LIVES;

    private boolean movingLeft = false;
    private boolean movingRight = false;

    private Image paddleLeftImage;
    private Image paddleRightImage;
    private Image gunLeftImage;
    private Image gunRightImage;

    private ImageView gunLeftImageView;
    private ImageView gunRightImageView;

    private Rectangle collisionShape;

    private boolean laserEnabled = false;
    private long lastLaserTime = 0;
    private List<Laser> activeLasers = new ArrayList<>();

    public Paddle(String imagePath, double boundary, double x, double y, double width, double height, double speed) {
        super(imagePath, x, y, width, height);
        this.speed = speed;
        this.boundary = boundary;

        this.paddleLeftImage = new Image("file:assets/images/paddle_left.png");
        this.paddleRightImage = new Image("file:assets/images/paddle_right.png");
        this.gunLeftImage = new Image("file:assets/images/gun_left.png");
        this.gunRightImage = new Image("file:assets/images/gun_right.png");

        this.collisionShape = new Rectangle(x, y, width, height);
        this.collisionShape.setVisible(false);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.RED);
        this.collisionShape.setArcWidth(20);
        this.collisionShape.setArcHeight(20);

        this.image = paddleLeftImage;
        this.imageView.setImage(paddleLeftImage);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(false);

        this.gunLeftImageView = new ImageView(gunLeftImage);
        this.gunLeftImageView.setVisible(false);
        this.gunLeftImageView.setFitWidth(GUN_WIDTH);
        this.gunLeftImageView.setFitHeight(GUN_HEIGHT);
        this.gunLeftImageView.setPreserveRatio(false);

        this.gunRightImageView = new ImageView(gunRightImage);
        this.gunRightImageView.setVisible(false);
        this.gunRightImageView.setFitWidth(GUN_WIDTH);
        this.gunRightImageView.setFitHeight(GUN_HEIGHT);
        this.gunRightImageView.setPreserveRatio(false);
    }

    public void moveLeft() {
        setX(Math.max(0, getX() - speed));
    }

    public void moveRight() {
        setX(Math.min(boundary - getWidth(), getX() + speed));
    }

    @Override
    public void update() {
        if (movingLeft) {
            moveLeft();
        }
        if (movingRight) {
            moveRight();
        }

        if (laserEnabled) {
            shootLaser();
        }

        updateLasers();
        updateGuns();
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

    public void updateLasers() {
        activeLasers.removeIf(laser -> !laser.isActive());

        for (Laser laser : activeLasers) {
            laser.update();
        }
    }

    public void updateGuns() {
        this.gunLeftImageView.setX(getCenterX() - getWidth() / 3);
        this.gunLeftImageView.setY(getY() - GUN_HEIGHT + 2);

        this.gunRightImageView.setX(getCenterX() + getWidth() / 3 - GUN_WIDTH);
        this.gunRightImageView.setY(getY() - GUN_HEIGHT + 2);
    }

    private void updateCollisionShape() {
        collisionShape.setX(getX());
        collisionShape.setY(getY());
    }

    public void reset() {
        setX(PADDLE_POS_X);
        setY(PADDLE_POS_Y);
        lives = DEFAULT_LIVES;
        clearLasers();
        setGunVisible(false);
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void gainLife() {
        if (lives < 3) {
            lives++;
        }
    }

    public boolean isOutOfLives() {
        return lives <= 0;
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

    public double getCenterX() {
        return getX() + getWidth() / 2;
    }

    public double getCenterY() {
        return getY() + getHeight() / 2;
    }

    public double getSpeed() {
        return speed;
    }

    public int getLives() {
        return lives;
    }

    public void handleKeyPressed(KeyCode code) {
        if (code == KeyCode.LEFT || code == KeyCode.A) {
            movingLeft = true;
            imageView.setImage(paddleLeftImage);
        }
        if (code == KeyCode.RIGHT || code == KeyCode.D) {
            movingRight = true;
            imageView.setImage(paddleRightImage);
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

    public void enableLaser() {
        this.gunLeftImageView.setVisible(true);
        this.gunRightImageView.setVisible(true);
        this.laserEnabled = true;
    }

    public void disableLaser() {
        this.gunLeftImageView.setVisible(false);
        this.gunRightImageView.setVisible(false);
        this.laserEnabled = false;
    }

    public boolean isLaserEnabled() {
        return laserEnabled;
    }

    public void shootLaser() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastLaserTime < LASER_COOLDOWN) {
            return;
        }
        lastLaserTime = currentTime;

        Laser leftLaser = new Laser(getCenterX() - getWidth() / 3, getY() - LASER_HEIGHT);
        Laser rightLaser = new Laser(getCenterX() + getWidth() / 3 - LASER_WIDTH, getY() - LASER_HEIGHT);
        activeLasers.add(leftLaser);
        activeLasers.add(rightLaser);
    }

    public List<Laser> getActiveLasers() {
        return activeLasers;
    }

    public void clearLasers() {
        activeLasers.clear();
    }

    public void setGunVisible(boolean visible) {
        this.gunLeftImageView.setVisible(visible);
        this.gunRightImageView.setVisible(visible);
    }

    public void addLaserImage(Pane root) {
        for (Laser laser : activeLasers) {
            if (!root.getChildren().contains(laser.getCollisionShape())) {
                root.getChildren().addAll(laser.getImageView(), laser.getCollisionShape());
            }
        }
    }

    public void removeLaserImage(Pane root) {
        for (Laser laser : activeLasers) {
            root.getChildren().removeAll(laser.getImageView(), laser.getCollisionShape());
        }
    }

    public ImageView getGunLeftImageView() {
        return gunLeftImageView;
    }

    public ImageView getGunRightImageView() {
        return gunRightImageView;
    }
}